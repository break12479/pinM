package com.pinmem.memoryai.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 聊天完成请求
 *
 * @param model LLM 模型名称（如 "deepseek-chat"）
 * @param messages 消息列表
 * @param temperature 温度参数（0.0-2.0）
 * @param maxTokens 最大输出 Token 数
 * @param stream 是否流式输出
 */
@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Float = 0.7f,
    @SerialName("max_tokens")
    val maxTokens: Int = 500,
    val stream: Boolean = false
)

/**
 * 聊天消息
 *
 * @param role 角色："system" | "user" | "assistant"
 * @param content 消息内容
 */
@Serializable
data class Message(
    val role: String,
    val content: String
) {
    companion object {
        fun system(content: String) = Message("system", content)
        fun user(content: String) = Message("user", content)
        fun assistant(content: String) = Message("assistant", content)
    }
}

/**
 * 聊天完成响应
 *
 * @param id 响应 ID
 * @param choices 选择列表
 * @param usage Token 使用信息
 * @param model 使用的模型
 */
@Serializable
data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?,
    val model: String? = null,
    val created: Long = 0
)

/**
 * 聊天选择
 *
 * @param index 选择索引
 * @param message AI 返回的消息
 * @param finishReason 结束原因
 */
@Serializable
data class Choice(
    val index: Int = 0,
    val message: Message,
    @SerialName("finish_reason")
    val finishReason: String? = null
)
