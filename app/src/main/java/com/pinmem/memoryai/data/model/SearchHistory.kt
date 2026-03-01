package com.pinmem.pinm.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 搜索历史实体
 *
 * @param id 搜索历史 ID
 * @param query 搜索查询文本
 * @param resultCount 搜索结果数量
 * @param createdAt 搜索时间戳
 * @param searchType 搜索类型：semantic/keyword/hybrid
 */
@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "query")
    val query: String,

    @ColumnInfo(name = "result_count")
    val resultCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "search_type")
    val searchType: String = "semantic",

    @ColumnInfo(name = "filter_tags")
    val filterTags: String? = null,

    @ColumnInfo(name = "filter_category")
    val filterCategory: String? = null
) {
    /**
     * 获取格式化的搜索时间
     */
    fun getFormattedTime(): String {
        return try {
            val sdf = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(createdAt))
        } catch (e: Exception) {
            createdAt.toString()
        }
    }

    /**
     * 获取解析后的 Tag 列表
     */
    fun getFilterTagsList(): List<String> {
        return filterTags?.let {
            it.trim('[', ']', ' ').split(",").filter { tag -> tag.isNotBlank() }
        } ?: emptyList()
    }
}
