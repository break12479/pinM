@file:OptIn(ExperimentalMaterial3Api::class)

package com.pinmem.pinm.ui.newmemory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinmem.pinm.ui.components.LoadingIndicatorSmall
import com.pinmem.pinm.viewmodel.NewMemoryUiState
import com.pinmem.pinm.viewmodel.NewMemoryViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 新建/编辑记录界面
 */
@Composable
fun NewMemoryScreen(
    memoryId: Long? = null,
    viewModel: NewMemoryViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val content by viewModel.content.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()

    // 如果是编辑模式，加载记忆数据
    LaunchedEffect(memoryId) {
        memoryId?.let { id ->
            viewModel.loadMemoryForEdit(id)
        }
    }

    // 监听保存成功，返回上一页
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.clearSuccessState()
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (memoryId != null) "编辑记录" else "新建记录") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.cancel(); onNavigateBack() }) {
                        Icon(Icons.Default.Close, contentDescription = "取消")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveMemory() },
                        enabled = content.isNotBlank() && !isSaving
                    ) {
                        if (isSaving) {
                            LoadingIndicatorSmall()
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("保存中...")
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "保存")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("保存")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 内容输入区
            OutlinedTextField(
                value = content,
                onValueChange = { viewModel.updateContent(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                label = { Text("记录内容") },
                placeholder = {
                    Text(
                        text = "记录你的想法、事件或感悟...\n\n可以是：\n• 今天发生的一件小事\n• 突然冒出的想法\n• 重要的会议或约会\n• 读到的好句子",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                minLines = 15,
                maxLines = 20,
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // 信息卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 时间信息
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "时间",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "记录时间",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = getCurrentDateTime(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // 位置信息
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "位置",
                            tint = if (currentLocation != null) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "当前位置",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (currentLocation != null) {
                                Text(
                                    text = currentLocation?.address ?: "获取位置中...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Text(
                                    text = "未获取到位置（可选）",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 提示信息
            Text(
                text = "💡 小提示：记录会自动保存为草稿，防止意外丢失",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )

            // 底部间距
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 错误提示
    if (uiState is NewMemoryUiState.Error) {
        val error = (uiState as NewMemoryUiState.Error).message
        Snackbar(
            modifier = Modifier.padding(16.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer
        ) {
            Text(error, color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}

/**
 * 获取当前日期时间字符串
 */
private fun getCurrentDateTime(): String {
    val now = Date()
    val format = SimpleDateFormat("yyyy 年 MM 月 dd 日 HH:mm", Locale.getDefault())
    return format.format(now)
}
