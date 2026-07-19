package com.realsanjeev.renttracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.realsanjeev.renttracker.ui.addtenant.AddEditTenantScreen
import com.realsanjeev.renttracker.ui.dashboard.DashboardScreen
import com.realsanjeev.renttracker.ui.dashboard.DashboardUiState
import com.realsanjeev.renttracker.ui.dashboard.DashboardViewModel
import com.realsanjeev.renttracker.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object AddTenant : Screen("add_tenant?tenantId={tenantId}") {
        fun createRoute(tenantId: Long = -1L) = "add_tenant?tenantId=$tenantId"
    }
    data object Settings : Screen("settings")
}

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
) {
    data object Dashboard : BottomNavItem(
        route = Screen.Dashboard.route,
        label = "Dashboard",
        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") }
    )

    data object Tenants : BottomNavItem(
        route = Screen.Dashboard.route,
        label = "Tenants",
        icon = { Icon(Icons.Default.People, contentDescription = "Tenants") }
    )

    data object Settings : BottomNavItem(
        route = Screen.Settings.route,
        label = "Settings",
        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
    )
}

@Composable
fun RentTrackerNavHost(
    dashboardViewModel: DashboardViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onToggleLanguage: () -> Unit,
    onClearAllData: () -> Unit,
    onSendReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val bottomNavItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Tenants,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { item.icon() },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    uiState = uiState,
                    onLanguageToggle = onToggleLanguage,
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onAddTenant = {
                        navController.navigate(Screen.AddTenant.createRoute())
                    },
                    onEditTenant = { tenant ->
                        navController.navigate(Screen.AddTenant.createRoute(tenant.id))
                    },
                    onRecordPayment = {
                        android.widget.Toast.makeText(
                            context,
                            context.getString(com.realsanjeev.renttracker.R.string.msg_record_payment_hint),
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSendReminder = onSendReminder,
                    onDeleteTenant = { tenant ->
                        dashboardViewModel.deleteTenant(tenant)
                    }
                )
            }

            composable(
                route = "add_tenant?tenantId={tenantId}",
                arguments = listOf(
                    navArgument("tenantId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                AddEditTenantScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onToggleDarkMode = onToggleDarkMode,
                    onToggleLanguage = onToggleLanguage,
                    onClearAllData = onClearAllData,
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}
