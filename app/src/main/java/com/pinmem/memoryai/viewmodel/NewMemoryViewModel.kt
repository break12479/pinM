package com.pinmem.memoryai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinmem.memoryai.data.model.LocationInfo
import com.pinmem.memoryai.data.repository.MemoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 新建记录 ViewModel
 * 支持：内容输入、自动时间戳、草稿箱自动保存
 */
class NewMemoryViewModel(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewMemoryUiState>(NewMemoryUiState.Idle)
    val uiState: StateFlow<NewMemoryUiState> = _uiState.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    // 草稿自动保存
    private var draftSaveJob: Job? = null
    private val _draftContent = MutableStateFlow("")
    val draftContent: StateFlow<String> = _draftContent.asStateFlow()

    // 当前位置信息（只读显示）
    private val _currentLocation = MutableStateFlow<LocationInfo?>(null)
    val currentLocation: StateFlow<LocationInfo?> = _currentLocation.asStateFlow()

    // 自动时间戳
    val currentTimestamp: Long = System.currentTimeMillis()

    // 当前编辑的记忆 ID（如果是编辑模式）
    private var editingMemoryId: Long? = null

    init {
        loadDraft()
    }

    /**
     * 加载记忆数据进行编辑
     */
    fun loadMemoryForEdit(memoryId: Long) {
        viewModelScope.launch {
            try {
                val memory = memoryRepository.getMemoryById(memoryId)
                memory?.let {
                    editingMemoryId = memoryId
                    _content.value = it.content
                    _currentLocation.value = if (it.hasLocation()) {
                        LocationInfo(
                            latitude = it.locationLat!!,
                            longitude = it.locationLng!!,
                            address = it.locationAddress
                        )
                    } else null
                }
            } catch (e: Exception) {
                _uiState.value = NewMemoryUiState.Error("加载失败：${e.message}")
            }
        }
    }

    /**
     * 更新内容
     */
    fun updateContent(newContent: String) {
        _content.value = newContent
        scheduleDraftSave(newContent)
    }

    /**
     * 设置位置信息
     */
    fun setLocation(location: LocationInfo?) {
        _currentLocation.value = location
    }

    /**
     * 保存记录（新建或更新）
     */
    fun saveMemory() {
        val content = _content.value.trim()
        if (content.isEmpty()) {
            _uiState.value = NewMemoryUiState.Error("内容不能为空")
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _uiState.value = NewMemoryUiState.Saving

            val result = if (editingMemoryId != null) {
                // 编辑模式
                memoryRepository.updateMemory(
                    editingMemoryId!!,
                    content,
                    _currentLocation.value
                )
            } else {
                // 新建模式
                memoryRepository.createMemory(content, _currentLocation.value)
            }

            result.fold(
                onSuccess = {
                    _isSaving.value = false
                    _saveSuccess.value = true
                    _uiState.value = NewMemoryUiState.Success
                    clearDraft()
                    clearContent()
                    editingMemoryId = null
                },
                onFailure = { e ->
                    _isSaving.value = false
                    _uiState.value = NewMemoryUiState.Error(e.message ?: "保存失败")
                }
            )
        }
    }

    /**
     * 清除成功状态
     */
    fun clearSuccessState() {
        _saveSuccess.value = false
        _uiState.value = NewMemoryUiState.Idle
    }

    /**
     * 清除内容
     */
    fun clearContent() {
        _content.value = ""
    }

    /**
     * 加载草稿
     */
    private fun loadDraft() {
        // 从 DataStore 加载草稿（简化实现，使用内存）
        viewModelScope.launch {
            // 实际项目中应从 DataStore 加载
            delay(100)
        }
    }

    /**
     * 计划草稿自动保存（防抖）
     */
    private fun scheduleDraftSave(content: String) {
        draftSaveJob?.cancel()
        draftSaveJob = viewModelScope.launch {
            delay(1000) // 1 秒防抖
            _draftContent.value = content
            saveDraft(content)
        }
    }

    /**
     * 保存草稿
     */
    private suspend fun saveDraft(content: String) {
        // 实际项目中应保存到 DataStore
        // 这里仅做演示
    }

    /**
     * 清除草稿
     */
    private fun clearDraft() {
        _draftContent.value = ""
        draftSaveJob?.cancel()
    }

    /**
     * 取消操作
     */
    fun cancel() {
        clearContent()
        clearDraft()
        _uiState.value = NewMemoryUiState.Idle
    }
}

/**
 * 新建记录 UI 状态
 */
sealed class NewMemoryUiState {
    object Idle : NewMemoryUiState()
    object Saving : NewMemoryUiState()
    object Success : NewMemoryUiState()
    data class Error(val message: String) : NewMemoryUiState()
}
