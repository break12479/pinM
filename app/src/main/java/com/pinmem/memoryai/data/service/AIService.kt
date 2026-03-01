package com.pinmem.memoryai.data.service

import com.pinmem.memoryai.data.model.AIConfig
import com.pinmem.memoryai.data.remote.AIApi
import com.pinmem.memoryai.data.remote.model.ChatCompletionRequest
import com.pinmem.memoryai.data.remote.model.EmbeddingRequest
import com.pinmem.memoryai.data.remote.model.Message
import com.pinmem.memoryai.util.AppLogger
import com.pinmem.memoryai.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * AI 服务接口
 *
 * 提供高层 AI 能力抽象，包括分类、Tag 提取、向量嵌入生成等
 */
interface AIService {

    /**
     * AI 分类记忆
     *
     * @param content 记录内容
     * @return 分类结果（场景分类、类型分类、置信度）
     */
    suspend fun classifyMemory(content: String): Result<ClassificationResult>

    /**
     * 提取 Tag
     *
     * @param content 记录内容
     * @param existingTags 已有标签列表（用于 Tag 复用）
     * @return 提取的标签列表
     */
    suspend fun extractTags(
        content: String,
        existingTags: List<String>
    ): Result<List<String>>

    /**
     * 生成向量嵌入
     *
     * @param text 输入文本
     * @return 嵌入向量（FloatArray）
     */
    suspend fun generateEmbedding(text: String): Result<FloatArray>

    /**
     * 获取当前 AI 配置状态
     *
     * @return AI 配置是否有效
     */
    fun isConfigured(): Boolean

    /**
     * 设置 AI 配置
     *
     * @param config AI 配置
     */
    fun setConfig(config: AIConfig?)
}

/**
 * 分类结果
 *
 * @param sceneCategory 场景分类
 * @param typeCategory 类型分类
 * @param confidence 置信度
 */
data class ClassificationResult(
    val sceneCategory: String,
    val typeCategory: String,
    val confidence: Float
)

/**
 * AI 服务实现
 *
 * @param aiApi Retrofit API 客户端
 * @param json JSON 序列化器
 */
class AIServiceImpl(
    private val aiApi: AIApi,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : AIService {

    private var aiConfig: AIConfig? = null

    override fun isConfigured(): Boolean = aiConfig?.isValid() == true

    override fun setConfig(config: AIConfig?) {
        aiConfig = config
    }

    override suspend fun classifyMemory(content: String): Result<ClassificationResult> {
        val config = aiConfig ?: return Result.failure(AIException.NotConfigured())

        return try {
            val prompt = PromptTemplates.buildClassificationPrompt(content)

            val request = ChatCompletionRequest(
                model = config.llmModel,
                messages = listOf(
                    Message.system(PromptTemplates.SYSTEM_CLASSIFICATION),
                    Message.user(prompt)
                ),
                temperature = 0.3f,
                maxTokens = 100
            )

            val response = callLLM(config, request)
            parseClassificationResult(response)
        } catch (e: Exception) {
            AppLogger.w("Classification failed", e)
            Result.failure(e)
        }
    }

    override suspend fun extractTags(
        content: String,
        existingTags: List<String>
    ): Result<List<String>> {
        val config = aiConfig ?: return Result.failure(AIException.NotConfigured())

        return try {
            val prompt = PromptTemplates.buildTagExtractionPrompt(content, existingTags)

            val request = ChatCompletionRequest(
                model = config.llmModel,
                messages = listOf(
                    Message.system(PromptTemplates.SYSTEM_TAG_EXTRACTION),
                    Message.user(prompt)
                ),
                temperature = 0.5f,
                maxTokens = 200
            )

            val response = callLLM(config, request)
            parseTagResult(response)
        } catch (e: Exception) {
            AppLogger.w("Tag extraction failed", e)
            Result.failure(e)
        }
    }

    override suspend fun generateEmbedding(text: String): Result<FloatArray> {
        val config = aiConfig ?: return Result.failure(AIException.NotConfigured())

        return try {
            val request = EmbeddingRequest(
                model = config.embeddingModel,
                input = text,
                dimensions = Constants.EMBEDDING_DIMENSION
            )

            val authHeader = "Bearer ${config.apiKey}"
            val response = aiApi.getEmbeddings(authHeader, request)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                return Result.failure(AIException.APIError(response.code(), errorBody))
            }

            val embeddingResponse = response.body()
                ?: return Result.failure(AIException.EmptyResponse())

            val embedding = embeddingResponse.data.firstOrNull()
                ?: return Result.failure(AIException.NoEmbedding())

            Result.success(embedding.embedding.map { it.toFloat() }.toFloatArray())
        } catch (e: IOException) {
            AppLogger.w("Embedding network error", e)
            Result.failure(AIException.NetworkError(e))
        } catch (e: Exception) {
            AppLogger.w("Embedding failed", e)
            Result.failure(e)
        }
    }

    /**
     * 调用 LLM
     */
    private suspend fun callLLM(config: AIConfig, request: ChatCompletionRequest): String {
        val authHeader = "Bearer ${config.apiKey}"
        val response = aiApi.chatCompletion(authHeader, request)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            throw AIException.APIError(response.code(), errorBody)
        }

        val completionResponse = response.body()
            ?: throw AIException.EmptyResponse()

        return completionResponse.choices.firstOrNull()?.message?.content
            ?: throw AIException.NoContent()
    }

    /**
     * 解析分类结果
     */
    private fun parseClassificationResult(content: String): Result<ClassificationResult> {
        return try {
            val jsonStr = extractJsonFromContent(content)
            val result = json.decodeFromString<ClassificationJson>(jsonStr)
            Result.success(
                ClassificationResult(
                    sceneCategory = result.scene_category,
                    typeCategory = result.type_category,
                    confidence = result.confidence
                )
            )
        } catch (e: Exception) {
            AppLogger.w("Classification parse error, using default", e)
            Result.success(
                ClassificationResult(
                    sceneCategory = "其他",
                    typeCategory = "其他",
                    confidence = 0.5f
                )
            )
        }
    }

    /**
     * 解析 Tag 结果
     */
    private fun parseTagResult(content: String): Result<List<String>> {
        return try {
            val jsonStr = extractJsonFromContent(content)
            val result = json.decodeFromString<TagResultJson>(jsonStr)
            Result.success(result.tags)
        } catch (e: Exception) {
            AppLogger.w("Tag parse error", e)
            Result.success(emptyList())
        }
    }

    /**
     * 从内容中提取 JSON
     */
    private fun extractJsonFromContent(content: String): String {
        // 尝试提取 JSON 对象
        val startIndex = content.indexOf('{')
        val endIndex = content.lastIndexOf('}')

        return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            content.substring(startIndex, endIndex + 1)
        } else {
            content.trim()
        }
    }

    // ==================== JSON 数据类 ====================

    @Serializable
    private data class ClassificationJson(
        @SerialName("scene_category")
        val scene_category: String,
        @SerialName("type_category")
        val type_category: String,
        val confidence: Float
    )

    @Serializable
    private data class TagResultJson(
        val tags: List<String>
    )
}

/**
 * AI 异常体系
 */
sealed class AIException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /** AI 未配置 */
    class NotConfigured : AIException("AI 服务未配置，请先在设置中输入 API Key")

    /** API 调用错误 */
    class APIError(val code: Int, details: String) : AIException("API 错误 ($code): $details")

    /** 空响应 */
    class EmptyResponse : AIException("API 返回空响应")

    /** 无嵌入数据 */
    class NoEmbedding : AIException("API 未返回嵌入数据")

    /** 无内容 */
    class NoContent : AIException("API 未返回内容")

    /** 网络错误 */
    class NetworkError(cause: Throwable) : AIException("网络错误", cause)

    /** 解析错误 */
    class ParseError(cause: Throwable) : AIException("JSON 解析错误", cause)

    /** 超时错误 */
    class TimeoutError : AIException("请求超时")
}

/**
 * AI 服务状态（用于 Flow 状态管理）
 */
sealed class AIServiceState {
    object NotConfigured : AIServiceState()
    object Configured : AIServiceState()
    data class Processing(val task: String) : AIServiceState()
    data class Success(val message: String) : AIServiceState()
    data class Error(val message: String, val exception: Throwable? = null) : AIServiceState()
}

/**
 * 获取 AI 服务状态流
 */
fun AIService.stateFlow(): Flow<AIServiceState> = flow {
    emit(
        if (isConfigured()) {
            AIServiceState.Configured
        } else {
            AIServiceState.NotConfigured
        }
    )
}
