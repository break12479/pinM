package com.pinmem.memoryai.data.local.vector

import android.content.Context
import com.pinmem.memoryai.util.AppLogger

/**
 * 向量存储工厂
 *
 * 用于创建和管理向量存储实例
 * 支持 InMemoryVectorStore 和 SQLiteVecVectorStore 两种实现
 */
object VectorStoreFactory {

    private const val TAG = "VectorStoreFactory"

    /**
     * 向量存储类型
     */
    enum class StoreType {
        IN_MEMORY,      // 内存存储（临时方案）
        SQLITE_VEC     // SQLite-vec（生产方案）
    }

    /**
     * 创建向量存储实例
     */
    suspend fun create(
        context: Context,
        type: StoreType = StoreType.IN_MEMORY
    ): VectorStore {
        return when (type) {
            StoreType.IN_MEMORY -> {
                AppLogger.d(TAG, "Creating InMemoryVectorStore")
                InMemoryVectorStore()
            }
            StoreType.SQLITE_VEC -> {
                AppLogger.d(TAG, "Creating SQLiteVecVectorStore")
                SQLiteVecVectorStore(context).also {
                    it.initialize()
                }
            }
        }
    }

    /**
     * 检查 SQLite-vec 是否可用
     */
    fun isSqliteVecAvailable(context: Context): Boolean {
        val extensionFile = context.filesDir.resolve("libsqlite_vec.so")
        val available = extensionFile.exists()

        if (!available) {
            AppLogger.d(TAG, "sqlite-vec extension not found, using InMemoryVectorStore")
        }

        return available
    }

    /**
     * 获取推荐的存储类型
     */
    fun getRecommendedType(context: Context): StoreType {
        return if (isSqliteVecAvailable(context)) {
            StoreType.SQLITE_VEC
        } else {
            StoreType.IN_MEMORY
        }
    }
}
