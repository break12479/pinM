package com.pinmem.memoryai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pinmem.memoryai.ui.navigation.AppNavGraph
import com.pinmem.memoryai.ui.navigation.Screen
import com.pinmem.memoryai.ui.navigation.bottomNavItems
import com.pinmem.memoryai.ui.theme.MemoryAITheme

/**
 * 主 Activity
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MemoryAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MemoryAIApp()
                }
            }
        }
    }
}

/**
 * MemoryAI 主应用
 * 包含底部导航和导航图
 */
@Composable
fun MemoryAIApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 判断是否显示底部导航
    val shouldShowBottomNav = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav) {
                MemoryAIBottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            startDestination = Screen.Timeline.route,
            innerPadding = paddingValues
        )
    }
}

/**
 * 底部导航栏
 */
@Composable
fun MemoryAIBottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        // 弹出栈中所有相同路由的实例，避免重复
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
