package info.nukoneko.cuc.android.kidspos.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import info.nukoneko.cuc.android.kidspos.ui.main.MainScreen
import info.nukoneko.cuc.android.kidspos.ui.settings.SettingsScreen

const val MainRoute = "main"
const val SettingsRoute = "settings"

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    mainScreen: @Composable (onNavigateToSettings: () -> Unit) -> Unit = { onNavigateToSettings ->
        MainScreen(onNavigateToSettings = onNavigateToSettings)
    },
    settingsScreen: @Composable (onNavigateBack: () -> Unit) -> Unit = { onNavigateBack ->
        SettingsScreen(onNavigateBack = onNavigateBack)
    }
) {
    NavHost(navController = navController, startDestination = MainRoute) {
        composable(MainRoute) { entry ->
            mainScreen {
                if (entry.isReadyForNavigation()) {
                    navController.navigate(SettingsRoute)
                }
            }
        }
        composable(SettingsRoute) { entry ->
            settingsScreen {
                if (entry.isReadyForNavigation()) {
                    navController.popBackStack(SettingsRoute, inclusive = true)
                }
            }
        }
    }
}

// 遷移アニメーション中も同じボタンを続けて押せてしまい、二重の pop でバックスタックが
// 空になると何も描画されなくなるため、RESUMED の間だけ遷移を受け付ける
private fun NavBackStackEntry.isReadyForNavigation(): Boolean =
    lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
