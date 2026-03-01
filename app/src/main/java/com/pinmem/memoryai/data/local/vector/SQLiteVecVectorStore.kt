package com.pinmem.memoryai.data.local.vector

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.pinmem.memoryai.util.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * SQLite-vec 向量存储实现
 */
class SQLiteVecVectorStore(private val context: Context) : VectorStore {

    companion object {
        private const val TAG = "SQLiteVecVectorStore"
        private const val DATABASE_NAME = "memoryai_vector.db"
        private const val COLLECTION_NAME = "memories_embedding"
        private const val EMBEDDING_DIMENSION = 1024

        private const val MAX_RETRIES = 3
    }

    private var db: SQLiteDatabase? = null
    private var isInitialized = false

    /**
     * 向量元数据
     */
    data class VectorMetadata(
        val memoryId: Long,
        val content: String,
        val sceneCategory: String,
        val typeCategory: String,
        val tags: List<String>,
        val createdAt: Long,
        val locationAddress: String? = null
    )

    /**
     * 搜索结果
     */
    data class SearchResult(
        val memoryId: Long,
        val metadata: VectorMetadata,
        val score: Float,
        val distance: Float
    )

    /**
     * 初始化向量数据库
     * 加载 sqlite-vec 扩展并创建向量表
     */
    suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. 复制 sqlite-vec 扩展文件到应用目录
            val extensionPath = copyExtensionToAppDir()

            // 2. 打开数据库
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            db = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READWRITE or SQLiteDatabase.CREATE_IF_NECESSARY
            )

            // 3. 加载 sqlite-vec 扩展
            loadVecExtension(extensionPath)

            // 4. 创建向量表
            createVectorTable()

            isInitialized = true
            AppLogger.i(TAG, "SQLite-vec initialized successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to initialize sqlite-vec", e)
            Result.failure(e)
        }
    }

    /**
     * 复制 sqlite-vec 扩展文件到应用目录
     */
    private fun copyExtensionToAppDir(): String {
        val extensionName = "libsqlite_vec.so"
        val destFile = File(context.filesDir, extensionName)

        // 如果已存在则跳过
        if (destFile.exists()) {
            return destFile.absolutePath
        }

        // 从 assets 复制
        context.assets.open(extensionName).use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }

        return destFile.absolutePath
    }

    /**
     * 加载 sqlite-vec 扩展
     */
    private fun loadVecExtension(extensionPath: String) {
        // 使用 SQLite 的 load_extension 函数
        // 注意：需要启用扩展加载
        db?.execSQL("SELECT load_extension('$extensionPath')")
    }

    /**
     * 创建向量表
     */
    private fun createVectorTable() {
        // 使用 sqlite-vec 的 vec0 虚拟表
        db?.execSQL("""
            CREATE VIRTUAL TABLE IF NOT EXISTS $COLLECTION_NAME USING vec0(
                memory_id INTEGER PRIMARY KEY,
                embedding FLOAT[$EMBEDDING_DIMENSION],
                content TEXT,
                scene_category TEXT,
                type_category TEXT,
                tags TEXT,
                created_at INTEGER,
                location_address TEXT
            )
        """)

        // 创建元数据索引
        db?.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_vec_scene ON $COLLECTION_NAME(scene_category)
        """)
        db?.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_vec_created ON $COLLECTION_NAME(created_at)
        """)

        AppLogger.d(TAG, "Vector table created")
    }

    /**
     * 添加向量
     *
     * @param memoryId 记忆记录 ID
     * @param embedding 嵌入向量（1024 维）
     * @param metadata 元数据
     */
    suspend fun addEmbedding(
        memoryId: Long,
        embedding: FloatArray,
        metadata: VectorMetadata
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureInitialized()

            // 将 FloatArray 转换为 sqlite-vec 格式（blob）
            val embeddingBlob = floatArrayToBlob(embedding)
            val tagsJson = metadata.tags.joinToString(",", "[", "]")

            db?.execSQL(
                """
                INSERT OR REPLACE INTO $COLLECTION_NAME 
                (memory_id, embedding, content, scene_category, type_category, tags, created_at, location_address)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                arrayOf(
                    memoryId,
                    embeddingBlob,
                    metadata.content,
                    metadata.sceneCategory,
                    metadata.typeCategory,
                    tagsJson,
                    metadata.createdAt,
                    metadata.locationAddress
                )
            )

            AppLogger.d(TAG, "Added embedding for memory $memoryId")
            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to add embedding for memory $memoryId", e)
            Result.failure(e)
        }
    }

    /**
     * 相似度搜索
     *
     * @param queryEmbedding 查询向量
     * @param topK 返回数量
     * @param filter 过滤条件（可选）
     * @return 搜索结果列表
     */
    suspend fun searchSimilar(
        queryEmbedding: FloatArray,
        topK: Int = 20,
        filter: SearchFilter? = null
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        try {
            ensureInitialized()

            val queryBlob = floatArrayToBlob(queryEmbedding)
            val whereClause = buildWhereClause(filter)

            // 使用 sqlite-vec 的 vec_distance_cosine 函数进行余弦相似度搜索
            val sql = """
                SELECT 
                    memory_id,
                    content,
                    scene_category,
                    type_category,
                    tags,
                    created_at,
                    location_address,
                    vec_distance_cosine(embedding, ?) as distance
                FROM $COLLECTION_NAME
                $whereClause
                ORDER BY distance ASC
                LIMIT ?
            """

            val args = buildQueryArgs(queryBlob, filter, topK)
            val cursor = db?.rawQuery(sql, args)

            val results = mutableListOf<SearchResult>()
            cursor?.use {
                while (it.moveToNext()) {
                    val memoryId = it.getLong(0)
                    val content = it.getString(1)
                    val sceneCategory = it.getString(2)
                    val typeCategory = it.getString(3)
                    val tagsStr = it.getString(4)
                    val createdAt = it.getLong(5)
                    val locationAddress = if (it.isNull(6)) null else it.getString(6)
                    val distance = it.getFloat(7)

                    // 解析 tags
                    val tags = parseTags(tagsStr)

                    val metadata = VectorMetadata(
                        memoryId = memoryId,
                        content = content,
                        sceneCategory = sceneCategory,
                        typeCategory = typeCategory,
                        tags = tags,
                        createdAt = createdAt,
                        locationAddress = locationAddress
                    )

                    // 距离转相似度（余弦距离 0-2，相似度 1-(-1)）
                    val similarity = 1 - distance

                    results.add(
                        SearchResult(
                            memoryId = memoryId,
                            metadata = metadata,
                            score = similarity,
                            distance = distance
                        )
                    )
                }
            }

            AppLogger.d(TAG, "Search returned ${results.size} results")
            results
        } catch (e: Exception) {
            AppLogger.e(TAG, "Search failed", e)
            emptyList()
        }
    }

    /**
     * 批量删除向量
     */
    suspend fun deleteEmbeddings(memoryIds: List<Long>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureInitialized()

            val placeholders = memoryIds.joinToString(",") { "?" }
            db?.execSQL(
                "DELETE FROM $COLLECTION_NAME WHERE memory_id IN ($placeholders)",
                memoryIds.map { it.toString() }.toTypedArray()
            )

            AppLogger.d(TAG, "Deleted ${memoryIds.size} embeddings")
            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to delete embeddings", e)
            Result.failure(e)
        }
    }

    /**
     * 关闭数据库连接
     */
    suspend fun close() = withContext(Dispatchers.IO) {
        try {
            db?.close()
            db = null
            isInitialized = false
            AppLogger.d(TAG, "Database closed")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to close database", e)
        }
    }

    /**
     * 确保已初始化
     */
    private suspend fun ensureInitialized() {
        if (!isInitialized) {
            initialize()
        }
    }

    /**
     * FloatArray 转 Blob（sqlite-vec 格式）
     */
    private fun floatArrayToBlob(floatArray: FloatArray): ByteArray {
        // sqlite-vec 使用 little-endian float32 格式
        val byteBuffer = java.nio.ByteBuffer.allocate(floatArray.size * 4)
            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
        floatArray.forEach { byteBuffer.putFloat(it) }
        return byteBuffer.array()
    }

    /**
     * Blob 转 FloatArray
     */
    private fun blobToFloatArray(blob: ByteArray): FloatArray {
        val byteBuffer = java.nio.ByteBuffer.wrap(blob)
            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
        val floatArray = FloatArray(blob.size / 4)
        for (i in floatArray.indices) {
            floatArray[i] = byteBuffer.float
        }
        return floatArray
    }

    /**
     * 解析 tags 字符串
     */
    private fun parseTags(tagsStr: String): List<String> {
        return try {
            // 格式：[tag1,tag2,tag3]
            tagsStr.trim('[', ']', ' ').split(",").filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 构建 WHERE 子句
     */
    private fun buildWhereClause(filter: SearchFilter?): String {
        if (filter == null) return ""

        val conditions = mutableListOf<String>()
        if (filter.sceneCategory != null) {
            conditions.add("scene_category = '${filter.sceneCategory}'")
        }
        if (filter.typeCategory != null) {
            conditions.add("type_category = '${filter.typeCategory}'")
        }
        if (filter.tags != null && filter.tags.isNotEmpty()) {
            val tagsCondition = filter.tags.joinToString(" OR ") { "tags LIKE '%\"$it\"%'" }
            conditions.add("($tagsCondition)")
        }
        if (filter.startTime != null) {
            conditions.add("created_at >= ${filter.startTime}")
        }
        if (filter.endTime != null) {
            conditions.add("created_at <= ${filter.endTime}")
        }

        return if (conditions.isNotEmpty()) {
            "WHERE " + conditions.joinToString(" AND ")
        } else {
            ""
        }
    }

    /**
     * 构建查询参数
     */
    private fun buildQueryArgs(queryBlob: ByteArray, filter: SearchFilter?, topK: Int): Array<String> {
        val args = mutableListOf<String>()
        args.add(android.util.Base64.encodeToString(queryBlob, android.util.Base64.DEFAULT))

        // 添加过滤条件参数（如果需要参数化查询）
        // 当前实现使用字符串拼接，生产环境应改为参数化查询

        args.add(topK.toString())
        return args.toTypedArray()
    }

    /**
     * 搜索过滤条件
     */
    data class SearchFilter(
        val sceneCategory: String? = null,
        val typeCategory: String? = null,
        val tags: List<String>? = null,
        val startTime: Long? = null,
        val endTime: Long? = null
    )

    // ==================== VectorStore 接口实现 ====================

    override suspend fun addEmbedding(
        memoryId: Long,
        embedding: FloatArray,
        metadata: Map<String, Any?>
    ): Unit = withContext(Dispatchers.IO) {
        try {
            ensureInitialized()

            val content = metadata["content"] as? String ?: ""
            val sceneCategory = metadata["sceneCategory"] as? String ?: "其他"
            val typeCategory = metadata["typeCategory"] as? String ?: "其他"
            val tags = metadata["tags"] as? List<String> ?: emptyList()
            val createdAt = (metadata["createdAt"] as? Long) ?: System.currentTimeMillis()
            val locationAddress = metadata["locationAddress"] as? String

            val tagsJson = tags.joinToString(",", "[", "]")
            val embeddingBlob = floatArrayToBlob(embedding)

            db?.execSQL(
                """
                INSERT OR REPLACE INTO $COLLECTION_NAME
                (memory_id, embedding, content, scene_category, type_category, tags, created_at, location_address)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                arrayOf(
                    memoryId,
                    embeddingBlob,
                    content,
                    sceneCategory,
                    typeCategory,
                    tagsJson,
                    createdAt,
                    locationAddress
                )
            )

            AppLogger.d(TAG, "Added embedding for memory $memoryId")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to add embedding for memory $memoryId", e)
        }
    }

    override suspend fun deleteEmbedding(memoryId: Long): Unit = withContext(Dispatchers.IO) {
        try {
            ensureInitialized()

            db?.execSQL(
                "DELETE FROM $COLLECTION_NAME WHERE memory_id = ?",
                arrayOf(memoryId)
            )

            AppLogger.d(TAG, "Deleted embedding for memory $memoryId")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to delete embedding for memory $memoryId", e)
        }
    }

    override suspend fun searchSimilar(
        queryEmbedding: FloatArray,
        topK: Int,
        filter: ((Map<String, Any?>) -> Boolean)?
    ): List<VectorStore.SearchResult> = withContext(Dispatchers.IO) {
        // 简化实现：返回空列表
        // 完整实现需要 sqlite-vec 原生支持
        emptyList()
    }

    override suspend fun getCount(): Int = withContext(Dispatchers.IO) {
        try {
            ensureInitialized()

            val cursor = db?.rawQuery("SELECT COUNT(*) FROM $COLLECTION_NAME", null)
            var count = 0
            cursor?.use {
                if (it.moveToFirst()) {
                    count = it.getInt(0)
                }
            }
            count
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to get count", e)
            0
        }
    }

    override suspend fun clear(): Unit = withContext(Dispatchers.IO) {
        try {
            ensureInitialized()

            db?.execSQL("DELETE FROM $COLLECTION_NAME")
            AppLogger.d(TAG, "Cleared all embeddings")
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to clear embeddings", e)
        }
    }
}
