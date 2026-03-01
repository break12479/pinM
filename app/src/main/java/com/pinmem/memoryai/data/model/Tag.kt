package com.pinmem.pinm.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tag 实体
 * 
 * @param id Tag ID
 * @param name Tag 名称
 * @param embedding Tag 向量（用于相似度检索）
 * @param embeddingModel 嵌入模型版本
 * @param usageCount 使用次数
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 * @param isPreferred 是否推荐 Tag
 * @param aliasOf 别名指向
 */
@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "embedding")
    val embedding: ByteArray? = null,

    @ColumnInfo(name = "embedding_model")
    val embeddingModel: String? = null,

    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 1,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_preferred")
    val isPreferred: Int = 1,

    @ColumnInfo(name = "alias_of")
    val aliasOf: String? = null
)
