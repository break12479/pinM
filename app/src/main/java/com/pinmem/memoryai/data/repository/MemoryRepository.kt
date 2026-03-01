package com.pinmem.memoryai.data.repository

import com.pinmem.memoryai.data.local.database.MemoryDao
import com.pinmem.memoryai.data.local.vector.InMemoryVectorStore
import com.pinmem.memoryai.data.model.Memory
import com.pinmem.memoryai.data.model.LocationInfo
import com.pinmem.memoryai.data.remote.AIApiService
import com.pinmem.memoryai.data.service.AIException
import com.pinmem.memoryai.data.remote.MemoryContext
import com.pinmem.memoryai.util.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 记忆记录 Repository
 */
class MemoryRepository(
    private val memoryDao: MemoryDao,
    private val vectorStore: InMemoryVectorStore,
    private val aiApiService: AIApiService,
    private val aiConfigRepository: AIConfigRepository,
    private val tagRepository: TagRepository,
    private val applicationScope: CoroutineScope
) {

    /**
     * 获取所有记忆（Flow）
     */
    fun getAllMemories(): Flow<List<Memory>> = memoryDao.getAllMemories()

    /**
     * 分页获取记忆
     */
    suspend fun getMemoriesPaged(page: Int, pageSize: Int = 20): List<Memory> {
        return memoryDao.getMemoriesPaged(pageSize, (page - 1) * pageSize)
    }

    /**
     * 根据 ID 获取记忆
     */
    suspend fun getMemoryById(id: Long): Memory? = memoryDao.getMemoryById(id)

    /**
     * 根据分类获取记忆
     */
    fun getMemoriesByCategory(category: String): Flow<List<Memory>> =
        memoryDao.getMemoriesByCategory(category)

    /**
     * 根据 Tag 获取记忆
     */
    fun getMemoriesByTag(tag: String): Flow<List<Memory>> =
        memoryDao.getMemoriesByTag("%\"$tag\"%")

    /**
     * 关键词搜索
     */
    suspend fun searchByKeyword(query: String): List<Memory> =
        memoryDao.searchByKeyword("%$query%")

    /**
     * 创建记忆记录
     */
    suspend fun createMemory(
        content: String,
        location: LocationInfo? = null
    ): Result<Memory> = try {
        val memory = Memory(
            content = content,
            locationLat = location?.latitude,
            locationLng = location?.longitude,
            locationAddress = location?.address
        )

        val id = memoryDao.insert(memory)
        val savedMemory = memory.copy(id = id)

        // 异步处理 AI 任务
        processAIAsync(savedMemory)

        Result.success(savedMemory)
    } catch (e: Exception) {
        AppLogger.e("Create memory failed", e)
        Result.failure(e)
    }

    /**
     * 更新记忆记录
     */
    suspend fun updateMemory(
        id: Long,
        content: String,
        location: LocationInfo? = null
    ): Result<Unit> {
        return try {
            val existing = memoryDao.getMemoryById(id)
                ?: return Result.failure(Exception("Memory not found"))

            val updated = existing.copy(
                content = content,
                locationLat = location?.latitude ?: existing.locationLat,
                locationLng = location?.longitude ?: existing.locationLng,
                locationAddress = location?.address ?: existing.locationAddress,
                updatedAt = System.currentTimeMillis(),
                aiProcessed = 0,  // 重置 AI 处理状态
                embeddingPending = 1
            )

            memoryDao.update(updated)

            // 异步重新处理 AI
            processAIAsync(updated)

            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.e("Update memory failed", e)
            Result.failure(e)
        }
    }

    /**
     * 删除记忆（软删除）
     */
    suspend fun deleteMemory(id: Long): Result<Unit> = try {
        memoryDao.softDelete(id)
        vectorStore.deleteEmbedding(id)
        Result.success(Unit)
    } catch (e: Exception) {
        AppLogger.e("Delete memory failed", e)
        Result.failure(e)
    }

    /**
     * 语义搜索
     * 优化：根据相似度阈值动态调整检索结果
     */
    suspend fun semanticSearch(query: String, limit: Int = 20): List<Memory> {
        val config = aiConfigRepository.getActiveConfig()
            ?: return searchByKeyword(query)  // 降级到关键词搜索

        return try {
            // 生成查询向量
            val embedding = aiApiService.getEmbedding(config, query)

            // 向量搜索（获取更多候选，然后过滤）
            val allResults = vectorStore.searchSimilar(embedding, topK = limit * 2)

            // 根据相似度阈值过滤
            val HIGH_SIMILARITY_THRESHOLD = 0.7f  // 高相似度阈值
            val MIN_SIMILARITY_THRESHOLD = 0.4f   // 最低相似度阈值
            
            val highQualityResults = allResults.filter { it.score >= HIGH_SIMILARITY_THRESHOLD }
            val filteredResults = if (highQualityResults.isEmpty()) {
                // 如果没有高相似度结果，使用最低阈值过滤
                allResults.filter { it.score >= MIN_SIMILARITY_THRESHOLD }
                    .sortedByDescending { it.score }
                    .take(limit)
            } else {
                // 有高相似度结果，只保留高相似度的（最多 limit 条）
                highQualityResults
                    .sortedByDescending { it.score }
                    .take(limit)
            }

            AppLogger.d("MemoryRepository", "Semantic search: total=${allResults.size}, highQuality=${highQualityResults.size}, final=${filteredResults.size}")

            // 获取完整的 Memory 对象
            filteredResults.mapNotNull { result ->
                memoryDao.getMemoryById(result.memoryId)
            }
        } catch (e: AIException.NotConfigured) {
            // 没有 AI 配置，直接返回空列表
            AppLogger.w("No AI config for semantic search")
            emptyList()
        } catch (e: AIException.NetworkError) {
            // 网络错误，降级到关键词搜索
            AppLogger.w("Network error in semantic search, fallback to keyword search", e)
            searchByKeyword(query)
        } catch (e: Exception) {
            // 其他错误，记录日志并降级
            AppLogger.e("Semantic search failed, fallback to keyword search", e)
            searchByKeyword(query)
        }
    }

    /**
     * 异步处理 AI 任务
     */
    private fun processAIAsync(memory: Memory) {
        applicationScope.launch(Dispatchers.IO) {
            try {
                AppLogger.i("MemoryRepository", "Starting AI processing for memory ${memory.id}")
                
                val config = aiConfigRepository.getActiveConfig()
                    ?: run {
                        AppLogger.w("No AI config found, skipping AI processing", null)
                        return@launch
                    }

                AppLogger.d("MemoryRepository", "AI config found: ${config.provider}")

                // 1. AI 分类
                AppLogger.d("MemoryRepository", "Starting classification...")
                val classification = aiApiService.classify(config, memory.content)
                AppLogger.d("MemoryRepository", "Classification result: ${classification.sceneCategory} / ${classification.typeCategory}")

                // 2. Tag 提取
                AppLogger.d("MemoryRepository", "Starting tag extraction...")
                val existingTags = tagRepository.getAllTagsOnce().map { it.name }
                AppLogger.d("MemoryRepository", "Existing tags: $existingTags")
                val tags = aiApiService.extractTags(config, memory.content, existingTags)
                AppLogger.d("MemoryRepository", "✅ Extracted tags: $tags")

                // 3. 更新记忆
                val updated = memory.copy(
                    sceneCategory = classification.sceneCategory,
                    typeCategory = classification.typeCategory,
                    tagsJson = memory.toTagsJson(tags),
                    aiProcessed = 1,
                    embeddingPending = 1  // 保持 pending，等待向量生成
                )
                memoryDao.update(updated)
                AppLogger.d("MemoryRepository", "✅ Memory updated with classification and tags")

                // 4. 更新 Tag 使用次数
                tags.forEach { tagRepository.incrementUsage(it) }

                // 5. 生成向量嵌入
                AppLogger.d("MemoryRepository", "🔢 Generating embedding (model=${config.embeddingModel})...")
                val embedding = aiApiService.getEmbedding(config, memory.content)
                AppLogger.d("MemoryRepository", "✅ Embedding generated, size=${embedding.size}, first5=${embedding.take(5).joinToString()}")

                // 6. 保存到向量存储
                AppLogger.d("MemoryRepository", "💾 Saving to vector store...")
                vectorStore.addEmbedding(
                    memoryId = memory.id,
                    embedding = embedding,
                    metadata = mapOf(
                        "memoryId" to memory.id,
                        "content" to memory.content,
                        "sceneCategory" to classification.sceneCategory,
                        "typeCategory" to classification.typeCategory,
                        "tags" to tags,
                        "createdAt" to memory.createdAt
                    )
                )
                AppLogger.d("MemoryRepository", "✅ Vector saved to InMemoryVectorStore, count=${vectorStore.getCount()}")

                // 7. 更新数据库中的向量
                AppLogger.d("MemoryRepository", "🗄️ Saving embedding to database...")
                val embeddingBytes = embedding.map { it.toBits().toByte() }.toByteArray()
                memoryDao.updateEmbedding(memory.id, embeddingBytes, config.embeddingModel)
                AppLogger.d("MemoryRepository", "✅ Vector saved to database")

                // 8. 标记为已完成
                memoryDao.updateAIStatus(memory.id, processed = 1, pending = 0)
                AppLogger.i("MemoryRepository", "🎉 AI processing COMPLETED for memory ${memory.id}")
            } catch (apiError: Exception) {
                AppLogger.e("MemoryRepository", "❌ AI API error: ${apiError.message}", apiError)
                // API 调用失败，标记为已完成（但无向量）
                memoryDao.updateAIStatus(memory.id, processed = 1, pending = 0)
                AppLogger.w("Marked as completed but without embedding (API error)")
            } catch (e: Exception) {
                AppLogger.e("MemoryRepository", "❌ Unexpected error in processAIAsync", e)
                // 失败后重置状态，显示为已完成（但无向量）
                memoryDao.updateAIStatus(memory.id, processed = 1, pending = 0)
            }
        }
    }

    /**
     * 获取记忆数量
     */
    suspend fun getCount(): Int = memoryDao.getCount()

    /**
     * 获取所有分类
     */
    suspend fun getAllCategories(): List<String> = memoryDao.getAllCategories()

    /**
     * 构建记忆上下文（用于 RAG）
     */
    private fun buildMemoryContext(memories: List<Memory>): List<MemoryContext> {
        return memories.map { mem ->
            MemoryContext(
                id = mem.id,
                content = mem.content,
                createdAt = mem.createdAt,
                tags = mem.getTags(),
                sceneCategory = mem.sceneCategory
            )
        }
    }
}
