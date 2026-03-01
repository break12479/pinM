package com.pinmem.pinm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinmem.pinm.data.model.QAHistory
import com.pinmem.pinm.data.model.QAResponse
import com.pinmem.pinm.data.repository.QARepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 问答 ViewModel
 *
 * 负责问答界面的状态管理和业务逻辑
 * - RAG 检索增强生成
 * - 问答历史
 * - 引用来源展示
 */
class QAViewModel(
    private val qaRepository: QARepository
) : ViewModel() {

    private val _conversation = MutableStateFlow<List<ConversationMessage>>(emptyList())
    val conversation: StateFlow<List<ConversationMessage>> = _conversation.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _uiState = MutableStateFlow<QAUiState>(QAUiState.Ready)
    val uiState: StateFlow<QAUiState> = _uiState.asStateFlow()

    private val _qaHistory = MutableStateFlow<List<QAHistory>>(emptyList())
    val qaHistory: StateFlow<List<QAHistory>> = _qaHistory.asStateFlow()

    private val _referencedMemories = MutableStateFlow<Map<Long, ReferencedMemory>>(emptyMap())
    val referencedMemories: StateFlow<Map<Long, ReferencedMemory>> = _referencedMemories.asStateFlow()

    init {
        // 加载问答历史
        loadQAHistory()
    }

    /**
     * 发送问题
     */
    fun askQuestion(question: String) {
        if (question.isBlank()) return

        // 添加用户消息
        val userMessage = ConversationMessage(
            text = question,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )
        _conversation.value = _conversation.value + userMessage
        _isThinking.value = true
        _uiState.value = QAUiState.Thinking

        viewModelScope.launch {
            try {
                val result = qaRepository.askQuestion(question)

                result.fold(
                    onSuccess = { response ->
                        val aiMessage = ConversationMessage(
                            text = response.answer,
                            isUser = false,
                            timestamp = System.currentTimeMillis(),
                            referencedIds = response.referencedIds
                        )
                        _conversation.value = _conversation.value + aiMessage
                        _uiState.value = QAUiState.Ready

                        // 加载引用的记忆详情
                        if (response.referencedIds.isNotEmpty()) {
                            loadReferencedMemories(response.referencedIds)
                        }
                    },
                    onFailure = { e ->
                        val errorMessage = ConversationMessage(
                            text = "出错了：${e.message}",
                            isUser = false,
                            timestamp = System.currentTimeMillis()
                        )
                        _conversation.value = _conversation.value + errorMessage
                        _uiState.value = QAUiState.Error(e.message ?: "未知错误")
                    }
                )
            } catch (e: Exception) {
                val errorMessage = ConversationMessage(
                    text = "出错了：${e.message}",
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                _conversation.value = _conversation.value + errorMessage
                _uiState.value = QAUiState.Error(e.message ?: "未知错误")
            } finally {
                _isThinking.value = false
            }
        }
    }

    /**
     * 清除对话
     */
    fun clearConversation() {
        _conversation.value = emptyList()
        _uiState.value = QAUiState.Ready
    }

    /**
     * 加载问答历史
     */
    private fun loadQAHistory() {
        viewModelScope.launch {
            qaRepository.getHistory(limit = 20)
                .catch { e ->
                    _qaHistory.value = emptyList()
                }
                .collect { histories ->
                    _qaHistory.value = histories
                }
        }
    }

    /**
     * 加载引用的记忆详情
     */
    private fun loadReferencedMemories(ids: List<Long>) {
        viewModelScope.launch {
            try {
                val memories = mutableMapOf<Long, ReferencedMemory>()
                for (id in ids) {
                    val memory = qaRepository.getMemoryById(id)
                    if (memory != null) {
                        memories[id] = ReferencedMemory(
                            id = memory.id,
                            content = memory.content,
                            createdAt = memory.createdAt,
                            tags = memory.getTags(),
                            category = memory.sceneCategory
                        )
                    }
                }
                _referencedMemories.value = memories
            } catch (e: Exception) {
                // 加载失败不影响主流程
            }
        }
    }

    /**
     * 从历史中选择问题
     */
    fun selectFromHistory(history: QAHistory) {
        // 将历史问答添加到当前对话
        val userMessage = ConversationMessage(
            text = history.question,
            isUser = true,
            timestamp = history.createdAt
        )
        val aiMessage = ConversationMessage(
            text = history.answer,
            isUser = false,
            timestamp = history.createdAt + 1000
        )
        _conversation.value = _conversation.value + userMessage + aiMessage
    }

    /**
     * 清除所有问答历史
     */
    fun clearAllHistory() {
        viewModelScope.launch {
            qaRepository.clearHistory()
        }
    }

    /**
     * 反馈（有用/无用）
     */
    fun giveFeedback(historyId: Long, isHelpful: Boolean) {
        viewModelScope.launch {
            try {
                qaRepository.updateFeedback(historyId, if (isHelpful) 1 else 0)
            } catch (e: Exception) {
                // 反馈失败不影响主流程
            }
        }
    }
}

/**
 * 对话消息
 */
data class ConversationMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val referencedIds: List<Long> = emptyList()
) {
    fun getFormattedTime(): String {
        return android.text.format.DateFormat.format("HH:mm", timestamp).toString()
    }
}

/**
 * 引用记忆
 */
data class ReferencedMemory(
    val id: Long,
    val content: String,
    val createdAt: Long,
    val tags: List<String>,
    val category: String
) {
    fun getRelativeTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - createdAt

        return when {
            diff < 60_000 -> "刚刚"
            diff < 3600_000 -> "${diff / 60_000}分钟前"
            diff < 86400_000 -> "${diff / 3600_000}小时前"
            diff < 172800_000 -> "昨天"
            diff < 604800_000 -> "${diff / 86400_000}天前"
            else -> {
                val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date(createdAt))
                date
            }
        }
    }
}

/**
 * 问答 UI 状态
 */
sealed class QAUiState {
    object Ready : QAUiState()
    object Thinking : QAUiState()
    data class Error(val message: String) : QAUiState()
}
