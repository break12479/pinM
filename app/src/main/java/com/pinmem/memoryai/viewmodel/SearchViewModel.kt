package com.pinmem.pinm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinmem.pinm.data.model.Memory
import com.pinmem.pinm.data.model.SearchHistory
import com.pinmem.pinm.data.model.SearchResult
import com.pinmem.pinm.data.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 搜索 ViewModel
 *
 * 负责搜索界面的状态管理和业务逻辑
 * - 实时搜索（带防抖）
 * - 搜索历史（最近 20 条）
 * - Tag 筛选
 * - 语义搜索 + 关键词搜索降级
 */
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<SearchHistory>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistory>> = _searchHistory.asStateFlow()

    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    val selectedTags: StateFlow<Set<String>> = _selectedTags.asStateFlow()

    private val _availableTags = MutableStateFlow<List<String>>(emptyList())
    val availableTags: StateFlow<List<String>> = _availableTags.asStateFlow()

    private var searchJob: Job? = null

    init {
        // 加载搜索历史
        loadSearchHistory()
        // 加载所有可用 Tag
        loadAvailableTags()
    }

    /**
     * 更新搜索查询
     */
    fun updateQuery(newQuery: String) {
        _query.value = newQuery

        // 防抖处理
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (newQuery.isBlank()) {
                _searchResults.value = emptyList()
                _uiState.value = SearchUiState.Idle
                _isSearching.value = false
            } else {
                delay(300)  // 300ms 防抖
                performSearch(newQuery)
            }
        }
    }

    /**
     * 执行搜索
     */
    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _uiState.value = SearchUiState.Searching

            try {
                val results = if (_selectedTags.value.isNotEmpty()) {
                    // 有 Tag 筛选，使用高级搜索
                    searchRepository.advancedSearch(
                        query = query,
                        tags = _selectedTags.value.toList(),
                        limit = 20
                    ).getOrNull() ?: emptyList()
                } else {
                    // 无 Tag 筛选，使用语义搜索
                    searchRepository.semanticSearch(query, limit = 20).getOrNull() ?: emptyList()
                }

                if (results.isEmpty()) {
                    // 降级到关键词搜索
                    val keywordResults = searchRepository.keywordSearch(query, limit = 20)
                        .getOrNull() ?: emptyList()
                    _searchResults.value = keywordResults.map { m -> SearchResult(m.memory, 0.5f) }
                    _uiState.value = if (keywordResults.isEmpty()) {
                        SearchUiState.NoResults
                    } else {
                        SearchUiState.Success(keywordResults.map { m -> SearchResult(m.memory, 0.5f) })
                    }
                } else {
                    _searchResults.value = results
                    _uiState.value = if (results.isEmpty()) {
                        SearchUiState.NoResults
                    } else {
                        SearchUiState.Success(results)
                    }
                }

                // 保存搜索历史
                if (query.isNotBlank()) {
                    searchRepository.saveSearchHistory(
                        query = query,
                        resultCount = results.size,
                        searchType = "semantic",
                        filterTags = _selectedTags.value.toList().takeIf { it.isNotEmpty() }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "搜索失败")
            } finally {
                _isSearching.value = false
            }
        }
    }

    /**
     * 清除搜索
     */
    fun clearSearch() {
        _query.value = ""
        _searchResults.value = emptyList()
        _uiState.value = SearchUiState.Idle
    }

    /**
     * 加载搜索历史
     */
    private fun loadSearchHistory() {
        viewModelScope.launch {
            searchRepository.getSearchHistory(limit = 20)
                .catch { e ->
                    _searchHistory.value = emptyList()
                }
                .collect { histories ->
                    _searchHistory.value = histories
                }
        }
    }

    /**
     * 加载所有可用 Tag
     */
    private fun loadAvailableTags() {
        viewModelScope.launch {
            try {
                // 从记忆中提取所有唯一的 Tag
                val tags = searchRepository.getSearchSuggestions("", limit = 100)
                _availableTags.value = tags.distinct()
            } catch (e: Exception) {
                _availableTags.value = emptyList()
            }
        }
    }

    /**
     * 切换 Tag 选择状态
     */
    fun toggleTag(tag: String) {
        val currentTags = _selectedTags.value.toMutableSet()
        if (tag in currentTags) {
            currentTags.remove(tag)
        } else {
            currentTags.add(tag)
        }
        _selectedTags.value = currentTags

        // 重新执行搜索
        if (_query.value.isNotBlank()) {
            performSearch(_query.value)
        }
    }

    /**
     * 清除 Tag 筛选
     */
    fun clearTagFilter() {
        _selectedTags.value = emptySet()
        if (_query.value.isNotBlank()) {
            performSearch(_query.value)
        }
    }

    /**
     * 从搜索历史中选择
     */
    fun selectFromHistory(history: SearchHistory) {
        _query.value = history.query
        performSearch(history.query)
    }

    /**
     * 删除搜索历史
     */
    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            searchRepository.deleteSearchHistory(id)
        }
    }

    /**
     * 清除所有搜索历史
     */
    fun clearAllHistory() {
        viewModelScope.launch {
            searchRepository.clearSearchHistory()
        }
    }

    /**
     * 按 Tag 搜索
     */
    fun searchByTag(tag: String) {
        viewModelScope.launch {
            _selectedTags.value = setOf(tag)
            _isSearching.value = true

            try {
                val results = searchRepository.searchByTags(listOf(tag), limit = 20)
                    .getOrNull() ?: emptyList()

                _searchResults.value = results.map { memory ->
                    SearchResult(memory, 0.5f)
                }
                _uiState.value = if (results.isEmpty()) {
                    SearchUiState.NoResults
                } else {
                    SearchUiState.Success(_searchResults.value)
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "搜索失败")
            } finally {
                _isSearching.value = false
            }
        }
    }
}

/**
 * 搜索 UI 状态
 */
sealed class SearchUiState {
    object Idle : SearchUiState()
    object Searching : SearchUiState()
    data class Success(val results: List<SearchResult>) : SearchUiState()
    object NoResults : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
