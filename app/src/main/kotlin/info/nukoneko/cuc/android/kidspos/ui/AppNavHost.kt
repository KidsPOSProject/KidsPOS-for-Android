package info.nukoneko.cuc.android.kidspos.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import info.nukoneko.cuc.android.kidspos.ui.log.LogScreen
import info.nukoneko.cuc.android.kidspos.ui.main.MainScreen
import info.nukoneko.cuc.android.kidspos.ui.settings.SettingsScreen
import info.nukoneko.cuc.android.kidspos.ui.startup.StartupScreen

const val StartupRoute = "startup"
const val MainRoute = "main"
const val SettingsRoute = "settings"
const val LogsRoute = "logs"

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startupScreen: @Composable (onNavigateToMain: () -> Unit, onNavigateToSettings: () -> Unit) -> Unit =
        { onNavigateToMain, onNavigateToSettings ->
            StartupScreen(onNavigateToMain = onNavigateToMain, onNavigateToSettings = onNavigateToSettings)
        },
    mainScreen: @Composable (onNavigateToSettings: () -> Unit) -> Unit = { onNavigateToSettings ->
        MainScreen(onNavigateToSettings = onNavigateToSettings)
    },
    settingsScreen: @Composable (onNavigateBack: () -> Unit, onNavigateToLogs: () -> Unit) -> Unit =
        { onNavigateBack, onNavigateToLogs ->
            SettingsScreen(onNavigateBack = onNavigateBack, onNavigateToLogs = onNavigateToLogs)
        },
    logScreen: @Composable (onNavigateBack: () -> Unit) -> Unit = { onNavigateBack ->
        LogScreen(onNavigateBack = onNavigateBack)
    }
) {
    NavHost(navController = navController, startDestination = StartupRoute) {
        composable(StartupRoute) {
            startupScreen(
                {
                    navController.navigate(MainRoute) {
                        popUpTo(StartupRoute) { inclusive = true }
                    }
                },
                {
                    navController.navigate(SettingsRoute) {
                        popUpTo(StartupRoute) { inclusive = true }
                    }
                }
            )
        }
        composable(MainRoute) { entry ->
            mainScreen {
                if (entry.isReadyForNavigation()) {
                    navController.navigate(SettingsRoute)
                }
            }
        }
        composable(SettingsRoute) { entry ->
            settingsScreen(
                {
                    if (entry.isReadyForNavigation()) {
                        if (!navController.popBackStack(MainRoute, inclusive = false)) {
                            navController.navigate(MainRoute) {
                                popUpTo(SettingsRoute) { inclusive = true }
                            }
                        }
                    }
                },
                {
                    if (entry.isReadyForNavigation()) {
                        navController.navigate(LogsRoute)
                    }
                }
            )
        }
        composable(LogsRoute) { entry ->
            logScreen {
                if (entry.isReadyForNavigation()) {
                    navController.popBackStack(LogsRoute, inclusive = true)
                }
            }
        }
    }
}

// 遷移アニメーション中も同じボタンを続けて押せてしまい、二重の pop でバックスタックが
// 空になると何も描画されなくなるため、RESUMED の間だけ遷移を受け付ける
private fun NavBackStackEntry.isReadyForNavigation(): Boolean =
    lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
