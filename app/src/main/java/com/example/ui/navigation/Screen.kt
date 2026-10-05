package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Overview", Icons.Default.Dashboard)
    data object ChartOfAccounts : Screen("accounts", "Accounts", Icons.Default.AccountBalance)
    data object Branches : Screen("branches", "Branches", Icons.Default.LocationCity)
    data object AuditTrail : Screen("audit", "Audit Trail", Icons.Default.History)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val BottomNavItems = listOf(
    Screen.Dashboard,
    Screen.ChartOfAccounts,
    Screen.Branches,
    Screen.AuditTrail,
    Screen.Settings
)
