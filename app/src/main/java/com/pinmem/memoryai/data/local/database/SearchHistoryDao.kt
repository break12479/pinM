package com.pinmem.memoryai.data.local.database

import androidx.room.*
import com.pinmem.memoryai.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

/**
 * 搜索历史 DAO
 */
@Dao
interface SearchHistoryDao {

    /**
     * 获取最近的搜索历史（Flow）
     *
     * @param limit 返回数量
     * @return 搜索历史列表
     */
    @Query("SELECT * FROM search_history ORDER BY created_at DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 20): Flow<List<SearchHistory>>

    /**
     * 获取最近的搜索历史（一次性）
     *
     * @param limit 返回数量
     * @return 搜索历史列表
     */
    @Query("SELECT * FROM search_history ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecentHistoryOnce(limit: Int = 20): List<SearchHistory>

    /**
     * 插入搜索历史
     *
     * @param history 搜索历史
     * @return 插入的 ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: SearchHistory): Long

    /**
     * 批量插入搜索历史
     *
     * @param histories 搜索历史列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(histories: List<SearchHistory>)

    /**
     * 删除单条搜索历史
     *
     * @param history 搜索历史
     */
    @Delete
    suspend fun delete(history: SearchHistory)

    /**
     * 删除指定 ID 的搜索历史
     *
     * @param id 搜索历史 ID
     */
    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * 清除所有搜索历史
     */
    @Query("DELETE FROM search_history")
    suspend fun clearAll()

    /**
     * 清除指定时间之前的搜索历史
     *
     * @param timestamp 时间戳
     */
    @Query("DELETE FROM search_history WHERE created_at < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    /**
     * 搜索历史记录
     *
     * @param query 查询文本
     * @return 匹配的搜索历史
     */
    @Query("SELECT * FROM search_history WHERE query LIKE :query ORDER BY created_at DESC")
    suspend fun searchHistory(query: String): List<SearchHistory>

    /**
     * 获取所有唯一的搜索查询
     *
     * @param limit 返回数量
     * @return 查询列表
     */
    @Query("SELECT DISTINCT query FROM search_history ORDER BY created_at DESC LIMIT :limit")
    suspend fun getUniqueQueries(limit: Int = 50): List<String>
}
