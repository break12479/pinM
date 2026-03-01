package com.pinmem.pinm.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 导航路由定义
 */
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasBottomNav: Boolean = true
) {
    object Timeline : Screen(
        route = "timeline",
        title = "时间线",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    )

    object Search : Screen(
        route = "search",
        title = "搜索",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    object QA : Screen(
        route = "qa",
        title = "问答",
        selectedIcon = Icons.Filled.Chat,
        unselectedIcon = Icons.Outlined.Chat
    )

    object Settings : Screen(
        route = "settings",
        title = "设置",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    object NewMemory : Screen(
        route = "new_memory",
        title = "新建记录",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        hasBottomNav = false
    )

    object EditMemory : Screen(
        route = "edit_memory/{memoryId}",
        title = "编辑记录",
        selectedIcon = Icons.Filled.Edit,
        unselectedIcon = Icons.Outlined.Edit,
        hasBottomNav = false
    ) {
        fun createRoute(memoryId: Long) = "edit_memory/$memoryId"
    }

    object MemoryDetail : Screen(
        route = "memory/{memoryId}",
        title = "记忆详情",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List,
        hasBottomNav = false
    ) {
        fun createRoute(memoryId: Long) = "memory/$memoryId"
    }
}

/**
 * 底部导航栏项（仅包含有底部导航的页面）
 */
val bottomNavItems = listOf(
    Screen.Timeline,
    Screen.Search,
    Screen.QA,
    Screen.Settings
)
