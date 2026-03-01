package com.pinmem.pinm.data.repository

import com.pinmem.pinm.data.local.database.MemoryDao
import com.pinmem.pinm.data.local.database.SearchHistoryDao
import com.pinmem.pinm.data.local.vector.InMemoryVectorStore
import com.pinmem.pinm.data.model.Memory
import com.pinmem.pinm.data.model.SearchHistory
import com.pinmem.pinm.data.model.SearchResult
import com.pinmem.pinm.data.remote.AIApiService
import com.pinmem.pinm.util.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * 搜索 Repository
 *
 * 提供语义搜索和关键词搜索功能
 */
class SearchRepository(
    private val memoryDao: MemoryDao,
    private val vectorStore: InMemoryVectorStore,
    private val aiApiService: AIApiService,
    private val aiConfigRepository: AIConfigRepository,
    private val searchHistoryDao: SearchHistoryDao
) {

    companion object {
        private const val TAG = "SearchRepository"

        // 搜索权重配置
        private const val VECTOR_WEIGHT = 0.7f      // 向量相似度权重
        private const val TAG_WEIGHT = 0.2f         // Tag 匹配权重
        private const val TIME_WEIGHT = 0.1f        // 时间新鲜度权重
    }

    /**
     * 语义搜索
     */
    suspend fun semanticSearch(
        query: String,
        limit: Int = 20
    ): Result<List<SearchResult>> = withContext(Dispatchers.IO) {
        try {
            AppLogger.d(TAG, "🔍 Starting semantic search for: $query")
            
            val config = aiConfigRepository.getActiveConfig()
                ?: return@withContext Result.failure(Exception("AI 配置未设置"))

            AppLogger.d(TAG, "✅ AI config found: ${config.provider}, model=${config.embeddingModel}")

            // 1. 生成查询向量
            AppLogger.d(TAG, "🔢 Generating query embedding...")
            val queryEmbedding = aiApiService.getEmbedding(config, query)
            AppLogger.d(TAG, "✅ Query embedding generated, size=${queryEmbedding.size}")

            // 2. 向量相似度搜索
            AppLogger.d(TAG, "💾 Searching vector store, topK=${limit * 2}")
            val vectorCount = vectorStore.getCount()
            AppLogger.d(TAG, "📊 Vector store count: $vectorCount")
            
            val vectorResults = vectorStore.searchSimilar(queryEmbedding, topK = limit * 2)
            AppLogger.d(TAG, "✅ Vector search returned ${vectorResults.size} results")
            
            if (vectorResults.isEmpty()) {
                AppLogger.w("No vector results, falling back to keyword search")
                return@withContext keywordSearch(query, limit)
            }

            // 3. 获取完整的 Memory 对象并计算综合分数
            val results = vectorResults.mapNotNull { vectorResult ->
                val memory = memoryDao.getMemoryById(vectorResult.memoryId) ?: return@mapNotNull null

                // 计算 Tag 匹配分数
                val tagScore = calculateTagScore(memory, query)

                // 计算时间新鲜度分数
                val timeScore = calculateTimeScore(memory)

                // 综合分数
                val finalScore = VECTOR_WEIGHT * vectorResult.score +
                        TAG_WEIGHT * tagScore +
                        TIME_WEIGHT * timeScore

                SearchResult(
                    memory = memory,
                    score = finalScore
                )
            }

            // 4. 按综合分数排序并返回
            val sortedResults = results.sortedByDescending { it.score }.take(limit)

            AppLogger.d(TAG, "Semantic search returned ${sortedResults.size} results")
            Result.success(sortedResults)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Semantic search failed, fallback to keyword search", e)
            keywordSearch(query, limit)
        }
    }

    /**
     * 关键词搜索
     *
     * @param query 搜索查询
     * @param limit 返回数量
     * @return 搜索结果
     */
    suspend fun keywordSearch(
        query: String,
        limit: Int = 20
    ): Result<List<SearchResult>> = withContext(Dispatchers.IO) {
        try {
            val memories = memoryDao.searchByKeyword("%$query%")

            val results = memories.map { memory ->
                // 计算关键词匹配分数
                val keywordScore = calculateKeywordScore(memory.content, query)
                val tagScore = calculateTagScore(memory, query)
                val timeScore = calculateTimeScore(memory)

                val finalScore = keywordScore * 0.6f + tagScore * 0.3f + timeScore * 0.1f

                SearchResult(
                    memory = memory,
                    score = finalScore
                )
            }

            Result.success(results.sortedByDescending { it.score }.take(limit))
        } catch (e: Exception) {
            AppLogger.e(TAG, "Keyword search failed", e)
            Result.failure(e)
        }
    }

    /**
     * 混合搜索（语义 + 关键词）
     *
     * @param query 搜索查询
     * @param limit 返回数量
     * @param semanticWeight 语义搜索权重（0-1）
     * @return 搜索结果
     */
    suspend fun hybridSearch(
        query: String,
        limit: Int = 20,
        semanticWeight: Float = 0.7f
    ): Result<List<SearchResult>> = withContext(Dispatchers.IO) {
        try {
            // 并行执行语义搜索和关键词搜索
            val semanticResult = semanticSearch(query, limit * 2)
            val keywordResult = keywordSearch(query, limit * 2)

            val semanticMemories = semanticResult.getOrNull()?.associateBy { it.memory.id } ?: emptyMap()
            val keywordMemories = keywordResult.getOrNull()?.associateBy { it.memory.id } ?: emptyMap()

            // 合并结果
            val allMemoryIds = (semanticMemories.keys + keywordMemories.keys).toSet()

            val mergedResults = allMemoryIds.mapNotNull { id ->
                val semanticScore = semanticMemories[id]?.score ?: 0f
                val keywordScore = keywordMemories[id]?.score ?: 0f
                val memory = semanticMemories[id]?.memory ?: keywordMemories[id]?.memory ?: return@mapNotNull null

                // 加权平均
                val finalScore = semanticWeight * semanticScore + (1 - semanticWeight) * keywordScore

                SearchResult(
                    memory = memory,
                    score = finalScore
                )
            }

            Result.success(mergedResults.sortedByDescending { it.score }.take(limit))
        } catch (e: Exception) {
            AppLogger.e(TAG, "Hybrid search failed", e)
            Result.failure(e)
        }
    }

    /**
     * 按 Tag 搜索
     *
     * @param tag Tag 名称
     * @return 记忆列表
     */
    fun searchByTag(tag: String): Flow<List<Memory>> {
        return memoryDao.getMemoriesByTag("%\"$tag\"%")
    }

    /**
     * 按分类搜索
     *
     * @param category 分类名称
     * @return 记忆列表
     */
    fun searchByCategory(category: String): Flow<List<Memory>> {
        return memoryDao.getMemoriesByCategory(category)
    }

    /**
     * 高级搜索
     *
     * @param query 搜索查询
     * @param categories 分类过滤
     * @param tags Tag 过滤
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 返回数量
     * @return 搜索结果
     */
    suspend fun advancedSearch(
        query: String? = null,
        categories: List<String>? = null,
        tags: List<String>? = null,
        startTime: Long? = null,
        endTime: Long? = null,
        limit: Int = 50
    ): Result<List<SearchResult>> = withContext(Dispatchers.IO) {
        try {
            // 1. 获取基础结果
            val memories = when {
                query != null && query.isNotBlank() -> {
                    // 有查询文本，使用语义搜索
                    semanticSearch(query, limit * 2).getOrNull() ?: emptyList()
                }
                else -> {
                    // 无查询文本，获取所有记忆
                    memoryDao.getMemoriesPaged(limit * 2, 0).map { memory ->
                        SearchResult(memory, 0.5f)
                    }
                }
            }

            // 2. 应用过滤
            val filteredResults = memories.filter { result ->
                val memory = result.memory

                // 分类过滤
                if (categories != null && categories.isNotEmpty()) {
                    if (memory.sceneCategory !in categories && memory.typeCategory !in categories) {
                        return@filter false
                    }
                }

                // Tag 过滤
                if (tags != null && tags.isNotEmpty()) {
                    val memoryTags = memory.getTags()
                    if (tags.none { it in memoryTags }) {
                        return@filter false
                    }
                }

                // 时间过滤
                if (startTime != null && memory.createdAt < startTime) {
                    return@filter false
                }
                if (endTime != null && memory.createdAt > endTime) {
                    return@filter false
                }

                true
            }

            Result.success(filteredResults.sortedByDescending { it.score }.take(limit))
        } catch (e: Exception) {
            AppLogger.e(TAG, "Advanced search failed", e)
            Result.failure(e)
        }
    }

    /**
     * 计算 Tag 匹配分数
     */
    private fun calculateTagScore(memory: Memory, query: String): Float {
        val tags = memory.getTags()
        if (tags.isEmpty()) return 0f

        val queryLower = query.lowercase()

        // 检查是否有 Tag 匹配查询
        val matchCount = tags.count { tag ->
            tag.lowercase().contains(queryLower) || queryLower.contains(tag.lowercase())
        }

        return if (matchCount > 0) {
            matchCount.toFloat() / tags.size
        } else {
            0f
        }
    }

    /**
     * 计算时间新鲜度分数
     * 越近的记录分数越高
     */
    private fun calculateTimeScore(memory: Memory): Float {
        val now = System.currentTimeMillis()
        val ageDays = (now - memory.createdAt) / (1000 * 60 * 60 * 24)

        // 指数衰减：当天 1.0, 7 天后 0.5, 30 天后 0.1
        return kotlin.math.exp(-0.1 * ageDays).toFloat()
    }

    /**
     * 计算关键词匹配分数
     */
    private fun calculateKeywordScore(content: String, query: String): Float {
        val contentLower = content.lowercase()
        val queryLower = query.lowercase()

        // 完全匹配
        if (contentLower.contains(queryLower)) {
            return 1.0f
        }

        // 分词匹配（简单实现：按空格分词）
        val queryWords = queryLower.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (queryWords.isEmpty()) return 0f

        val matchCount = queryWords.count { word ->
            contentLower.contains(word)
        }

        return matchCount.toFloat() / queryWords.size
    }

    /**
     * 获取搜索建议
     *
     * @param query 输入查询
     * @param limit 建议数量
     * @return 建议列表
     */
    suspend fun getSearchSuggestions(query: String, limit: Int = 5): List<String> = withContext(Dispatchers.IO) {
        try {
            val suggestions = mutableSetOf<String>()

            // 从 Tag 中获取建议
            val allTags = memoryDao.getAllTags()
            val queryLower = query.lowercase()

            val matchingTags = allTags
                .filter { it.lowercase().contains(queryLower) }
                .take(limit)

            suggestions.addAll(matchingTags)

            // 从分类中获取建议
            val categories = memoryDao.getAllCategories()
            val matchingCategories = categories
                .filter { it.lowercase().contains(queryLower) }
                .take(limit - suggestions.size)

            suggestions.addAll(matchingCategories)

            suggestions.toList()
        } catch (e: Exception) {
            AppLogger.e(TAG, "Get search suggestions failed", e)
            emptyList()
        }
    }

    // ==================== 搜索历史功能 ====================

    /**
     * 获取搜索历史
     *
     * @param limit 返回数量（默认 20）
     * @return 搜索历史 Flow
     */
    fun getSearchHistory(limit: Int = 20): Flow<List<SearchHistory>> {
        return searchHistoryDao.getRecentHistory(limit)
    }

    /**
     * 保存搜索历史
     *
     * @param query 搜索查询
     * @param resultCount 结果数量
     * @param searchType 搜索类型
     * @param filterTags 过滤 Tag
     * @param filterCategory 过滤分类
     */
    suspend fun saveSearchHistory(
        query: String,
        resultCount: Int = 0,
        searchType: String = "semantic",
        filterTags: List<String>? = null,
        filterCategory: String? = null
    ) = withContext(Dispatchers.IO) {
        try {
            val history = SearchHistory(
                query = query,
                resultCount = resultCount,
                searchType = searchType,
                filterTags = filterTags?.joinToString(","),
                filterCategory = filterCategory
            )
            searchHistoryDao.insert(history)
            AppLogger.d(TAG, "Saved search history: $query")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to save search history", e)
        }
    }

    /**
     * 删除搜索历史
     *
     * @param id 历史 ID
     */
    suspend fun deleteSearchHistory(id: Long) {
        searchHistoryDao.deleteById(id)
    }

    /**
     * 清除所有搜索历史
     */
    suspend fun clearSearchHistory() {
        searchHistoryDao.clearAll()
    }

    /**
     * 按 Tag 筛选搜索
     */
    suspend fun searchByTags(
        tags: List<String>,
        limit: Int = 20
    ): Result<List<Memory>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 构建 Tag 查询条件
            val tagQuery = tags.joinToString("", "[", "]") { "\"$it\"" }
            val memories = memoryDao.getMemoriesByTag("%$tagQuery%")
                .first()  // 获取 Flow 的第一个值

            Result.success(memories.take(limit))
        } catch (e: Exception) {
            AppLogger.e(TAG, "Search by tags failed", e)
            Result.failure(e)
        }
    }
}
