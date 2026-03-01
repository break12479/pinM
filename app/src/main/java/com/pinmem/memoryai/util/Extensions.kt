package com.pinmem.pinm.util

/**
 * 扩展函数集合
 */

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * 封装 Flow 的状态处理
 */
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Resource<Nothing>()
}

/**
 * 将 Flow 转换为 Resource 流
 */
fun <T> Flow<T>.asResource(): Flow<Resource<T>> = this
    .map<T, Resource<T>> { Resource.Success(it) }
    .onStart { emit(Resource.Loading) }
    .catch { e -> emit(Resource.Error(e.message ?: "Unknown error", e)) }

/**
 * String 扩展
 */
fun String?.isNullOrEmpty(): Boolean = this == null || this.isBlank()

fun String?.isValidEmail(): Boolean {
    return this != null && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Long 时间戳扩展
 */
fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60_000 -> "刚刚"
        diff < 3600_000 -> "${diff / 60_000}分钟前"
        diff < 86400_000 -> "${diff / 3600_000}小时前"
        diff < 172800_000 -> "昨天"
        diff < 604800_000 -> "${diff / 86400_000}天前"
        else -> {
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date(this))
            date
        }
    }
}

/**
 * List 扩展
 */
fun <T> List<T>.safeGet(index: Int): T? = this.getOrNull(index)
