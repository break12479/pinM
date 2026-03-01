package com.pinmem.memoryai.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 问答历史实体
 * 
 * @param id 问答 ID
 * @param question 用户问题
 * @param answer AI 回答
 * @param referencedMemoryIds 引用记录 ID 列表 JSON
 * @param createdAt 问答时间
 * @param feedback 用户反馈：1=有用 0=无用 NULL=未反馈
 * @param modelUsed 使用的 LLM 模型
 * @param tokensUsed 消耗 Token 数
 * @param latencyMs 响应延迟（毫秒）
 */
@Entity(tableName = "qa_history")
data class QAHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "question")
    val question: String,

    @ColumnInfo(name = "answer")
    val answer: String,

    @ColumnInfo(name = "referenced_memory_ids")
    val referencedMemoryIds: String = "[]",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "feedback")
    val feedback: Int? = null,

    @ColumnInfo(name = "model_used")
    val modelUsed: String? = null,

    @ColumnInfo(name = "tokens_used")
    val tokensUsed: Int? = null,

    @ColumnInfo(name = "latency_ms")
    val latencyMs: Long? = null
) {
    /**
     * 获取格式化的时间
     */
    fun getFormattedTime(): String {
        return try {
            val sdf = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(createdAt))
        } catch (e: Exception) {
            createdAt.toString()
        }
    }
}
