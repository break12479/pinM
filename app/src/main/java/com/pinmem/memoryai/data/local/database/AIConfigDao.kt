package com.pinmem.pinm.data.local.database

import androidx.room.*
import com.pinmem.pinm.data.model.AIConfig
import kotlinx.coroutines.flow.Flow

/**
 * AI 配置 DAO
 */
@Dao
interface AIConfigDao {

    @Query("SELECT * FROM ai_config WHERE is_active = 1 LIMIT 1")
    fun getActiveConfig(): Flow<AIConfig?>

    @Query("SELECT * FROM ai_config WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveConfigOnce(): AIConfig?

    @Query("SELECT * FROM ai_config")
    suspend fun getAllConfigs(): List<AIConfig>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: AIConfig): Long

    @Update
    suspend fun update(config: AIConfig)

    @Delete
    suspend fun delete(config: AIConfig)

    @Query("UPDATE ai_config SET is_active = 0")
    suspend fun deactivateAll()

    @Query("UPDATE ai_config SET is_active = 1 WHERE id = :id")
    suspend fun activateConfig(id: Long)
}
