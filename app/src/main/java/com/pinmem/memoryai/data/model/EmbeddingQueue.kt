package com.pinmem.pinm.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 向量嵌入待处理队列实体
 *
 * 当 AI 服务不可用或网络中断时，将待处理的向量嵌入任务加入队列
 * 网络恢复后按顺序处理
 *
 * @param id 队列 ID
 * @param memoryId 关联的记忆记录 ID
 * @param content 记录内容（用于生成向量）
 * @param retryCount 重试次数
 * @param createdAt 创建时间
 * @param lastAttemptAt 最后尝试时间
 */
@Entity(
    tableName = "embedding_queue",
    foreignKeys = [
        ForeignKey(
            entity = Memory::class,
            parentColumns = ["id"],
            childColumns = ["memory_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["memory_id"]),
        Index(value = ["last_attempt_at"])
    ]
)
data class EmbeddingQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "memory_id")
    val memoryId: Long,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "retry_count")
    val retryCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_attempt_at")
    val lastAttemptAt: Long? = null,

    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null
) {
    /**
     * 是否超过最大重试次数
     */
    fun hasMaxRetries(maxRetries: Int = 3): Boolean = retryCount >= maxRetries

    /**
     * 创建重试副本
     */
    fun withRetry(errorMessage: String? = null): EmbeddingQueue = copy(
        retryCount = retryCount + 1,
        lastAttemptAt = System.currentTimeMillis(),
        errorMessage = errorMessage
    )
}
