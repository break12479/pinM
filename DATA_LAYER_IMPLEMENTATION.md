# MemoryAI 数据层开发总结

## 创建的文件列表

### 1. Entity 实体类

| 文件路径 | 说明 |
|---------|------|
| `data/model/EmbeddingQueue.kt` | 向量嵌入待处理队列实体 |

### 2. DAO 数据访问对象

| 文件路径 | 说明 |
|---------|------|
| `data/local/database/EmbeddingQueueDao.kt` | 向量嵌入队列的 CRUD 操作 |

### 3. 向量存储

| 文件路径 | 说明 |
|---------|------|
| `data/local/vector/SQLiteVecVectorStore.kt` | sqlite-vec 向量存储实现 |
| `data/local/vector/VectorStoreFactory.kt` | 向量存储工厂和接口定义 |

### 4. Repository 数据仓库

| 文件路径 | 说明 |
|---------|------|
| `data/repository/SearchRepository.kt` | 搜索数据仓库（语义搜索 + 关键词搜索） |

### 5. 更新的现有文件

| 文件路径 | 更新内容 |
|---------|---------|
| `data/local/database/MemoryDatabase.kt` | 添加 EmbeddingQueue 实体，版本升级到 v2 |
| `data/local/vector/InMemoryVectorStore.kt` | 实现 VectorStore 接口 |
| `di/AppModules.kt` | 添加 SearchRepository 和 EmbeddingQueueDao 注入 |
| `app/build.gradle.kts` | 更新 sqlite-vec 集成说明 |

---

## sqlite-vec 集成方式

### 概述

sqlite-vec 是一个 SQLite 扩展，提供向量相似度搜索功能。MemoryAI 使用它来实现语义搜索功能。

### 集成架构

```
┌─────────────────────────────────────────────────────────┐
│                    MemoryAI Application                  │
├─────────────────────────────────────────────────────────┤
│  Repository Layer                                        │
│  ┌─────────────────┐  ┌─────────────────┐              │
│  │ MemoryRepository│  │ SearchRepository│              │
│  └────────┬────────┘  └────────┬────────┘              │
│           │                    │                        │
│  ┌────────▼────────────────────▼────────┐              │
│  │         VectorStore (Interface)       │              │
│  └────────┬────────────────────┬────────┘              │
│           │                    │                        │
│  ┌────────▼────────┐  ┌────────▼────────┐             │
│  │InMemoryVectorStore│ │SQLiteVecVectorStore│          │
│  │  (临时方案)      │ │  (生产方案)      │             │
│  └─────────────────┘  └────────┬────────┘             │
│                                │                       │
│                    ┌───────────▼───────────┐          │
│                    │   sqlite-vec 扩展     │          │
│                    │   libsqlite_vec.so    │          │
│                    └───────────────────────┘          │
└─────────────────────────────────────────────────────────┘
```

### 集成步骤

#### 1. 下载 sqlite-vec 扩展

从官方发布页面下载对应 ABI 的 native library：
- GitHub: https://github.com/asg017/sqlite-vec/releases
- 或使用包管理器：`cargo install sqlite-vec`

#### 2. 放置 native library

将 `libsqlite_vec.so` 放到项目的 `app/src/main/jniLibs/` 目录：

```
app/src/main/jniLibs/
├── arm64-v8a/
│   └── libsqlite_vec.so
├── armeabi-v7a/
│   └── libsqlite_vec.so
├── x86/
│   └── libsqlite_vec.so
└── x86_64/
    └── libsqlite_vec.so
```

#### 3. 运行时加载扩展

`SQLiteVecVectorStore` 类在初始化时会自动加载扩展：

```kotlin
class SQLiteVecVectorStore(private val context: Context) {
    suspend fun initialize(): Result<Unit> {
        // 1. 复制扩展文件到应用目录
        val extensionPath = copyExtensionToAppDir()
        
        // 2. 打开数据库
        db = SQLiteDatabase.openDatabase(...)
        
        // 3. 加载 sqlite-vec 扩展
        loadVecExtension(extensionPath)
        
        // 4. 创建向量表
        createVectorTable()
    }
}
```

#### 4. 使用向量存储

```kotlin
// 通过工厂创建向量存储
val vectorStore = VectorStoreFactory.create(
    context = context,
    type = VectorStoreFactory.StoreType.SQLITE_VEC
)

// 添加向量
vectorStore.addEmbedding(
    memoryId = 1L,
    embedding = floatArrayOf(0.1f, 0.2f, ...),  // 1024 维
    metadata = InMemoryVectorStore.VectorMetadata(...)
)

// 相似度搜索
val results = vectorStore.searchSimilar(
    queryEmbedding = queryEmbedding,
    topK = 20
)

// 删除向量
vectorStore.deleteEmbedding(memoryId = 1L)
```

### 向量表结构

```sql
CREATE VIRTUAL TABLE memories_embedding USING vec0(
    memory_id INTEGER PRIMARY KEY,
    embedding FLOAT[1024],
    content TEXT,
    scene_category TEXT,
    type_category TEXT,
    tags TEXT,
    created_at INTEGER,
    location_address TEXT
);
```

### 相似度计算

sqlite-vec 支持多种距离度量：
- **余弦距离**: `vec_distance_cosine(embedding, query)`
- **欧几里得距离**: `vec_distance_l2(embedding, query)`
- **点积**: `vec_dot_product(embedding, query)`

MemoryAI 使用余弦相似度，因为：
1. 对向量长度不敏感
2. 适合文本嵌入向量
3. 取值范围固定 [-1, 1]

### 降级策略

当 sqlite-vec 不可用时，自动降级到 `InMemoryVectorStore`：

```kotlin
val storeType = VectorStoreFactory.getRecommendedType(context)
val vectorStore = VectorStoreFactory.create(context, storeType)
```

---

## 数据模型总览

### Entity 关系图

```
┌─────────────────┐     ┌─────────────────┐
│    Memory       │     │      Tag        │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │     │ id (PK)         │
│ content         │     │ name (UNIQUE)   │
│ media_type      │     │ embedding       │
│ created_at      │     │ usage_count     │
│ location_*      │     │ created_at      │
│ scene_category  │     │ updated_at      │
│ type_category   │     └─────────────────┘
│ tags (JSON)     │
│ embedding       │     ┌─────────────────┐
│ is_deleted      │     │   AIConfig      │
└─────────────────┘     ├─────────────────┤
                        │ id (PK)         │
┌─────────────────┐     │ provider        │
│ EmbeddingQueue  │     │ api_key         │
├─────────────────┤     │ base_url        │
│ id (PK)         │     │ embedding_model │
│ memory_id (FK)  │     │ llm_model       │
│ content         │     │ is_active       │
│ retry_count     │     └─────────────────┘
│ created_at      │
│ last_attempt_at │     ┌─────────────────┐
└─────────────────┘     │   QAHistory     │
                        ├─────────────────┤
                        │ id (PK)         │
                        │ question        │
                        │ answer          │
                        │ referenced_ids  │
                        │ created_at      │
                        │ feedback        │
                        └─────────────────┘
```

### 各表用途

| 表名 | 用途 | 关键字段 |
|------|------|---------|
| `memories` | 存储记忆记录 | content, tags, embedding, scene_category, type_category |
| `tags` | 存储标签及使用情况 | name, usage_count, embedding |
| `ai_config` | 存储 AI 服务配置 | provider, api_key, embedding_model, llm_model |
| `embedding_queue` | 待处理的向量嵌入任务 | memory_id, content, retry_count |
| `qa_history` | 问答历史记录 | question, answer, referenced_memory_ids |

---

## Repository 层说明

### MemoryRepository

负责记忆记录的 CRUD 操作和 AI 异步处理：

```kotlin
class MemoryRepository(
    private val memoryDao: MemoryDao,
    private val vectorStore: InMemoryVectorStore,
    private val aiApiService: AIApiService,
    private val aiConfigRepository: AIConfigRepository,
    private val tagRepository: TagRepository,
    private val applicationScope: CoroutineScope
)
```

**主要方法**：
- `createMemory()`: 创建记忆，异步触发 AI 处理
- `updateMemory()`: 更新记忆，重新触发 AI 处理
- `deleteMemory()`: 软删除，同时删除向量
- `semanticSearch()`: 语义搜索

### AIConfigRepository

负责 AI 配置管理：

```kotlin
class AIConfigRepository(
    private val aiConfigDao: AIConfigDao
)
```

**主要方法**：
- `getActiveConfig()`: 获取活跃配置
- `saveConfig()`: 保存并激活配置
- `hasValidConfig()`: 检查是否有有效配置

### TagRepository

负责标签管理：

```kotlin
class TagRepository(
    private val tagDao: TagDao
)
```

**主要方法**：
- `getAllTags()`: 获取所有标签
- `incrementUsage()`: 增加使用次数
- `getTopTags()`: 获取热门标签

### SearchRepository

负责搜索功能（新增）：

```kotlin
class SearchRepository(
    private val memoryDao: MemoryDao,
    private val vectorStore: InMemoryVectorStore,
    private val aiApiService: AIApiService,
    private val aiConfigRepository: AIConfigRepository
)
```

**主要方法**：
- `semanticSearch()`: 语义搜索（向量相似度）
- `keywordSearch()`: 关键词搜索（降级方案）
- `hybridSearch()`: 混合搜索（语义 + 关键词）
- `advancedSearch()`: 高级搜索（支持过滤）

### QARepository

负责问答功能：

```kotlin
class QARepository(
    private val qaHistoryDao: QAHistoryDao,
    private val memoryRepository: MemoryRepository,
    private val aiApiService: AIApiService,
    private val aiConfigRepository: AIConfigRepository,
    private val vectorStore: InMemoryVectorStore
)
```

**主要方法**：
- `askQuestion()`: RAG 问答
- `updateFeedback()`: 更新反馈

---

## 编译说明

### 依赖版本

项目已配置以下关键依赖：

```kotlin
// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Retrofit & OkHttp
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Kotlinx Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

// Koin DI
implementation("io.insert-koin:koin-android:3.5.3")
```

### 编译命令

```bash
cd workplace
./gradlew assembleDebug
```

### 注意事项

1. **KSP 配置**: Room schema 导出路径已配置在 `app/build.gradle.kts`
2. **序列化**: 已启用 Kotlinx Serialization 插件
3. **Java 版本**: 使用 Java 17

---

## 后续工作

### P0 优先级
- [ ] 集成 sqlite-vec native library
- [ ] 测试向量搜索性能
- [ ] 完善错误处理和重试机制

### P1 优先级
- [ ] 添加数据库加密（SQLCipher）
- [ ] 实现备份/恢复功能
- [ ] 添加数据迁移脚本

### P2 优先级
- [ ] 多设备同步支持
- [ ] 向量索引优化
- [ ] 缓存策略优化
