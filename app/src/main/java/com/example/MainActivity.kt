package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.accounts.ChartOfAccountsScreen
import com.example.ui.audit.AuditTrailScreen
import com.example.ui.auth.RoleSwitchDialog
import com.example.ui.branches.BranchManagementScreen
import com.example.ui.components.TopBarAndHeader
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.navigation.BottomNavItems
import com.example.ui.navigation.Screen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.EasternFlavoursTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasternFlavoursTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    val currentUser by viewModel.currentUser.collectAsState()
    val currentBranch by viewModel.currentBranch.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val showRoleDialog by viewModel.showRoleSwitchDialog.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessage()
        }
    }

    // BackHandler: If user is on a secondary screen, return to Dashboard
    BackHandler(enabled = currentScreen != Screen.Dashboard) {
        currentScreen = Screen.Dashboard
    }

    Scaffold(
        topBar = {
            TopBarAndHeader(
                currentBranch = currentBranch,
                currentUser = currentUser,
                onRoleClick = { viewModel.setRoleSwitchDialogVisible(true) },
                onBranchClick = { currentScreen = Screen.Branches }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                BottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(
                    viewModel = viewModel,
                    currentUser = currentUser,
                    currentBranch = currentBranch,
                    accounts = allAccounts,
                    auditLogs = auditLogs,
                    onNavigateToAccounts = { currentScreen = Screen.ChartOfAccounts },
                    onNavigateToAudit = { currentScreen = Screen.AuditTrail },
                    onNavigateToBranches = { currentScreen = Screen.Branches }
                )

                Screen.ChartOfAccounts -> ChartOfAccountsScreen(
                    viewModel = viewModel,
                    currentUser = currentUser
                )

                Screen.Branches -> BranchManagementScreen(
                    viewModel = viewModel
                )

                Screen.AuditTrail -> AuditTrailScreen(
                    viewModel = viewModel
                )

                Screen.Settings -> SettingsScreen(
                    viewModel = viewModel,
                    currentUser = currentUser
                )
            }
        }
    }

    if (showRoleDialog) {
        RoleSwitchDialog(
            users = allUsers,
            currentUser = currentUser,
            onDismiss = { viewModel.setRoleSwitchDialogVisible(false) },
            onUserSelected = { selectedUser ->
                viewModel.switchUserDirectlyForDemo(selectedUser)
            }
        )
    }
}
