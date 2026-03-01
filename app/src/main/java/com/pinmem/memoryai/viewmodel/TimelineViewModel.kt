package com.pinmem.memoryai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinmem.memoryai.data.model.Memory
import com.pinmem.memoryai.data.model.LocationInfo
import com.pinmem.memoryai.data.repository.MemoryRepository
import com.pinmem.memoryai.ui.timeline.GroupedMemories
import com.pinmem.memoryai.ui.timeline.groupByTime
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 时间线 ViewModel
 */
class TimelineViewModel(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    private val _refreshState = MutableStateFlow(false)
    val refreshState: StateFlow<Boolean> = _refreshState.asStateFlow()

    // 按时间分组的记忆列表
    private val _groupedMemories = MutableStateFlow<List<GroupedMemories>>(emptyList())
    val groupedMemories: StateFlow<List<GroupedMemories>> = _groupedMemories.asStateFlow()

    // 所有记忆（用于分组）
    private var allMemories: List<Memory> = emptyList()

    init {
        loadMemories()
    }

    private fun loadMemories() {
        viewModelScope.launch {
            memoryRepository.getAllMemories()
                .catch { e ->
                    _uiState.value = TimelineUiState.Error(e.message ?: "加载失败")
                }
                .collect { memories ->
                    allMemories = memories
                    _uiState.value = TimelineUiState.Success(memories)
                    _groupedMemories.value = memories.groupByTime()
                    _refreshState.value = false
                }
        }
    }

    /**
     * 创建新记忆
     */
    fun createMemory(content: String, location: LocationInfo? = null) {
        viewModelScope.launch {
            val result = memoryRepository.createMemory(content, location)
            result.onFailure { e ->
                _uiState.value = TimelineUiState.Error(e.message ?: "创建失败")
            }
        }
    }

    /**
     * 删除记忆
     */
    fun deleteMemory(memory: Memory) {
        viewModelScope.launch {
            val result = memoryRepository.deleteMemory(memory.id)
            result.onFailure { e ->
                _uiState.value = TimelineUiState.Error(e.message ?: "删除失败")
            }
        }
    }

    /**
     * 刷新
     */
    fun refresh() {
        _refreshState.value = true
        loadMemories()
    }
}

/**
 * 时间线 UI 状态
 */
sealed class TimelineUiState {
    object Loading : TimelineUiState()
    data class Success(val memories: List<Memory>) : TimelineUiState()
    data class Error(val message: String) : TimelineUiState()
}
