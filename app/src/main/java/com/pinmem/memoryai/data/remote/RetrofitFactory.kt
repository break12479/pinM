package com.pinmem.pinm.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Retrofit 客户端工厂
 *
 * 用于创建和管理 Retrofit 实例
 */
object RetrofitFactory {

    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 60L
    private const val WRITE_TIMEOUT_SECONDS = 30L

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * 创建 OkHttpClient
     *
     * @param addLogging 是否添加日志拦截器
     */
    fun createOkHttpClient(addLogging: Boolean = true): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)

        if (addLogging) {
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    /**
     * 创建 Retrofit 实例
     *
     * @param baseUrl 基础 URL
     * @param client OkHttpClient（可选）
     */
    fun createRetrofit(
        baseUrl: String,
        client: OkHttpClient = createOkHttpClient()
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    /**
     * 创建 AI API 服务
     *
     * @param baseUrl 基础 URL
     * @param client OkHttpClient（可选）
     */
    fun createAIApi(
        baseUrl: String,
        client: OkHttpClient = createOkHttpClient()
    ): AIApi {
        val retrofit = createRetrofit(baseUrl, client)
        return retrofit.create(AIApi::class.java)
    }

    /**
     * 获取 JSON 序列化器
     */
    fun getJson(): Json = json
}
