package com.pinmem.pinm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinmem.pinm.data.model.Memory
import com.pinmem.pinm.data.repository.MemoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 记忆详情 ViewModel
 */
class MemoryDetailViewModel(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _memory = MutableStateFlow<Memory?>(null)
    val memory: StateFlow<Memory?> = _memory.asStateFlow()

    private val _uiState = MutableStateFlow<MemoryDetailUiState>(MemoryDetailUiState.Loading)
    val uiState: StateFlow<MemoryDetailUiState> = _uiState.asStateFlow()

    /**
     * 加载记忆数据
     */
    fun loadMemory(memoryId: Long) {
        viewModelScope.launch {
            _uiState.value = MemoryDetailUiState.Loading
            try {
                val mem = memoryRepository.getMemoryById(memoryId)
                _memory.value = mem
                _uiState.value = if (mem != null) {
                    MemoryDetailUiState.Success
                } else {
                    MemoryDetailUiState.Error("记录不存在")
                }
            } catch (e: Exception) {
                _uiState.value = MemoryDetailUiState.Error(e.message ?: "加载失败")
            }
        }
    }

    /**
     * 删除记忆
     */
    fun deleteMemory(memory: Memory) {
        viewModelScope.launch {
            try {
                val result = memoryRepository.deleteMemory(memory.id)
                result.onSuccess {
                    _uiState.value = MemoryDetailUiState.Deleted
                    _memory.value = null
                }.onFailure { e ->
                    _uiState.value = MemoryDetailUiState.Error(e.message ?: "删除失败")
                }
            } catch (e: Exception) {
                _uiState.value = MemoryDetailUiState.Error(e.message ?: "删除失败")
            }
        }
    }
}

/**
 * 记忆详情 UI 状态
 */
sealed class MemoryDetailUiState {
    object Loading : MemoryDetailUiState()
    object Success : MemoryDetailUiState()
    object Deleted : MemoryDetailUiState()
    data class Error(val message: String) : MemoryDetailUiState()
}
