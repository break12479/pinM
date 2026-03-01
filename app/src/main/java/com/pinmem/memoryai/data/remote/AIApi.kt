package com.pinmem.memoryai.data.remote

import com.pinmem.memoryai.data.remote.model.ChatCompletionRequest
import com.pinmem.memoryai.data.remote.model.ChatCompletionResponse
import com.pinmem.memoryai.data.remote.model.EmbeddingRequest
import com.pinmem.memoryai.data.remote.model.EmbeddingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * AI API 接口定义
 *
 * 使用 Retrofit 定义标准的 OpenAI 兼容 API 接口
 * 支持 DeepSeek、OpenAI 等服务商
 */
interface AIApi {

    /**
     * 生成文本嵌入向量
     *
     * @param authorization Bearer Token 认证头
     * @param request 嵌入请求
     * @return 嵌入响应
     */
    @POST("v1/embeddings")
    suspend fun getEmbeddings(
        @Header("Authorization") authorization: String,
        @Body request: EmbeddingRequest
    ): Response<EmbeddingResponse>

    /**
     * 聊天完成（LLM 调用）
     *
     * @param authorization Bearer Token 认证头
     * @param request 聊天完成请求
     * @return 聊天完成响应
     */
    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>
}
