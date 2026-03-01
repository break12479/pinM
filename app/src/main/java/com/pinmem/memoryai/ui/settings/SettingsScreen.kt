@file:OptIn(ExperimentalMaterial3Api::class)

package com.pinmem.pinm.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pinmem.pinm.viewmodel.SettingsUiState
import com.pinmem.pinm.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * 设置界面
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val aiConfig by viewModel.aiConfig.collectAsState()
    val isConfigured by viewModel.isConfigured.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showConfigDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // 文件选择器 - 导出
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportBackup(it) }
    }

    // 文件选择器 - 导入
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importBackup(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // AI 配置卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { showConfigDialog = true }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI 配置",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI 配置",
                            tint = if (isConfigured) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isConfigured) {
                        Text(
                            text = "已配置：${aiConfig?.provider ?: "未知"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "嵌入：${aiConfig?.embeddingModel} | LLM: ${aiConfig?.llmModel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "未配置，点击设置",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // 备份卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = "数据备份",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "备份",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { exportLauncher.launch("memoryai_backup_${System.currentTimeMillis()}.db") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("导出备份")
                        }
                        OutlinedButton(
                            onClick = { importLauncher.launch("*/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("导入备份")
                        }
                    }
                }
            }

            // 关于卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = "关于",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "关于",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "pinM v1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "个人智能记忆助手",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // AI 配置对话框
    if (showConfigDialog) {
        AIConfigDialog(
            currentConfig = aiConfig,
            onConfirm = { provider, apiKey, baseUrl, embeddingModel, llmModel ->
                viewModel.saveAIConfig(provider, apiKey, baseUrl, embeddingModel, llmModel)
                showConfigDialog = false
            },
            onDismiss = { showConfigDialog = false }
        )
    }

    // 状态提示
    when (val state = uiState) {
        is SettingsUiState.Success -> {
            LaunchedEffect(state) {
                // 显示成功提示
                viewModel.clearState()
            }
        }
        is SettingsUiState.Error -> {
            LaunchedEffect(state) {
                // 显示错误提示
                viewModel.clearState()
            }
        }
        else -> {}
    }
}

/**
 * AI 配置对话框
 */
@Composable
private fun AIConfigDialog(
    currentConfig: com.pinmem.pinm.data.model.AIConfig?,
    onConfirm: (String, String, String?, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var provider by remember { mutableStateOf(currentConfig?.provider ?: "dashscope") }
    var apiKey by remember { mutableStateOf(currentConfig?.apiKey ?: "") }
    var baseUrl by remember { mutableStateOf(currentConfig?.baseUrl ?: "") }
    var embeddingModel by remember { mutableStateOf(currentConfig?.embeddingModel ?: "text-embedding-v4") }
    var llmModel by remember { mutableStateOf(currentConfig?.llmModel ?: "qwen-plus") }

    // 服务商预设配置
    val providerOptions = listOf("dashscope", "deepseek", "openai", "custom")
    val providerDisplayNames = mapOf(
        "dashscope" to "阿里云百炼 (推荐)",
        "deepseek" to "DeepSeek",
        "openai" to "OpenAI",
        "custom" to "自定义"
    )

    // 根据服务商自动填充模型
    fun updateModelsForProvider(newProvider: String) {
        when (newProvider) {
            "dashscope" -> {
                embeddingModel = "text-embedding-v4"
                llmModel = "qwen-turbo"  // 更快的模型，响应速度约 1-3 秒
                baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1"
            }
            "deepseek" -> {
                embeddingModel = "bge-m3"
                llmModel = "deepseek-chat"
                baseUrl = ""
            }
            "openai" -> {
                embeddingModel = "text-embedding-3-small"
                llmModel = "gpt-4o-mini"
                baseUrl = ""
            }
            "custom" -> {
                // 保持当前值
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 配置") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // 服务提供商选择
                Text(
                    text = "服务提供商",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    providerOptions.forEach { option ->
                        FilterChip(
                            selected = provider == option,
                            onClick = {
                                provider = option
                                updateModelsForProvider(option)
                            },
                            label = { Text(providerDisplayNames[option] ?: option) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // API Key
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key *") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    supportingText = { Text("必填，用于调用 AI 服务") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 自定义 Base URL（仅自定义服务商显示）
                if (provider == "custom") {
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it },
                        label = { Text("Base URL *") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://api.example.com") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 嵌入模型
                OutlinedTextField(
                    value = embeddingModel,
                    onValueChange = { embeddingModel = it },
                    label = { Text("嵌入模型") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("如：bge-m3, text-embedding-3-small") },
                    supportingText = { Text("用于生成向量嵌入，推荐 BGE-M3 (1024 维)") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // LLM 模型
                OutlinedTextField(
                    value = llmModel,
                    onValueChange = { llmModel = it },
                    label = { Text("LLM 模型") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("如：deepseek-chat, gpt-4o-mini") },
                    supportingText = { Text("用于分类、Tag 提取和问答") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(provider, apiKey, baseUrl, embeddingModel, llmModel) },
                enabled = apiKey.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
