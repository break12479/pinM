package com.pinmem.pinm.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.pinmem.pinm.ui.newmemory.NewMemoryScreen
import com.pinmem.pinm.ui.qa.QAScreen
import com.pinmem.pinm.ui.search.SearchScreen
import com.pinmem.pinm.ui.settings.SettingsScreen
import com.pinmem.pinm.ui.timeline.TimelineScreen
import com.pinmem.pinm.ui.navigation.MemoryDetailScreen

/**
 * 应用导航图
 * 定义所有页面的导航路由
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Timeline.route,
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        // 时间线页面（主页）
        composable(Screen.Timeline.route) {
            TimelineScreen(
                onNavigateToNewMemory = {
                    navController.navigate(Screen.NewMemory.route)
                },
                onNavigateToMemoryDetail = { id ->
                    navController.navigate(Screen.MemoryDetail.createRoute(id))
                }
            )
        }

        // 搜索页面
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToMemoryDetail = { id ->
                    navController.navigate(Screen.MemoryDetail.createRoute(id))
                }
            )
        }

        // 问答页面
        composable(Screen.QA.route) {
            QAScreen()
        }

        // 设置页面
        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        // 新建记录页面
        composable(Screen.NewMemory.route) {
            NewMemoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaved = {
                    // 保存成功后刷新时间线
                    navController.navigate(Screen.Timeline.route) {
                        popUpTo(Screen.Timeline.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        // 编辑记录页面（复用 NewMemoryScreen）
        composable(
            route = Screen.EditMemory.route,
            arguments = listOf(
                navArgument("memoryId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val memoryId = backStackEntry.arguments?.getLong("memoryId") ?: return@composable
            NewMemoryScreen(
                memoryId = memoryId,
                onNavigateBack = { navController.popBackStack() },
                onSaved = {
                    // 保存成功后返回详情页
                    navController.popBackStack()
                }
            )
        }

        // 记忆详情页面
        composable(
            route = Screen.MemoryDetail.route,
            arguments = listOf(
                navArgument("memoryId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val memoryId = backStackEntry.arguments?.getLong("memoryId") ?: return@composable
            MemoryDetailScreen(
                memoryId = memoryId,
                onNavigateBack = { navController.popBackStack() },
                onEdit = { id ->
                    navController.navigate(Screen.EditMemory.createRoute(id))
                }
            )
        }
    }
}

/**
 * 获取当前路由
 */
@Composable
fun NavHostController.currentRoute(): String? {
    val navBackStackEntry by currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

/**
 * 判断当前页面是否显示底部导航
 */
@Composable
fun NavHostController.shouldShowBottomNav(): Boolean {
    val currentRoute = currentRoute()
    return bottomNavItems.any { it.route == currentRoute }
}
