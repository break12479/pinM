package com.pinmem.pinm.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 记忆记录实体
 * 
 * @param id 记录 ID
 * @param content 文本内容
 * @param mediaType 媒体类型 ('text' | 'image' | 'video')
 * @param mediaPath 媒体文件本地路径
 * @param mediaDescription AI 生成的媒体描述
 * @param createdAt 创建时间戳（毫秒）
 * @param updatedAt 更新时间戳（毫秒）
 * @param locationLat 纬度
 * @param locationLng 经度
 * @param locationAddress 地址文本
 * @param sceneCategory 场景分类：工作/生活/学习/旅行/健康/社交/财务/其他
 * @param typeCategory 类型分类：事件/想法/待办/感悟/引用/其他
 * @param tagsJson Tag JSON 数组
 * @param embedding 向量嵌入 (sqlite-vec, 1024 维 float32)
 * @param embeddingModel 嵌入模型版本
 * @param isDeleted 软删除标记
 * @param deletedAt 删除时间戳
 */
@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "media_type")
    val mediaType: String = "text",

    @ColumnInfo(name = "media_path")
    val mediaPath: String? = null,

    @ColumnInfo(name = "media_description")
    val mediaDescription: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "location_lat")
    val locationLat: Double? = null,

    @ColumnInfo(name = "location_lng")
    val locationLng: Double? = null,

    @ColumnInfo(name = "location_address")
    val locationAddress: String? = null,

    @ColumnInfo(name = "scene_category")
    val sceneCategory: String = "其他",

    @ColumnInfo(name = "type_category")
    val typeCategory: String = "其他",

    @ColumnInfo(name = "tags")
    val tagsJson: String = "[]",

    @ColumnInfo(name = "embedding")
    val embedding: ByteArray? = null,

    @ColumnInfo(name = "embedding_model")
    val embeddingModel: String? = null,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null,

    @ColumnInfo(name = "ai_processed")
    val aiProcessed: Int = 0,

    @ColumnInfo(name = "embedding_pending")
    val embeddingPending: Int = 0
) {
    fun getTags(): List<String> {
        return try {
            Json.decodeFromString(tagsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun toTagsJson(tags: List<String>): String {
        return Json.encodeToString(tags)
    }

    fun hasLocation(): Boolean = locationLat != null && locationLng != null

    /**
     * 获取录入状态
     */
    fun getProcessingStatus(): ProcessingStatus {
        return when {
            aiProcessed == 0 && embeddingPending == 1 -> ProcessingStatus.PENDING
            aiProcessed == 1 && embeddingPending == 0 -> ProcessingStatus.COMPLETED
            else -> ProcessingStatus.PROCESSING
        }
    }

    fun getRelativeTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - createdAt

        return when {
            diff < 60_000 -> "刚刚"
            diff < 3600_000 -> "${diff / 60_000}分钟前"
            diff < 86400_000 -> "${diff / 3600_000}小时前"
            diff < 172800_000 -> "昨天"
            diff < 604800_000 -> "${diff / 86400_000}天前"
            else -> {
                val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date(createdAt))
                date
            }
        }
    }

    fun getFullTime(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(createdAt))
    }
}

/**
 * 处理状态枚举
 */
enum class ProcessingStatus {
    /** 待处理（刚保存，等待 AI 处理） */
    PENDING,
    /** 处理中（AI 正在处理） */
    PROCESSING,
    /** 已完成（AI 处理完成，向量已生成） */
    COMPLETED
}
