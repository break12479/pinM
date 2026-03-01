package com.pinmem.memoryai.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pinmem.memoryai.data.model.AIConfig
import com.pinmem.memoryai.data.model.EmbeddingQueue
import com.pinmem.memoryai.data.model.Memory
import com.pinmem.memoryai.data.model.QAHistory
import com.pinmem.memoryai.data.model.SearchHistory
import com.pinmem.memoryai.data.model.Tag

/**
 * MemoryAI 数据库
 *
 * 包含以下表：
 * - memories: 记忆记录表
 * - ai_config: AI 配置表
 * - tags: 标签表
 * - qa_history: 问答历史表
 * - search_history: 搜索历史表
 * - embedding_queue: 向量嵌入待处理队列
 *
 * 使用 SQLCipher 加密（可选）
 * 支持 sqlite-vec 向量搜索扩展
 */
@Database(
    entities = [
        Memory::class,
        AIConfig::class,
        Tag::class,
        QAHistory::class,
        SearchHistory::class,
        EmbeddingQueue::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MemoryDatabase : RoomDatabase() {

    abstract fun memoryDao(): MemoryDao
    abstract fun aiConfigDao(): AIConfigDao
    abstract fun tagDao(): TagDao
    abstract fun qaHistoryDao(): QAHistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun embeddingQueueDao(): EmbeddingQueueDao

    companion object {
        private const val DATABASE_NAME = "memoryai_database"

        @Volatile
        private var INSTANCE: MemoryDatabase? = null

        /**
         * 获取数据库实例（单例）
         */
        fun getDatabase(context: Context): MemoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoryDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * 关闭数据库
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
