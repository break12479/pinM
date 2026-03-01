package com.pinmem.memoryai.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * AI 配置实体
 * 
 * @param id 配置 ID
 * @param provider 服务提供商："deepseek" | "openai" | "custom"
 * @param apiKey API Key
 * @param baseUrl 自定义 endpoint
 * @param embeddingModel 嵌入模型："bge-m3"
 * @param llmModel LLM 模型："deepseek-chat"
 * @param isActive 是否启用
 */
@Entity(tableName = "ai_config")
data class AIConfig(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "provider")
    val provider: String = "deepseek",

    @ColumnInfo(name = "api_key")
    val apiKey: String = "",

    @ColumnInfo(name = "base_url")
    val baseUrl: String? = null,

    @ColumnInfo(name = "embedding_model")
    val embeddingModel: String = "bge-m3",

    @ColumnInfo(name = "llm_model")
    val llmModel: String = "deepseek-chat",

    @ColumnInfo(name = "is_active")
    val isActive: Int = 1
) {
    /**
     * 获取完整的 API Base URL
     */
    fun resolveBaseUrl(): String {
        // 如果配置中指定了 baseUrl，直接使用
        if (!baseUrl.isNullOrBlank()) {
            return baseUrl
        }
        
        // 否则根据 provider 使用默认 URL
        return when (provider) {
            "dashscope" -> "https://dashscope.aliyuncs.com/compatible-mode/v1"
            "deepseek" -> "https://api.deepseek.com"
            "openai" -> "https://api.openai.com"
            else -> "https://dashscope.aliyuncs.com/compatible-mode/v1"
        }
    }

    /**
     * 检查配置是否有效
     */
    fun isValid(): Boolean = apiKey.isNotBlank()
}
