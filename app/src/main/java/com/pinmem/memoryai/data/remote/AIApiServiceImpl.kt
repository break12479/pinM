package com.pinmem.memoryai.data.remote

import com.pinmem.memoryai.data.model.AIConfig
import com.pinmem.memoryai.util.AppLogger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * AI API 服务实现
 */
class AIApiServiceImpl(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) : AIApiService {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getEmbedding(config: AIConfig, text: String): FloatArray {
        val baseUrl = config.resolveBaseUrl().removeSuffix("/v1")
        val url = "$baseUrl/v1/embeddings"
        
        AppLogger.d("AIService", "📍 Embedding Request: url=$url, model=${config.embeddingModel}, apiKey=${config.apiKey.take(8)}...")

        val requestBody = EmbeddingRequest(
            model = config.embeddingModel,
            input = text,
            dimensions = 1024
        )

        val request = Request.Builder()
            .url(url)
            .post(json.encodeToString(requestBody).toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .addHeader("Content-Type", "application/json")
            .build()

        return httpClient.newCall(request).awaitResponse { response ->
            AppLogger.d("AIService", "📍 Embedding Response: code=${response.code}, message=${response.message}")
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                AppLogger.e("AIService", "❌ Embedding Error: code=${response.code}, body=$errorBody", null)
                throw ApiException("Embedding API failed: ${response.code}")
            }

            val responseBody = response.body?.string()
                ?: throw ApiException("Empty response body")

            AppLogger.i("AIService", "✅ Embedding Success: response length=${responseBody.length}")
            
            val embeddingResponse = json.decodeFromString<EmbeddingResponse>(responseBody)
            embeddingResponse.data.first().embedding.map { it.toFloat() }.toFloatArray()
        }
    }

    override suspend fun classify(config: AIConfig, content: String): ClassificationResult {
        val prompt = buildClassificationPrompt(content)

        val requestBody = ChatCompletionRequest(
            model = config.llmModel,
            messages = listOf(
                Message("system", "你是一个个人记忆助手的分类专家。请根据以下内容，判断记录的场景分类和类型分类。"),
                Message("user", prompt)
            ),
            temperature = 0.3f,
            maxTokens = 100
        )

        val result = callLLM(config, requestBody)

        return try {
            val classification = json.decodeFromString<ClassificationJson>(result)
            ClassificationResult(
                sceneCategory = classification.scene_category,
                typeCategory = classification.type_category,
                confidence = classification.confidence
            )
        } catch (e: Exception) {
            AppLogger.w("Classification parse error", e)
            ClassificationResult("其他", "其他", 0.5f)
        }
    }

    override suspend fun extractTags(config: AIConfig, content: String, existingTags: List<String>): List<String> {
        val prompt = buildTagExtractionPrompt(content, existingTags)

        val requestBody = ChatCompletionRequest(
            model = config.llmModel,
            messages = listOf(
                Message("system", "你是一个个人记忆助手的标签提取专家。"),
                Message("user", prompt)
            ),
            temperature = 0.5f,
            maxTokens = 200
        )

        val result = callLLM(config, requestBody)

        return try {
            val tagResult = json.decodeFromString<TagResultJson>(result)
            tagResult.tags
        } catch (e: Exception) {
            AppLogger.w("Tag extraction parse error", e)
            emptyList()
        }
    }

    override suspend fun answerQuestion(
        config: AIConfig,
        question: String,
        context: List<MemoryContext>
    ): AnswerResult {
        val prompt = buildQAPrompt(question, context)

        val requestBody = ChatCompletionRequest(
            model = config.llmModel,
            messages = listOf(
                Message("system", "你是一个个人记忆助手的 AI 助手。请根据用户的历史记录回答问题。要求：1. 回答简洁，不超过 100 字 2. 直接给出答案，不要多余解释 3. 如果没有相关记录，简单告知"),
                Message("user", prompt)
            ),
            temperature = 0.5f,  // 降低随机性，提高响应速度
            maxTokens = 200  // 限制最大输出长度
        )

        val result = callLLM(config, requestBody)

        return try {
            // 提取 JSON（去除 Markdown 代码块标记）
            val jsonStr = extractJsonFromContent(result)
            AppLogger.d("AIService", "📍 QA JSON: $jsonStr")
            
            val answerResult = json.decodeFromString<AnswerJson>(jsonStr)
            AnswerResult(
                answer = answerResult.answer,
                referencedIds = answerResult.referenced_ids,
                confidence = answerResult.confidence
            )
        } catch (e: Exception) {
            AppLogger.w("QA parse error", e)
            AnswerResult(result, emptyList(), 0.5f)
        }
    }

    /**
     * 从内容中提取 JSON（去除 Markdown 代码块标记）
     */
    private fun extractJsonFromContent(content: String): String {
        // 去除 Markdown 代码块标记 ```json ... ```
        val cleaned = content
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        
        // 尝试提取 JSON 对象
        val startIndex = cleaned.indexOf('{')
        val endIndex = cleaned.lastIndexOf('}')

        return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            cleaned.substring(startIndex, endIndex + 1)
        } else {
            cleaned
        }
    }

    private suspend fun callLLM(config: AIConfig, requestBody: ChatCompletionRequest): String {
        val baseUrl = config.resolveBaseUrl().removeSuffix("/v1")
        val url = "$baseUrl/v1/chat/completions"
        
        val apiKeyPrefix = if (config.apiKey.length >= 8) config.apiKey.take(8) else config.apiKey
        val startTime = System.currentTimeMillis()
        AppLogger.i("AIService", "📍 LLM Request: url=$url, model=${config.llmModel}, apiKey=$apiKeyPrefix...")
        AppLogger.i("AIService", "📍 Config: provider=${config.provider}, baseUrl=${config.baseUrl}, llmModel=${config.llmModel}")

        val request = Request.Builder()
            .url(url)
            .post(json.encodeToString(requestBody).toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .addHeader("Content-Type", "application/json")
            .build()

        return httpClient.newCall(request).awaitResponse { response ->
            val duration = System.currentTimeMillis() - startTime
            AppLogger.d("AIService", "📍 LLM Response: code=${response.code}, time=${duration}ms")
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                AppLogger.e("AIService", "❌ LLM Error: code=${response.code}, body=$errorBody", null)
                throw ApiException("LLM API failed: ${response.code}")
            }

            val responseBody = response.body?.string()
                ?: throw ApiException("Empty response body")

            AppLogger.i("AIService", "✅ LLM Success: response length=${responseBody.length}")
            
            val completionResponse = json.decodeFromString<ChatCompletionResponse>(responseBody)
            completionResponse.choices.first().message.content
        }
    }

    private fun buildClassificationPrompt(content: String): String {
        return """
            场景分类选项：工作、生活、学习、旅行、健康、社交、财务、其他
            类型分类选项：事件、想法、待办、感悟、引用、其他

            记录内容：$content

            请只返回 JSON 格式：
            {
              "scene_category": "场景分类",
              "type_category": "类型分类",
              "confidence": 0.95
            }
        """.trimIndent()
    }

    private fun buildTagExtractionPrompt(content: String, existingTags: List<String>): String {
        return """
            请从以下内容中提取 1-5 个关键词作为标签。

            要求：
            1. 标签应简洁（2-5 个字）
            2. 优先使用已有标签（如果语义相似）
            3. 标签应有助于后续检索

            记录内容：$content
            已有标签：${existingTags.joinToString(", ")}

            请只返回 JSON 格式：
            {
              "tags": ["标签 1", "标签 2", "标签 3"]
            }
        """.trimIndent()
    }

    private fun buildQAPrompt(question: String, context: List<MemoryContext>): String {
        val contextText = context.joinToString("\n---\n") { mem ->
            "[${mem.id}] [${formatTimestamp(mem.createdAt)}] ${mem.content} #${mem.tags.joinToString(" ")}"
        }

        val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())

        return """
            你是一个个人记忆助手的 AI 助手，基于用户的历史记录回答问题。
            
            当前时间：$currentTime
            
            相关历史记录（按相关性排序，格式：[记录 ID] [时间] 内容 #标签）：
            $contextText
            
            用户问题：$question
            
            回答规则：
            1. 【准确性】只基于提供的记录回答，不要编造不存在的信息
            2. 【引用】提到具体记录时，在句尾注明 [记录 ID]，如"您今天上午需要去医院 [1]"
            3. 【时间】如果问题涉及时间，明确说明"根据 X 月 X 日的记录"
            4. 【不确定】如果记录不足或无法确定，说"根据现有记录无法确定"，不要猜测
            5. 【简洁】回答不超过 100 字，直接给出答案，不要多余解释
            6. 【多记录】如果多条记录相关，综合所有信息回答，引用所有相关记录 ID
            
            示例回答：
            - "根据今天上午的记录，您需要去医院 123 就诊 [1]"
            - "根据现有记录无法确定您下午的安排"
            
            请返回 JSON 格式（必须包含 answer 和 referenced_ids 字段）：
            {
              "answer": "答案内容",
              "referenced_ids": [1, 2],
              "confidence": 0.9,
              "uncertain": false
            }
        """.trimIndent()
    }

    /**
     * 格式化时间戳
     */
    private fun formatTimestamp(timestamp: Long): String {
        return try {
            val sdf = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        } catch (e: Exception) {
            timestamp.toString()
        }
    }

    @Serializable
    private data class EmbeddingRequest(
        val model: String,
        val input: String,
        val dimensions: Int = 1024
    )

    @Serializable
    private data class EmbeddingResponse(
        val data: List<EmbeddingData>,
        val usage: Usage?
    )

    @Serializable
    private data class EmbeddingData(
        val embedding: List<Double>
    ) {
        fun toFloatArray(): FloatArray = embedding.map { it.toFloat() }.toFloatArray()
    }

    @Serializable
    private data class Usage(
        @SerialName("prompt_tokens")
        val promptTokens: Int,
        @SerialName("total_tokens")
        val totalTokens: Int
    )

    @Serializable
    private data class ChatCompletionRequest(
        val model: String,
        val messages: List<Message>,
        val temperature: Float = 0.7f,
        @SerialName("max_tokens")
        val maxTokens: Int = 500
    )

    @Serializable
    private data class Message(
        val role: String,
        val content: String
    )

    @Serializable
    private data class ChatCompletionResponse(
        val choices: List<Choice>,
        val usage: Usage?
    )

    @Serializable
    private data class Choice(
        val message: Message
    )

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

    @Serializable
    private data class AnswerJson(
        val answer: String,
        @SerialName("referenced_ids")
        val referenced_ids: List<Long>,
        val confidence: Float,
        val uncertain: Boolean = false  // 是否不确定
    )

    class ApiException(message: String) : Exception(message)
}

// OkHttp Call 扩展，支持 suspend 函数
private suspend inline fun <T> okhttp3.Call.awaitResponse(crossinline block: (okhttp3.Response) -> T): T {
    return kotlin.coroutines.suspendCoroutine { continuation ->
        enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                continuation.resumeWith(Result.failure(e))
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                continuation.resumeWith(runCatching { block(response) })
            }
        })
    }
}
