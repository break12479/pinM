package com.pinmem.memoryai.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Tag 标签组件
 *
 * @param tag Tag 文本
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun TagChip(
    tag: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { onClick?.invoke() },
        label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
        shape = RoundedCornerShape(16.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier,
        enabled = onClick != null
    )
}

/**
 * Tag 标签列表组件
 *
 * @param tags Tag 列表
 * @param onTagClick Tag 点击回调
 * @param modifier 修饰符
 */
@Composable
fun TagChipList(
    tags: List<String>,
    onTagClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            TagChip(
                tag = tag,
                onClick = onTagClick?.let { { it(tag) } }
            )
        }
    }
}
