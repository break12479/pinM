package com.pinmem.pinm.data.local.database

import androidx.room.*
import com.pinmem.pinm.data.model.QAHistory
import kotlinx.coroutines.flow.Flow

/**
 * 问答历史 DAO
 */
@Dao
interface QAHistoryDao {

    @Query("SELECT * FROM qa_history ORDER BY created_at DESC LIMIT :limit")
    fun getHistory(limit: Int = 20): Flow<List<QAHistory>>

    @Query("SELECT * FROM qa_history ORDER BY created_at DESC LIMIT :limit")
    suspend fun getHistoryOnce(limit: Int = 20): List<QAHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: QAHistory): Long

    @Update
    suspend fun update(history: QAHistory)

    @Delete
    suspend fun delete(history: QAHistory)

    @Query("UPDATE qa_history SET feedback = :feedback WHERE id = :id")
    suspend fun updateFeedback(id: Long, feedback: Int)

    @Query("DELETE FROM qa_history")
    suspend fun clearAll()
}
