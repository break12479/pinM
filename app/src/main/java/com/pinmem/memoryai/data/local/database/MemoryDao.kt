package com.pinmem.pinm.data.local.database

import androidx.room.*
import com.pinmem.pinm.data.model.Memory
import kotlinx.coroutines.flow.Flow

/**
 * 记忆记录 DAO
 */
@Dao
interface MemoryDao {

    @Query("SELECT * FROM memories WHERE is_deleted = 0 ORDER BY created_at DESC")
    fun getAllMemories(): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE is_deleted = 0 ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getMemoriesPaged(limit: Int, offset: Int): List<Memory>

    @Query("SELECT * FROM memories WHERE id = :id AND is_deleted = 0")
    suspend fun getMemoryById(id: Long): Memory?

    @Query("SELECT * FROM memories WHERE is_deleted = 0 AND scene_category = :category ORDER BY created_at DESC")
    fun getMemoriesByCategory(category: String): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE is_deleted = 0 AND tags LIKE :tagQuery ORDER BY created_at DESC")
    fun getMemoriesByTag(tagQuery: String): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE is_deleted = 0 AND content LIKE :query ORDER BY created_at DESC")
    suspend fun searchByKeyword(query: String): List<Memory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: Memory): Long

    @Update
    suspend fun update(memory: Memory)

    @Delete
    suspend fun delete(memory: Memory)

    @Query("UPDATE memories SET is_deleted = 1, deleted_at = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE memories SET ai_processed = :processed, embedding_pending = :pending WHERE id = :id")
    suspend fun updateAIStatus(id: Long, processed: Int, pending: Int)

    @Query("UPDATE memories SET embedding = :embedding, embedding_model = :model WHERE id = :id")
    suspend fun updateEmbedding(id: Long, embedding: ByteArray, model: String)

    @Query("SELECT * FROM memories WHERE is_deleted = 0 AND embedding IS NOT NULL")
    suspend fun getMemoriesWithEmbedding(): List<Memory>

    @Query("SELECT COUNT(*) FROM memories WHERE is_deleted = 0")
    suspend fun getCount(): Int

    @Query("SELECT DISTINCT scene_category FROM memories WHERE is_deleted = 0")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT DISTINCT value FROM memories, json_each(tags) WHERE is_deleted = 0")
    suspend fun getAllTags(): List<String>
}
