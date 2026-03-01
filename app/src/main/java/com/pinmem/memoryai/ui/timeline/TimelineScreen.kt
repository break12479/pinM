@file:OptIn(ExperimentalMaterial3Api::class)

package com.pinmem.memoryai.ui.timeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pinmem.memoryai.data.model.Memory
import com.pinmem.memoryai.ui.components.*
import com.pinmem.memoryai.viewmodel.TimelineUiState
import com.pinmem.memoryai.viewmodel.TimelineViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.*

/**
 * 时间线界面
 */
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = koinViewModel(),
    onNavigateToNewMemory: () -> Unit = {},
    onNavigateToMemoryDetail: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.refreshState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var memoryToDelete by remember { mutableStateOf<Memory?>(null) }

    // 按时间分组的记忆列表
    val groupedMemories by viewModel.groupedMemories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "MemoryAI",
                        style = MaterialTheme.typography.headlineSmall
                    ) 
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNewMemory,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "新建记录")
                Spacer(modifier = Modifier.width(8.dp))
                Text("新建记录")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is TimelineUiState.Loading -> {
                    LoadingState()
                }

                is TimelineUiState.Success -> {
                    if (groupedMemories.isEmpty()) {
                        EmptyState(
                            message = "暂无记录\n点击右下角按钮，记录你的第一个想法吧！",
                            actionText = "新建记录",
                            onAction = onNavigateToNewMemory
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            groupedMemories.forEach { (groupLabel, memories) ->
                                item(key = groupLabel) {
                                    TimeGroupHeader(label = groupLabel)
                                }
                                items(
                                    items = memories,
                                    key = { it.id }
                                ) { memory ->
                                    MemoryCard(
                                        memory = memory,
                                        onClick = { onNavigateToMemoryDetail(memory.id) },
                                        onLongClick = {
                                            memoryToDelete = memory
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is TimelineUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.refresh() }
                    )
                }
            }

            // 下拉刷新指示器
            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                )
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog && memoryToDelete != null) {
        DeleteConfirmDialog(
            onConfirm = {
                viewModel.deleteMemory(memoryToDelete!!)
                showDeleteDialog = false
                memoryToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                memoryToDelete = null
            }
        )
    }
}

/**
 * 按时间分组记忆
 */
data class GroupedMemories(
    val groupLabel: String,
    val memories: List<Memory>
)

/**
 * 将记忆按时间分组
 */
fun List<Memory>.groupByTime(): List<GroupedMemories> {
    val now = System.currentTimeMillis()
    val calendar = Calendar.getInstance()
    
    // 计算各个时间边界
    calendar.time = Date(now)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val todayStart = calendar.timeInMillis
    
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayStart = calendar.timeInMillis
    
    calendar.add(Calendar.DAY_OF_YEAR, -6)
    val weekStart = calendar.timeInMillis
    
    val today = mutableListOf<Memory>()
    val yesterday = mutableListOf<Memory>()
    val thisWeek = mutableListOf<Memory>()
    val earlier = mutableListOf<Memory>()
    
    forEach { memory ->
        when {
            memory.createdAt >= todayStart -> today.add(memory)
            memory.createdAt >= yesterdayStart -> yesterday.add(memory)
            memory.createdAt >= weekStart -> thisWeek.add(memory)
            else -> earlier.add(memory)
        }
    }
    
    val result = mutableListOf<GroupedMemories>()
    if (today.isNotEmpty()) result.add(GroupedMemories("今天", today))
    if (yesterday.isNotEmpty()) result.add(GroupedMemories("昨天", yesterday))
    if (thisWeek.isNotEmpty()) result.add(GroupedMemories("本周", thisWeek))
    if (earlier.isNotEmpty()) result.add(GroupedMemories("更早", earlier))
    
    return result
}
