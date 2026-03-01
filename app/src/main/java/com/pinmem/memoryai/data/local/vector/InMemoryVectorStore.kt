package com.pinmem.pinm.data.local.vector

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存向量存储（简化版，用于 sqlite-vec 集成前的临时方案）
 *
 * 使用余弦相似度进行向量检索
 */
class InMemoryVectorStore : VectorStore {

    // 存储向量：memoryId -> embedding
    private val embeddings = ConcurrentHashMap<Long, FloatArray>()

    // 存储元数据：memoryId -> metadata
    private val metadataMap = ConcurrentHashMap<Long, Map<String, Any?>>()

    /**
     * 添加向量
     */
    override suspend fun addEmbedding(
        memoryId: Long,
        embedding: FloatArray,
        metadata: Map<String, Any?>
    ): Unit = withContext(Dispatchers.IO) {
        embeddings[memoryId] = embedding
        metadataMap[memoryId] = metadata
    }

    /**
     * 删除向量
     */
    override suspend fun deleteEmbedding(memoryId: Long): Unit = withContext(Dispatchers.IO) {
        embeddings.remove(memoryId)
        metadataMap.remove(memoryId)
    }

    /**
     * 相似度搜索（余弦相似度）
     */
    override suspend fun searchSimilar(
        queryEmbedding: FloatArray,
        topK: Int,
        filter: ((Map<String, Any?>) -> Boolean)?
    ): List<VectorStore.SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<VectorStore.SearchResult>()

        for ((memoryId, embedding) in embeddings) {
            val meta = metadataMap[memoryId] ?: continue

            // 应用过滤
            if (filter != null && !filter(meta)) continue

            // 计算余弦相似度
            val similarity = cosineSimilarity(queryEmbedding, embedding)
            results.add(VectorStore.SearchResult(memoryId, meta, similarity))
        }

        // 按相似度排序并返回 topK
        results.sortByDescending { it.score }
        results.take(topK)
    }

    /**
     * 计算余弦相似度
     */
    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return 0f

        var dotProduct = 0f
        var normA = 0f
        var normB = 0f

        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }

        return if (normA > 0 && normB > 0) {
            dotProduct / (kotlin.math.sqrt(normA) * kotlin.math.sqrt(normB))
        } else {
            0f
        }
    }

    /**
     * 获取向量数量
     */
    override suspend fun getCount(): Int = withContext(Dispatchers.IO) {
        embeddings.size
    }

    /**
     * 清空所有向量
     */
    override suspend fun clear(): Unit = withContext(Dispatchers.IO) {
        embeddings.clear()
        metadataMap.clear()
    }
}
