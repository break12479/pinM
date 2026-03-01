package com.pinmem.pinm.data.repository

import com.pinmem.pinm.data.local.database.QAHistoryDao
import com.pinmem.pinm.data.local.vector.InMemoryVectorStore
import com.pinmem.pinm.data.model.Memory
import com.pinmem.pinm.data.model.QAHistory
import com.pinmem.pinm.data.model.QAResponse
import com.pinmem.pinm.data.remote.AIApiService
import com.pinmem.pinm.data.service.AIException
import com.pinmem.pinm.data.remote.MemoryContext
import com.pinmem.pinm.util.AppLogger
import kotlinx.coroutines.flow.Flow

/**
 * 问答 Repository
 */
class QARepository(
    private val qaHistoryDao: QAHistoryDao,
    private val memoryRepository: MemoryRepository,
    private val aiApiService: AIApiService,
    private val aiConfigRepository: AIConfigRepository,
    private val vectorStore: InMemoryVectorStore
) {

    /**
     * 获取问答历史
     */
    fun getHistory(limit: Int = 20): Flow<List<QAHistory>> =
        qaHistoryDao.getHistory(limit)

    /**
     * 根据 ID 获取记忆
     */
    suspend fun getMemoryById(id: Long): Memory? =
        memoryRepository.getMemoryById(id)

    /**
     * 提问
     */
    suspend fun askQuestion(question: String): Result<QAResponse> {
        return try {
            AppLogger.d("QARepository", "Processing question: $question")

            val config = aiConfigRepository.getActiveConfig()
                ?: return Result.failure(Exception("请先在设置中配置 AI API"))

            AppLogger.d("QARepository", "AI config found: ${config.provider}")

            // 1. 语义搜索相关记忆（top 5）
            AppLogger.d("QARepository", "Searching for related memories...")
            val memories = memoryRepository.semanticSearch(question, limit = 5)
            AppLogger.d("QARepository", "Found ${memories.size} related memories")

            if (memories.isEmpty()) {
                AppLogger.w("No related memories found, returning default response")
                return Result.success(
                    QAResponse(
                        answer = "没有找到相关记录，无法回答这个问题。您可以先添加一些记录，或者换个问题试试。",
                        referencedIds = emptyList(),
                        confidence = 0.0f
                    )
                )
            }

            // 2. 构建上下文
            val context = memories.map { mem ->
                MemoryContext(
                    id = mem.id,
                    content = mem.content,
                    createdAt = mem.createdAt,
                    tags = mem.getTags(),
                    sceneCategory = mem.sceneCategory
                )
            }

            // 3. 调用 AI 生成答案
            val answerResult = aiApiService.answerQuestion(config, question, context)

            // 4. 保存问答历史
            val history = QAHistory(
                question = question,
                answer = answerResult.answer,
                referencedMemoryIds = answerResult.referencedIds.joinToString(","),
                modelUsed = config.llmModel
            )
            qaHistoryDao.insert(history)

            Result.success(
                QAResponse(
                    answer = answerResult.answer,
                    referencedIds = answerResult.referencedIds,
                    confidence = answerResult.confidence
                )
            )
        } catch (e: Exception) {
            AppLogger.e("QARepository", "Ask question failed: ${e.message}", e)
            // 根据错误类型返回不同的提示
            val errorMessage = when (e) {
                is java.net.UnknownHostException,
                is java.net.SocketTimeoutException -> "网络连接失败，请检查网络设置"
                is AIException.NotConfigured -> "AI 配置未设置，请先在设置中输入 API Key"
                is AIException.APIError -> "AI 服务响应异常：${e.message}"
                else -> "回答失败：${e.message ?: "未知错误"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * 保存问答历史
     */
    suspend fun saveQAHistory(history: QAHistory) {
        qaHistoryDao.insert(history)
    }

    /**
     * 更新反馈
     */
    suspend fun updateFeedback(id: Long, feedback: Int) {
        qaHistoryDao.updateFeedback(id, feedback)
    }

    /**
     * 清除历史
     */
    suspend fun clearHistory() {
        qaHistoryDao.clearAll()
    }
}
