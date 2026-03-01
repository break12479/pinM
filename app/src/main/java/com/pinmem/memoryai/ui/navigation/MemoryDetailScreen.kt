@file:OptIn(ExperimentalMaterial3Api::class)

package com.pinmem.pinm.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pinmem.pinm.data.model.Memory
import com.pinmem.pinm.ui.components.MemoryCard
import com.pinmem.pinm.viewmodel.MemoryDetailViewModel
import com.pinmem.pinm.viewmodel.MemoryDetailUiState
import org.koin.androidx.compose.koinViewModel

/**
 * 记忆详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryDetailScreen(
    memoryId: Long,
    onNavigateBack: () -> Unit,
    onEdit: (Long) -> Unit = {},
    viewModel: MemoryDetailViewModel = koinViewModel()
) {
    val memory by viewModel.memory.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    // 加载记忆数据
    LaunchedEffect(memoryId) {
        viewModel.loadMemory(memoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("记录详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    // 编辑按钮
                    IconButton(
                        onClick = { memory?.let { onEdit(it.id) } },
                        enabled = memory != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑"
                        )
                    }
                    // 删除按钮
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = memory != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            memory?.let { mem ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    MemoryCard(
                        memory = mem,
                        onClick = { },
                        onLongClick = { }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 详细信息
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 时间
                            Row {
                                Text(
                                    text = "📅 时间：",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = mem.getFullTime(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            // 分类
                            Row {
                                Text(
                                    text = "📁 分类：",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${mem.sceneCategory} · ${mem.typeCategory}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            // Tag
                            if (mem.getTags().isNotEmpty()) {
                                Row {
                                    Text(
                                        text = "🏷️ 标签：",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                mem.getTags().forEach { tag ->
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(tag) },
                                        modifier = Modifier.height(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 加载指示器
            if (uiState is MemoryDetailUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 错误提示
            if (uiState is MemoryDetailUiState.Error) {
                val error = (uiState as MemoryDetailUiState.Error).message
                Snackbar(
                    modifier = Modifier.align(Alignment.Center),
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog && memory != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条记录吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        memory?.let { viewModel.deleteMemory(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
