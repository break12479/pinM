package com.pinmem.memoryai.data.service

import com.pinmem.memoryai.util.Constants

/**
 * AI Prompt 模板管理
 */
object PromptTemplates {

    /**
     * 分类系统 Prompt
     */
    val SYSTEM_CLASSIFICATION = """
        你是一个个人记忆助手的分类专家。请根据用户提供的内容，准确判断记录的场景分类和类型分类。

        分类体系：
        - 场景分类：工作、生活、学习、旅行、健康、社交、财务、其他
        - 类型分类：事件、想法、待办、感悟、引用、其他

        请严格按照以下 JSON 格式返回结果，不要包含其他内容：
        {
          "scene_category": "场景分类",
          "type_category": "类型分类",
          "confidence": 0.95
        }
    """.trimIndent()

    /**
     * Tag 提取系统 Prompt
     */
    val SYSTEM_TAG_EXTRACTION = """
        你是一个个人记忆助手的标签提取专家。请从用户提供的内容中提取关键词作为标签。

        标签提取原则：
        1. 标签应简洁明了（2-5 个字）
        2. 优先复用已有标签（如果语义相似）
        3. 标签应有助于后续检索和分类
        4. 避免过于宽泛或过于具体的标签

        请严格按照以下 JSON 格式返回结果，不要包含其他内容：
        {
          "tags": ["标签 1", "标签 2", "标签 3"]
        }
    """.trimIndent()

    /**
     * 问答系统 Prompt
     */
    val SYSTEM_QA = """
        你是一个个人记忆助手的 AI 助手。请根据用户的历史记录回答问题。

        回答原则：
        1. 如果记录中有明确答案，直接回答并注明依据（记录 ID）
        2. 如果记录不足，诚实告知用户"没有找到相关记录"
        3. 如果需要计算或推理，给出清晰的推理过程
        4. 回答简洁明了，不超过 200 字
        5. 不要编造不存在的信息

        请严格按照以下 JSON 格式返回结果，不要包含其他内容：
        {
          "answer": "答案内容",
          "referenced_ids": [1, 2, 3],
          "confidence": 0.9
        }
    """.trimIndent()

    // ==================== Prompt 构建函数 ====================

    /**
     * 构建分类 Prompt
     *
     * @param content 记录内容
     * @return 完整的分类 Prompt
     */
    fun buildClassificationPrompt(content: String): String {
        return """
            请对以下记录内容进行分类：
            
            ---
            $content
            ---
            
            请只返回 JSON 格式结果。
        """.trimIndent()
    }

    /**
     * 构建 Tag 提取 Prompt
     *
     * @param content 记录内容
     * @param existingTags 已有标签列表
     * @return 完整的 Tag 提取 Prompt
     */
    fun buildTagExtractionPrompt(
        content: String,
        existingTags: List<String>
    ): String {
        val existingTagsText = if (existingTags.isEmpty()) {
            "无"
        } else {
            existingTags.joinToString(", ", prefix = "[", postfix = "]")
        }

        return """
            请从以下记录内容中提取 1-${Constants.MAX_TAGS_PER_MEMORY} 个关键词作为标签。
            
            记录内容：
            ---
            $content
            ---
            
            已有标签：$existingTagsText
            
            请只返回 JSON 格式结果。
        """.trimIndent()
    }

    /**
     * 构建问答 Prompt
     *
     * @param question 用户问题
     * @param currentTime 当前时间
     * @param memories 相关记忆上下文
     * @return 完整的问答 Prompt
     */
    fun buildQAPrompt(
        question: String,
        currentTime: String,
        memories: List<MemoryContext>
    ): String {
        val contextText = if (memories.isEmpty()) {
            "暂无相关记录"
        } else {
            memories.joinToString("\n---\n") { mem ->
                buildString {
                    append("[ID: ${mem.id}] ")
                    append("时间：${formatTimestamp(mem.createdAt)}\n")
                    append("内容：${mem.content}\n")
                    if (mem.tags.isNotEmpty()) {
                        append("标签：${mem.tags.joinToString(", ")}")
                    }
                }
            }
        }

        return """
            当前时间：$currentTime
            
            相关历史记录：
            $contextText
            
            用户问题：$question
            
            请只返回 JSON 格式结果。
        """.trimIndent()
    }

    // ==================== 辅助函数 ====================

    /**
     * 格式化时间戳
     */
    private fun formatTimestamp(timestamp: Long): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        } catch (e: Exception) {
            timestamp.toString()
        }
    }

    /**
     * 获取当前时间字符串
     */
    fun getCurrentTimeString(): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            sdf.format(java.util.Date())
        } catch (e: Exception) {
            System.currentTimeMillis().toString()
        }
    }
}

/**
 * 记忆上下文（用于 RAG 问答）
 *
 * @param id 记录 ID
 * @param content 记录内容
 * @param createdAt 创建时间戳
 * @param tags 标签列表
 * @param sceneCategory 场景分类
 * @param typeCategory 类型分类
 */
data class MemoryContext(
    val id: Long,
    val content: String,
    val createdAt: Long,
    val tags: List<String> = emptyList(),
    val sceneCategory: String = "其他",
    val typeCategory: String = "其他"
)
