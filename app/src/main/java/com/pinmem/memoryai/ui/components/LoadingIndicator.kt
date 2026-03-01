package com.pinmem.memoryai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

/**
 * 加载指示器组件
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            strokeWidth = 3.dp
        )
    }
}

/**
 * 小型加载指示器（用于按钮等）
 */
@Composable
fun LoadingIndicatorSmall(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier.size(20.dp),
        strokeWidth = 2.5.dp
    )
}

/**
 * 旋转加载图标
 */
@Composable
fun RotatingLoadingIcon(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Icon(
        imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
        contentDescription = "加载中",
        modifier = modifier
            .size(24.dp)
            .rotate(rotation),
        tint = MaterialTheme.colorScheme.primary
    )
}
