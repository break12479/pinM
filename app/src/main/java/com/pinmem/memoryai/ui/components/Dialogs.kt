package com.pinmem.memoryai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 新建记忆对话框
 */
@Composable
fun NewMemoryDialog(
    content: String,
    onContentChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建记录") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("内容") },
                    placeholder = { Text("记录你的想法、事件或感悟...") },
                    minLines = 5,
                    maxLines = 10,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = content.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * 删除确认对话框
 */
@Composable
fun DeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("确认删除") },
        text = { Text("确定要删除这条记录吗？此操作不可恢复。") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 简单对话框
 */
@Composable
fun SimpleAlertDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss ?: onConfirm,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("确定")
            }
        }
    )
}
