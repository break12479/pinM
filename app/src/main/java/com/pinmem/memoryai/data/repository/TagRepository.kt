package com.pinmem.memoryai.data.repository

import com.pinmem.memoryai.data.local.database.TagDao
import com.pinmem.memoryai.data.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Tag Repository
 */
class TagRepository(
    private val tagDao: TagDao
) {

    /**
     * 获取所有 Tag（Flow）
     */
    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    /**
     * 获取所有 Tag
     */
    suspend fun getAllTagsOnce(): List<Tag> = tagDao.getAllTagsOnce()

    /**
     * 根据名称获取 Tag
     */
    suspend fun getTagByName(name: String): Tag? = tagDao.getTagByName(name)

    /**
     * 搜索 Tag
     */
    suspend fun searchTags(query: String): List<Tag> = tagDao.searchTags(query)

    /**
     * 增加 Tag 使用次数
     */
    suspend fun incrementUsage(name: String) {
        val existing = tagDao.getTagByName(name)
        if (existing != null) {
            tagDao.incrementUsage(name)
        } else {
            tagDao.insert(Tag(name = name))
        }
    }

    /**
     * 获取热门标签
     */
    suspend fun getTopTags(): List<String> = tagDao.getTopTags()
}
