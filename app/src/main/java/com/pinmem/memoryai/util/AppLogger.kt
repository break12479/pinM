package com.pinmem.memoryai.util

import android.util.Log

/**
 * 应用日志工具
 */
object AppLogger {
    private const val TAG = "MemoryAI"
    private const val DEBUG = true  // 发布时设为 false

    fun d(message: String) {
        if (DEBUG) Log.d(TAG, message)
    }

    fun d(tag: String, message: String) {
        if (DEBUG) Log.d("$TAG-$tag", message)
    }

    fun i(message: String) {
        if (DEBUG) Log.i(TAG, message)
    }

    fun i(tag: String, message: String) {
        if (DEBUG) Log.i("$TAG-$tag", message)
    }

    fun w(message: String, throwable: Throwable? = null) {
        if (DEBUG) {
            if (throwable != null) {
                Log.w(TAG, message, throwable)
            } else {
                Log.w(TAG, message)
            }
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e("$TAG-$tag", message, throwable)
        } else {
            Log.e("$TAG-$tag", message)
        }
    }
}
