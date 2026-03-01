package com.pinmem.pinm

import android.app.Application
import com.pinmem.pinm.data.repository.AIConfigRepository
import com.pinmem.pinm.data.service.AIService
import com.pinmem.pinm.di.appModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * pinM 应用程序入口
 */
class PinMApplication : Application(), KoinComponent {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        // 初始化 Koin
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@PinMApplication)
            modules(appModules)
        }

        // 初始化 AI 服务配置
        initAIService()
    }

    /**
     * 初始化 AI 服务配置
     * 从数据库加载 AI 配置并设置到 AIService
     */
    private fun initAIService() {
        val aiConfigRepository: AIConfigRepository by inject()
        val aiService: AIService by inject()

        applicationScope.launch(Dispatchers.IO) {
            try {
                val config = aiConfigRepository.getActiveConfig()
                config?.let {
                    aiService.setConfig(it)
                }
            } catch (e: Exception) {
                // 忽略初始化错误
            }
        }
    }
}
