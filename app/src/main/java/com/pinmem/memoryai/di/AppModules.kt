package com.pinmem.memoryai.di

import com.pinmem.memoryai.data.local.database.MemoryDatabase
import com.pinmem.memoryai.data.local.vector.InMemoryVectorStore
import com.pinmem.memoryai.data.remote.AIApiService
import com.pinmem.memoryai.data.remote.AIApiServiceImpl
import com.pinmem.memoryai.data.remote.RetrofitFactory
import com.pinmem.memoryai.data.repository.*
import com.pinmem.memoryai.data.service.AIService
import com.pinmem.memoryai.data.service.AIServiceImpl
import com.pinmem.memoryai.viewmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 数据库模块
 */
val databaseModule = module {
    single { MemoryDatabase.getDatabase(androidContext()) }
    single { get<MemoryDatabase>().memoryDao() }
    single { get<MemoryDatabase>().aiConfigDao() }
    single { get<MemoryDatabase>().tagDao() }
    single { get<MemoryDatabase>().qaHistoryDao() }
    single { get<MemoryDatabase>().searchHistoryDao() }
    single { get<MemoryDatabase>().embeddingQueueDao() }
}

/**
 * 向量存储模块
 */
val vectorStoreModule = module {
    single { InMemoryVectorStore() }
}

/**
 * API 服务模块
 * 
 * 提供 Retrofit AIApi 和旧版 AIApiService 兼容
 */
val apiModule = module {
    // Retrofit AIApi（新）
    single { RetrofitFactory.createAIApi("https://api.deepseek.com") }
    
    // 旧版 AIApiService（兼容）
    single<AIApiService> { AIApiServiceImpl() }
    
    // 新版 AIService（业务层）
    single<AIService> { AIServiceImpl(get()) }
}

/**
 * Repository 模块
 */
val repositoryModule = module {
    // MemoryRepository 需要 Application Scope
    factory {
        MemoryRepository(
            memoryDao = get(),
            vectorStore = get(),
            aiApiService = get(),
            aiConfigRepository = get(),
            tagRepository = get(),
            applicationScope = get()
        )
    }

    // AIConfigRepository
    single { AIConfigRepository(get()) }

    // TagRepository
    single { TagRepository(get()) }

    // QARepository
    factory {
        QARepository(
            qaHistoryDao = get(),
            memoryRepository = get(),
            aiApiService = get(),
            aiConfigRepository = get(),
            vectorStore = get()
        )
    }

    // SearchRepository
    factory {
        SearchRepository(
            memoryDao = get(),
            vectorStore = get(),
            aiApiService = get(),
            aiConfigRepository = get(),
            searchHistoryDao = get()
        )
    }

    // BackupRepository
    single { BackupRepository(androidContext(), get()) }
}

/**
 * ViewModel 模块
 */
val viewModelModule = module {
    viewModel { TimelineViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { QAViewModel(get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { NewMemoryViewModel(get()) }
    viewModel { MemoryDetailViewModel(get()) }
}

/**
 * 应用范围 CoroutineScope
 * 用于后台任务处理
 */
val appModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
}

/**
 * 所有模块列表
 */
val appModules = listOf(
    databaseModule,
    vectorStoreModule,
    apiModule,
    repositoryModule,
    viewModelModule,
    appModule
)
