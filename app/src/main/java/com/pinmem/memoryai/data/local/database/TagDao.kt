package com.pinmem.memoryai.data.local.database

import androidx.room.*
import com.pinmem.memoryai.data.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Tag DAO
 */
@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY usage_count DESC")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * FROM tags ORDER BY usage_count DESC")
    suspend fun getAllTagsOnce(): List<Tag>

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): Tag?

    @Query("SELECT * FROM tags WHERE name LIKE :query ORDER BY usage_count DESC LIMIT 10")
    suspend fun searchTags(query: String): List<Tag>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag): Long

    @Update
    suspend fun update(tag: Tag)

    @Delete
    suspend fun delete(tag: Tag)

    @Query("UPDATE tags SET usage_count = usage_count + 1, updated_at = :updatedAt WHERE name = :name")
    suspend fun incrementUsage(name: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT name FROM tags ORDER BY usage_count DESC LIMIT 50")
    suspend fun getTopTags(): List<String>
}
