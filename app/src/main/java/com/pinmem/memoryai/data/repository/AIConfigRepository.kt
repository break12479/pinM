package com.pinmem.pinm.data.repository

import com.pinmem.pinm.data.local.database.AIConfigDao
import com.pinmem.pinm.data.model.AIConfig
import kotlinx.coroutines.flow.Flow

/**
 * AI 配置 Repository
 */
class AIConfigRepository(
    private val aiConfigDao: AIConfigDao
) {

    /**
     * 获取活跃的 AI 配置（Flow）
     */
    fun getActiveConfigFlow(): Flow<AIConfig?> = aiConfigDao.getActiveConfig()

    /**
     * 获取活跃的 AI 配置
     */
    suspend fun getActiveConfig(): AIConfig? = aiConfigDao.getActiveConfigOnce()

    /**
     * 保存 AI 配置
     */
    suspend fun saveConfig(config: AIConfig) {
        // 先停用所有配置
        aiConfigDao.deactivateAll()

        // 插入新配置并激活
        val id = aiConfigDao.insert(config)
        aiConfigDao.activateConfig(id)
    }

    /**
     * 检查是否有有效配置
     */
    suspend fun hasValidConfig(): Boolean {
        val config = getActiveConfig()
        return config?.isValid() == true
    }
}
