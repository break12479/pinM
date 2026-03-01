@file:OptIn(ExperimentalMaterial3Api::class)

package com.pinmem.memoryai.ui.qa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pinmem.memoryai.viewmodel.*
import org.koin.androidx.compose.koinViewModel

/**
 * 问答界面
 */
@Composable
fun QAScreen(
    viewModel: QAViewModel = koinViewModel(),
    onNavigateToMemoryDetail: (Long) -> Unit = {}
) {
    val conversation by viewModel.conversation.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val qaHistory by viewModel.qaHistory.collectAsState()
    val referencedMemories by viewModel.referencedMemories.collectAsState()

    var question by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // 当有新消息时滚动到底部
    LaunchedEffect(conversation.size) {
        if (conversation.isNotEmpty()) {
            listState.animateScrollToItem(conversation.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("问答") },
                actions = {
                    if (qaHistory.isNotEmpty()) {
                        IconButton(onClick = { showHistory = !showHistory }) {
                            Icon(Icons.Default.History, contentDescription = "历史")
                        }
                    }
                    if (conversation.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearConversation() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "清除对话")
                        }
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
            Column {
                // 对话列表
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 欢迎消息
                    if (conversation.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "💬",
                                        style = MaterialTheme.typography.displayLarge
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "有什么可以帮你的？",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // 对话消息
                    items(
                        items = conversation,
                        key = { it.timestamp }
                    ) { message ->
                        MessageBubble(
                            message = message,
                            referencedMemories = referencedMemories,
                            onMemoryClick = onNavigateToMemoryDetail
                        )
                    }

                    // 思考中指示器
                    if (isThinking) {
                        item {
                            ThinkingIndicator()
                        }
                    }
                }

                // 错误提示
                if (uiState is QAUiState.Error) {
                    val error = (uiState as QAUiState.Error).message
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(error, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }

                // 输入框
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedTextField(
                        value = question,
                        onValueChange = { question = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("问我任何问题...") },
                        minLines = 1,
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (question.isNotBlank()) {
                                viewModel.askQuestion(question)
                                question = ""
                            }
                        },
                        enabled = question.isNotBlank() && !isThinking,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "发送",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // 历史侧边栏
            if (showHistory) {
                HistorySidebar(
                    histories = qaHistory,
                    onSelect = {
                        viewModel.selectFromHistory(it)
                        showHistory = false
                    },
                    onClear = {
                        viewModel.clearAllHistory()
                        showHistory = false
                    },
                    onDismiss = { showHistory = false }
                )
            }
        }
    }
}

/**
 * 历史快捷访问项
 */
@Composable
private fun HistoryQuickAccessItem(
    history: com.pinmem.memoryai.data.model.QAHistory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = history.question,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = history.answer,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 消息气泡
 */
@Composable
private fun MessageBubble(
    message: ConversationMessage,
    referencedMemories: Map<Long, ReferencedMemory>,
    onMemoryClick: (Long) -> Unit
) {
    val isUser = message.isUser

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                // 引用来源（仅 AI 消息且存在引用时显示）
                if (!isUser && message.referencedIds.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "📚 依据来源",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    message.referencedIds.forEach { id ->
                        val memory = referencedMemories[id]
                        if (memory != null) {
                            ReferenceCard(
                                memory = memory,
                                onClick = { onMemoryClick(id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.getFormattedTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUser) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}

/**
 * 引用来源卡片
 */
@Composable
private fun ReferenceCard(
    memory: ReferencedMemory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // 内容摘要
            Text(
                text = memory.content,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 时间和 Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = memory.getRelativeTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Tag
                if (memory.tags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(memory.tags.take(3)) { tag ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 思考中指示器
 */
@Composable
private fun ThinkingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 200.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "思考中...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 历史侧边栏
 */
@Composable
private fun HistorySidebar(
    histories: List<com.pinmem.memoryai.data.model.QAHistory>,
    onSelect: (com.pinmem.memoryai.data.model.QAHistory) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Card(
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .align(Alignment.CenterEnd),
            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "问答历史",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row {
                        if (histories.isNotEmpty()) {
                            IconButton(onClick = onClear) {
                                Icon(
                                    Icons.Default.DeleteSweep,
                                    contentDescription = "清除全部",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "关闭")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (histories.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无历史记录",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(histories) { history ->
                            HistoryItem(
                                history = history,
                                onClick = { onSelect(history) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 历史项
 */
@Composable
private fun HistoryItem(
    history: com.pinmem.memoryai.data.model.QAHistory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = history.question,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = history.answer,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = history.getFormattedTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
