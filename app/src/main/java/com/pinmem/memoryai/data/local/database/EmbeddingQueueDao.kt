package com.pinmem.pinm.data.local.database

import androidx.room.*
import com.pinmem.pinm.data.model.EmbeddingQueue
import kotlinx.coroutines.flow.Flow

/**
 * 向量嵌入队列 DAO
 */
@Dao
interface EmbeddingQueueDao {

    /**
     * 获取所有待处理任务（Flow）
     */
    @Query("SELECT * FROM embedding_queue ORDER BY created_at ASC")
    fun getAllPending(): Flow<List<EmbeddingQueue>>

    /**
     * 获取所有待处理任务
     */
    @Query("SELECT * FROM embedding_queue ORDER BY created_at ASC LIMIT :limit")
    suspend fun getPendingTasks(limit: Int = 50): List<EmbeddingQueue>

    /**
     * 获取重试次数少于指定值的任务
     */
    @Query("SELECT * FROM embedding_queue WHERE retry_count < :maxRetries ORDER BY created_at ASC")
    suspend fun getRetryableTasks(maxRetries: Int = 3): List<EmbeddingQueue>

    /**
     * 根据 memoryId 获取任务
     */
    @Query("SELECT * FROM embedding_queue WHERE memory_id = :memoryId")
    suspend fun getByMemoryId(memoryId: Long): EmbeddingQueue?

    /**
     * 插入任务
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(queue: EmbeddingQueue): Long

    /**
     * 批量插入任务
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(queues: List<EmbeddingQueue>)

    /**
     * 更新任务
     */
    @Update
    suspend fun update(queue: EmbeddingQueue)

    /**
     * 删除任务
     */
    @Delete
    suspend fun delete(queue: EmbeddingQueue)

    /**
     * 根据 memoryId 删除任务
     */
    @Query("DELETE FROM embedding_queue WHERE memory_id = :memoryId")
    suspend fun deleteByMemoryId(memoryId: Long)

    /**
     * 删除已完成的任务
     */
    @Query("DELETE FROM embedding_queue WHERE memory_id IN (:memoryIds)")
    suspend fun deleteByMemoryIds(memoryIds: List<Long>)

    /**
     * 清除所有任务
     */
    @Query("DELETE FROM embedding_queue")
    suspend fun clearAll()

    /**
     * 获取待处理任务数量
     */
    @Query("SELECT COUNT(*) FROM embedding_queue")
    suspend fun getCount(): Int

    /**
     * 获取失败的任务（重试次数已达上限）
     */
    @Query("SELECT * FROM embedding_queue WHERE retry_count >= :maxRetries ORDER BY last_attempt_at DESC")
    suspend fun getFailedTasks(maxRetries: Int = 3): List<EmbeddingQueue>

    /**
     * 清除失败的任务
     */
    @Query("DELETE FROM embedding_queue WHERE retry_count >= :maxRetries")
    suspend fun clearFailedTasks(maxRetries: Int = 3)
}
