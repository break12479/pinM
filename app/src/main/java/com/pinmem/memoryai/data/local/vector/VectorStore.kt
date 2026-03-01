package com.pinmem.pinm.data.local.vector

/**
 * 向量存储接口
 *
 * 定义向量存储的基本操作
 */
interface VectorStore {
    /**
     * 添加向量
     */
    suspend fun addEmbedding(
        memoryId: Long,
        embedding: FloatArray,
        metadata: Map<String, Any?>
    )

    /**
     * 删除向量
     */
    suspend fun deleteEmbedding(memoryId: Long)

    /**
     * 相似度搜索
     */
    suspend fun searchSimilar(
        queryEmbedding: FloatArray,
        topK: Int = 10,
        filter: ((Map<String, Any?>) -> Boolean)? = null
    ): List<SearchResult>

    /**
     * 获取向量数量
     */
    suspend fun getCount(): Int

    /**
     * 清空所有向量
     */
    suspend fun clear()

    /**
     * 搜索结果
     */
    data class SearchResult(
        val memoryId: Long,
        val metadata: Map<String, Any?>,
        val score: Float
    )
}
