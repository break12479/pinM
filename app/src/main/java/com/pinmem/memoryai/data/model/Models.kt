package com.pinmem.memoryai.data.model

/**
 * AI 分类结果
 * 
 * @param sceneCategory 场景分类
 * @param typeCategory 类型分类
 * @param confidence 置信度
 */
data class Classification(
    val sceneCategory: String,
    val typeCategory: String,
    val confidence: Float = 0.9f
)

/**
 * QA 响应
 * 
 * @param answer 答案内容
 * @param referencedIds 引用记录 ID 列表
 * @param confidence 置信度
 */
data class QAResponse(
    val answer: String,
    val referencedIds: List<Long> = emptyList(),
    val confidence: Float = 0.0f
)

/**
 * 位置信息
 */
data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

/**
 * 搜索结果
 * 
 * @param memory 记忆记录
 * @param score 相似度分数
 */
data class SearchResult(
    val memory: Memory,
    val score: Float
)

/**
 * 备份信息
 */
data class BackupInfo(
    val path: String,
    val backupTime: Long,
    val recordCount: Int,
    val fileSize: Long,
    val checksum: String
)
