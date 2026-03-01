package com.pinmem.pinm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinmem.pinm.data.model.AIConfig
import com.pinmem.pinm.data.repository.AIConfigRepository
import com.pinmem.pinm.data.repository.BackupRepository
import com.pinmem.pinm.data.service.AIService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 设置 ViewModel
 */
class SettingsViewModel(
    private val aiConfigRepository: AIConfigRepository,
    private val backupRepository: BackupRepository,
    private val aiService: AIService
) : ViewModel() {

    private val _aiConfig = MutableStateFlow<AIConfig?>(null)
    val aiConfig: StateFlow<AIConfig?> = _aiConfig.asStateFlow()

    private val _isConfigured = MutableStateFlow(false)
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Ready)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadConfig()
    }

    private fun loadConfig() {
        viewModelScope.launch {
            aiConfigRepository.getActiveConfigFlow()
                .collect { config ->
                    _aiConfig.value = config
                    _isConfigured.value = config?.isValid() == true
                    // 同步更新 AIService 配置
                    aiService.setConfig(config)
                }
        }
    }

    /**
     * 保存 AI 配置
     */
    fun saveAIConfig(
        provider: String,
        apiKey: String,
        baseUrl: String?,
        embeddingModel: String,
        llmModel: String
    ) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Saving

            try {
                val config = AIConfig(
                    provider = provider,
                    apiKey = apiKey,
                    baseUrl = baseUrl,
                    embeddingModel = embeddingModel,
                    llmModel = llmModel
                )
                aiConfigRepository.saveConfig(config)
                // 更新 AIService 配置
                aiService.setConfig(config)
                _uiState.value = SettingsUiState.Success("配置已保存")
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "保存失败")
            }
        }
    }

    /**
     * 导出备份
     */
    fun exportBackup(destinationUri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.BackingUp

            val result = backupRepository.exportBackup(destinationUri)
            result.fold(
                onSuccess = { info ->
                    _uiState.value = SettingsUiState.Success("备份成功：${info.recordCount}条记录")
                },
                onFailure = { e ->
                    _uiState.value = SettingsUiState.Error(e.message ?: "备份失败")
                }
            )
        }
    }

    /**
     * 导入备份
     */
    fun importBackup(sourceUri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Restoring

            val result = backupRepository.importBackup(sourceUri)
            result.fold(
                onSuccess = {
                    _uiState.value = SettingsUiState.Success("恢复成功，请重启应用")
                },
                onFailure = { e ->
                    _uiState.value = SettingsUiState.Error(e.message ?: "恢复失败")
                }
            )
        }
    }

    /**
     * 清除状态消息
     */
    fun clearState() {
        if (_uiState.value is SettingsUiState.Success || _uiState.value is SettingsUiState.Error) {
            _uiState.value = SettingsUiState.Ready
        }
    }
}

/**
 * 设置 UI 状态
 */
sealed class SettingsUiState {
    object Ready : SettingsUiState()
    object Saving : SettingsUiState()
    object BackingUp : SettingsUiState()
    object Restoring : SettingsUiState()
    data class Success(val message: String) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}
