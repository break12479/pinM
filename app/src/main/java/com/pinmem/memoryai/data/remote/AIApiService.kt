package com.pinmem.pinm.data.remote

import com.pinmem.pinm.data.model.AIConfig

/**
 * AI API 服务接口
 */
interface AIApiService {

    /**
     * 生成文本嵌入向量
     * 
     * @param config AI 配置
     * @param text 输入文本
     * @return 嵌入向量
     */
    suspend fun getEmbedding(config: AIConfig, text: String): FloatArray

    /**
     * 文本分类
     * 
     * @param config AI 配置
     * @param content 记录内容
     * @return 分类结果
     */
    suspend fun classify(config: AIConfig, content: String): ClassificationResult

    /**
     * 提取标签
     * 
     * @param config AI 配置
     * @param content 记录内容
     * @param existingTags 已有标签列表
     * @return 提取的标签列表
     */
    suspend fun extractTags(config: AIConfig, content: String, existingTags: List<String>): List<String>

    /**
     * 问答
     * 
     * @param config AI 配置
     * @param question 用户问题
     * @param context 相关记忆上下文
     * @return 问答结果
     */
    suspend fun answerQuestion(
        config: AIConfig,
        question: String,
        context: List<MemoryContext>
    ): AnswerResult
}

/**
 * 分类结果
 */
data class ClassificationResult(
    val sceneCategory: String,
    val typeCategory: String,
    val confidence: Float
)

/**
 * 记忆上下文（用于 RAG）
 */
data class MemoryContext(
    val id: Long,
    val content: String,
    val createdAt: Long,
    val tags: List<String>,
    val sceneCategory: String
)

/**
 * 问答结果
 */
data class AnswerResult(
    val answer: String,
    val referencedIds: List<Long>,
    val confidence: Float
)
