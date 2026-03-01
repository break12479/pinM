package com.pinmem.pinm.util

/**
 * 常量定义
 */
object Constants {

    // 数据库
    const val DATABASE_NAME = "pinm_database"

    // 分类
    val SCENE_CATEGORIES = listOf(
        "工作", "生活", "学习", "旅行", "健康", "社交", "财务", "其他"
    )

    val TYPE_CATEGORIES = listOf(
        "事件", "想法", "待办", "感悟", "引用", "其他"
    )

    // AI 模型
    const val DEFAULT_EMBEDDING_MODEL = "bge-m3"
    const val DEFAULT_LLM_MODEL = "deepseek-chat"
    const val EMBEDDING_DIMENSION = 1024

    // Tag
    const val TAG_REUSE_THRESHOLD = 0.7f
    const val MAX_TAGS_PER_MEMORY = 5

    // 搜索
    const val SEARCH_DEBOUNCE_MS = 300L
    const val DEFAULT_SEARCH_LIMIT = 20

    // 分页
    const val DEFAULT_PAGE_SIZE = 20

    // 备份
    const val BACKUP_FILE_EXTENSION = ".db"
    const val BACKUP_DIR_NAME = "backup"

    // 向量
    const val VECTOR_SIMILARITY_THRESHOLD = 0.5f

    // API
    const val API_TIMEOUT_SECONDS = 30L
    const val API_READ_TIMEOUT_SECONDS = 60L
}
