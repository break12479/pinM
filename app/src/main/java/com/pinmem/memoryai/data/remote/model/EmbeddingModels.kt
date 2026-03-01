package com.pinmem.pinm.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 嵌入请求
 *
 * @param model 嵌入模型名称（如 "bge-m3"）
 * @param input 要嵌入的文本
 * @param dimensions 向量维度（默认 1024）
 */
@Serializable
data class EmbeddingRequest(
    val model: String,
    val input: String,
    val dimensions: Int = 1024
)

/**
 * 嵌入响应
 *
 * @param data 嵌入数据列表
 * @param usage Token 使用信息
 */
@Serializable
data class EmbeddingResponse(
    val data: List<EmbeddingData>,
    val usage: Usage?
)

/**
 * 嵌入数据
 *
 * @param embedding 向量嵌入（Double 精度）
 * @param index 索引位置
 */
@Serializable
data class EmbeddingData(
    val embedding: List<Double>,
    val index: Int = 0
) {
    /**
     * 转换为 Float 数组
     */
    fun toFloatArray(): FloatArray = embedding.map { it.toFloat() }.toFloatArray()
}

/**
 * Token 使用统计
 *
 * @param promptTokens 输入 Token 数
 * @param totalTokens 总 Token 数
 */
@Serializable
data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int = 0,
    @SerialName("total_tokens")
    val totalTokens: Int = 0,
    @SerialName("completion_tokens")
    val completionTokens: Int = 0
)
