package com.pinmem.pinm.data.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room 类型转换器
 *
 * 支持以下类型转换：
 * - List<String> <-> JSON 字符串（用于 tags 字段）
 * - ByteArray <-> Base64 字符串（用于 embedding 向量）
 * - Long <-> Date（用于时间戳）
 */
class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    // ========== List<String> 转换（用于 tags 字段）==========

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value.isNullOrEmpty()) {
            "[]"
        } else {
            json.encodeToString(value)
        }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrBlank()) {
            emptyList()
        } else {
            try {
                json.decodeFromString(value)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // ========== ByteArray 转换（用于 embedding 向量）==========

    @TypeConverter
    fun fromByteArray(value: ByteArray?): String? {
        return value?.let {
            android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT)
        }
    }

    @TypeConverter
    fun toByteArray(value: String?): ByteArray? {
        return value?.let {
            android.util.Base64.decode(it, android.util.Base64.DEFAULT)
        }
    }

    // ========== FloatArray 转换（用于 embedding 向量）==========

    @TypeConverter
    fun fromFloatArray(value: FloatArray?): String? {
        return value?.let {
            json.encodeToString(it.toList())
        }
    }

    @TypeConverter
    fun toFloatArray(value: String?): FloatArray? {
        return value?.let {
            try {
                val list = json.decodeFromString<List<Float>>(value)
                list.toFloatArray()
            } catch (e: Exception) {
                null
            }
        }
    }

    // ========== Long 转换（用于时间戳）==========

    @TypeConverter
    fun fromTimestamp(value: Long): String {
        return value.toString()
    }

    @TypeConverter
    fun toTimestamp(value: String): Long {
        return value.toLongOrNull() ?: System.currentTimeMillis()
    }
}
