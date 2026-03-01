# pinM - 个人智能记忆助手

<div align="center">

🧠 让珍贵的记忆不再遗忘

[![Platform](https://img.shields.io/badge/platform-Android-blue.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org/)
[![Min SDK](https://img.shields.io/badge/minSdk-26-green.svg)](https://developer.android.com/about/versions/oreo/android-8.0)
[![License](https://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

</div>

---

## 📖 项目简介

**pinM** - 像标签一样 **Pin** 住你的记忆（**M**emory）

pinM 是一款基于 **RAG（检索增强生成）** 技术的个人智能记忆助手，通过 AI 技术帮助用户轻松记录生活点滴，并能像"外置大脑"一样随时回顾和问答。

> 💡 **AI 编程项目**：本项目深度集成大语言模型（LLM）和向量嵌入技术，实现了完整的 RAG 检索增强生成流程，包括 AI 自动分类、智能标签提取、语义搜索和智能问答等功能。

### ✨ 核心特性

- 📝 **轻松记录** - 快速输入，AI 自动分类打标签
- 🔍 **智能检索** - 基于向量相似度的语义搜索，理解意图而非仅关键词匹配
- 💬 **自然问答** - 像聊天一样询问过去的记录，获得有依据的答案
- 🔐 **隐私安全** - 本地加密存储，用户完全掌控数据
- 📱 **离线可用** - 核心功能不依赖网络，数据可手动备份迁移

### 🤖 AI 技术栈

| 组件 | 技术 | 说明 |
|------|------|------|
| **LLM** | Qwen / DeepSeek / GPT | 大语言模型，用于分类、Tag 提取、问答 |
| **Embedding** | text-embedding-v4 / bge-m3 | 向量嵌入模型，将文本转为向量 |
| **RAG** | 检索增强生成 | 先检索相关记忆，再让 LLM 基于上下文回答 |
| **向量搜索** | In-Memory Vector Store | 余弦相似度搜索，支持阈值过滤 |
| **Prompt 工程** | 结构化 Prompt | 详细的指令、示例、JSON 格式约束 |

---

## 🚀 快速开始

### 系统要求

- Android 8.0 (API 26) 及以上
- 至少 100MB 可用存储空间

### 安装步骤

1. **下载 APK**
   - 从 [Releases](https://github.com/your-repo/pinM/releases) 下载最新版本
   - 或使用 Android Studio 编译调试版本

2. **安装应用**
   ```bash
   adb install pinM-debug.apk
   ```

3. **配置 AI**
   - 打开应用 → 设置 → AI 配置
   - 选择服务商（推荐：阿里云百炼）
   - 输入 API Key
   - 保存配置

4. **开始使用**
   - 点击底部"+"按钮新建记录
   - 在问答页面提问，如"我上周去了哪里？"

---

## 🛠️ 技术架构

### 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 语言 | Kotlin | 1.9+ |
| UI 框架 | Jetpack Compose | Latest |
| 架构模式 | MVVM + Clean Architecture | - |
| 数据库 | Room + SQLCipher | 2.6.1 |
| 向量存储 | In-Memory Vector Store | - |
| 网络请求 | Retrofit + OkHttp | 2.9.0 / 4.12.0 |
| 依赖注入 | Koin | 3.5.3 |
| 异步处理 | Kotlin Coroutines + Flow | 1.7.3 |
| JSON 解析 | kotlinx.serialization | 1.6.2 |

### 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                      用户界面层                               │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐               │
│  │ 时间线    │  │ 搜索/问答  │  │ 设置       │               │
│  └───────────┘  └───────────┘  └───────────┘               │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                      业务逻辑层                               │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐               │
│  │记录管理   │  │ 搜索服务   │  │ 问答服务   │               │
│  └───────────┘  └───────────┘  └───────────┘               │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐               │
│  │ AI 服务    │  │ 备份服务   │  │ 位置服务   │               │
│  └───────────┘  └───────────┘  └───────────┘               │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                      数据访问层                               │
│  ┌───────────────────┐  ┌───────────────────┐              │
│  │ Room Database     │  │ Vector Store      │              │
│  └───────────────────┘  └───────────────────┘              │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                      外部服务层                               │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐               │
│  │ 嵌入 API   │  │ LLM API   │  │ 地图 API   │               │
│  └───────────┘  └───────────┘  └───────────┘               │
└─────────────────────────────────────────────────────────────┘
```

### RAG 问答流程

```
用户提问
    ↓
1. 语义搜索相关记忆（向量相似度）
   ├─ 生成查询向量（Embedding API）
   ├─ 向量相似度搜索（InMemoryVectorStore）
   └─ 过滤（相似度阈值 ≥ 0.7）
    ↓
2. 构建上下文（Top 5 相关记忆）
    ↓
3. 调用 LLM 生成答案
   ├─ Prompt 构建（包含上下文 + 问题）
   ├─ 调用 LLM API（qwen-turbo）
   └─ 解析 JSON 响应
    ↓
4. 保存问答历史
    ↓
5. 返回答案（带引用来源）
```

---

## 📁 项目结构

```
pinM/
├── workplace/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/pinmem/memoryai/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/          # 本地数据源
│   │   │   │   │   │   ├── database/   # Room DAO & Database
│   │   │   │   │   │   └── vector/     # 向量存储
│   │   │   │   │   ├── remote/         # 远程 API
│   │   │   │   │   ├── repository/     # Repository 实现
│   │   │   │   │   └── model/          # 数据模型
│   │   │   │   ├── service/            # 业务服务
│   │   │   │   ├── viewmodel/          # ViewModel 层
│   │   │   │   ├── ui/                 # Compose UI
│   │   │   │   │   ├── timeline/       # 时间线界面
│   │   │   │   │   ├── search/         # 搜索界面
│   │   │   │   │   ├── qa/             # 问答界面
│   │   │   │   │   └── settings/       # 设置界面
│   │   │   │   └── util/               # 工具类
│   │   │   └── res/                    # 资源文件
│   │   └── build.gradle.kts
│   └── README.md
```

---

## 🔧 开发指南

### 环境搭建

1. **安装 Android Studio** (推荐最新稳定版)
2. **配置 JDK 17**
3. **克隆项目**
   ```bash
   git clone https://github.com/your-repo/pinM.git
   ```
4. **同步 Gradle**
5. **运行应用**

### 构建命令

```bash
# 调试版本
./gradlew assembleDebug

# 发布版本
./gradlew assembleRelease

# 运行测试
./gradlew test

# 代码格式化
./gradlew ktlintFormat
```

### 配置说明

#### AI 服务商配置

| 服务商 | Base URL | 推荐模型 |
|--------|---------|---------|
| 阿里云百炼 | `https://dashscope.aliyuncs.com/compatible-mode/v1` | qwen-turbo |
| DeepSeek | `https://api.deepseek.com` | deepseek-chat |
| OpenAI | `https://api.openai.com` | gpt-4o-mini |

---

## 📊 功能特性

### 已实现功能

- ✅ 时间线浏览（按时间分组）
- ✅ 新建/编辑/删除记录
- ✅ AI 自动分类（场景 + 类型）
- ✅ AI 自动提取 Tag
- ✅ 语义搜索（向量相似度）
- ✅ 关键词搜索
- ✅ RAG 智能问答
- ✅ 问答历史记录
- ✅ 数据备份/恢复
- ✅ 录入状态显示

### 计划功能

- ⏳ 图片记录支持
- ⏳ 语音输入
- ⏳ 定期回顾推送
- ⏳ 基于位置的提醒
- ⏳ 多设备同步

---

## 🔒 隐私与安全

- **本地存储**：所有数据存储在本地数据库
- **加密备份**：备份文件采用加密格式
- **权限最小化**：仅申请必要权限
- **透明日志**：所有 API 调用都有详细日志

### 权限说明

| 权限 | 用途 | 是否必需 |
|------|------|---------|
| INTERNET | AI API 调用 | 是 |
| ACCESS_NETWORK_STATE | 网络状态检测 | 是 |
| ACCESS_FINE_LOCATION | 自动获取记录位置 | 否 |
| READ/WRITE_EXTERNAL_STORAGE | 备份/恢复文件 | 否 |

---

## 🐛 常见问题

### Q: 问答响应很慢怎么办？

A: 可能是以下原因：
1. 使用了高级模型（如 qwen3.5-plus），建议改用 `qwen-turbo`
2. 网络延迟，检查网络连接
3. 上下文过长，限制历史记录数量

### Q: 提示"没有找到相关记录"？

A: 可能原因：
1. 数据库中确实没有相关记录
2. 语义搜索阈值过高（当前为 0.7）
3. 向量嵌入未生成（检查 AI 配置）

### Q: 如何备份数据？

A: 设置 → 数据备份 → 导出备份文件，保存到云盘或本地

---

## 📝 更新日志

### v1.0.0 (2026-03-01)

**新增功能**
- 🎉 初始版本发布
- 📝 记录管理（新建/编辑/删除）
- 🤖 AI 自动分类和 Tag 提取
- 🔍 语义搜索和关键词搜索
- 💬 RAG 智能问答
- 💾 数据备份/恢复

**优化改进**
- ⚡ 检索质量优化（相似度阈值过滤）
- 📝 Prompt 优化（更详细的指令和元数据）
- ⚠️ 错误处理优化（区分错误类型）
- 📊 添加详细日志输出

**Bug 修复**
- 🐛 修复 JSON 解析错误（Markdown 代码块）
- 🐛 修复 SQL 查询错误（getAllTags）
- 🐛 修复 URL 重复问题（/v1/v1/）

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 开源协议

本项目采用 MIT 协议开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

## 📬 联系方式

- 项目地址：https://github.com/your-repo/pinM
- 问题反馈：https://github.com/your-repo/pinM/issues

---

<div align="center">

**Made with ❤️ by pinM Team**

如果这个项目对你有帮助，请给一个 ⭐️ Star 支持！

</div>
