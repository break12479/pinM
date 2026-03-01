@file:OptIn(ExperimentalMaterial3Api::class)

package com.pinmem.pinm.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pinmem.pinm.data.model.Memory
import com.pinmem.pinm.data.model.SearchHistory
import com.pinmem.pinm.data.model.SearchResult
import com.pinmem.pinm.ui.components.*
import com.pinmem.pinm.viewmodel.SearchUiState
import com.pinmem.pinm.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * 搜索界面
 */
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onNavigateToMemoryDetail: (Long) -> Unit = {}
) {
    val query by viewModel.query.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val availableTags by viewModel.availableTags.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showTagFilter by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("搜索") },
                actions = {
                    if (searchHistory.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAllHistory() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "清除历史")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索框
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.updateQuery(it) },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = { Text("搜索记忆...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(Icons.Default.Clear, contentDescription = "清除")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                // Tag 筛选按钮
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedIconButton(
                    onClick = { showTagFilter = !showTagFilter },
                    modifier = Modifier.size(48.dp),
                    colors = if (selectedTags.isNotEmpty()) {
                        IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    } else {
                        IconButtonDefaults.outlinedIconButtonColors()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "筛选",
                        tint = if (selectedTags.isNotEmpty()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            // Tag 筛选区域
            if (showTagFilter && availableTags.isNotEmpty()) {
                TagFilterSection(
                    availableTags = availableTags,
                    selectedTags = selectedTags,
                    onTagToggle = { viewModel.toggleTag(it) },
                    onClear = { viewModel.clearTagFilter() }
                )
            }

            // 搜索结果/历史
            when (val state = uiState) {
                is SearchUiState.Idle -> {
                    // 空闲状态显示搜索历史
                    if (searchHistory.isNotEmpty()) {
                        SearchHistorySection(
                            histories = searchHistory,
                            onHistoryClick = { viewModel.selectFromHistory(it) },
                            onHistoryDelete = { viewModel.deleteHistoryItem(it.id) }
                        )
                    } else {
                        EmptyState("输入关键词开始搜索")
                    }
                }

                is SearchUiState.Searching -> {
                    LoadingState(modifier = Modifier.fillMaxSize())
                }

                is SearchUiState.Success -> {
                    if (state.results.isEmpty()) {
                        EmptyState("没有找到相关记录")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = state.results,
                                key = { it.memory.id }
                            ) { result ->
                                MemoryCard(
                                    memory = result.memory,
                                    score = result.score,
                                    onClick = { onNavigateToMemoryDetail(result.memory.id) },
                                    onLongClick = { }
                                )
                            }
                        }
                    }
                }

                is SearchUiState.NoResults -> {
                    EmptyState("没有找到相关记录")
                }

                is SearchUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.updateQuery(query) }
                    )
                }
            }
        }
    }
}

/**
 * Tag 筛选区域
 */
@Composable
private fun TagFilterSection(
    availableTags: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "按 Tag 筛选",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (selectedTags.isNotEmpty()) {
                    TextButton(onClick = onClear) {
                        Text("清除", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableTags) { tag ->
                    FilterChip(
                        selected = tag in selectedTags,
                        onClick = { onTagToggle(tag) },
                        label = { Text(tag) },
                        leadingIcon = if (tag in selectedTags) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "已选择",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}

/**
 * 搜索历史区域
 */
@Composable
private fun SearchHistorySection(
    histories: List<SearchHistory>,
    onHistoryClick: (SearchHistory) -> Unit,
    onHistoryDelete: (SearchHistory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "最近搜索",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(histories) { history ->
                SearchHistoryItem(
                    history = history,
                    onClick = { onHistoryClick(history) },
                    onDelete = { onHistoryDelete(history) }
                )
            }
        }
    }
}

/**
 * 搜索历史项
 */
@Composable
private fun SearchHistoryItem(
    history: SearchHistory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "历史",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = history.query,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${history.resultCount} 条结果 · ${history.getFormattedTime()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 记忆卡片（带分数显示）
 */
@Composable
fun MemoryCard(
    memory: Memory,
    score: Float,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // 内容
            Text(
                text = memory.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tag 列表
            if (memory.getTags().isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    memory.getTags().take(4).forEach { tag ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                    if (memory.getTags().size > 4) {
                        Text(
                            text = "+${memory.getTags().size - 4}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 底部信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 时间和分数
                Column {
                    Text(
                        text = memory.getRelativeTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "相似度：${(score * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 位置和分类图标
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (memory.hasLocation()) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "位置",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
