package com.example.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.AccountType
import com.example.data.model.NormalBalance
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.RubyExpense

@Composable
fun ChartOfAccountsScreen(
    viewModel: MainViewModel,
    currentUser: UserEntity?,
    modifier: Modifier = Modifier
) {
    val filteredAccounts by viewModel.filteredAccounts.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val selectedType by viewModel.selectedAccountType.collectAsState()
    val searchQuery by viewModel.accountSearchQuery.collectAsState()
    val showAddDialog by viewModel.showAddAccountDialog.collectAsState()
    val exportedCsv by viewModel.exportedQuickBooksCsv.collectAsState()

    val totalAssets = allAccounts.filter { it.type == AccountType.ASSET }.sumOf { it.currentBalance }
    val totalLiabilities = allAccounts.filter { it.type == AccountType.LIABILITY }.sumOf { it.currentBalance }
    val totalEquity = allAccounts.filter { it.type == AccountType.EQUITY }.sumOf { it.currentBalance }
    val totalRevenue = allAccounts.filter { it.type == AccountType.REVENUE }.sumOf { it.currentBalance }
    val totalCogs = allAccounts.filter { it.type == AccountType.COGS }.sumOf { it.currentBalance }
    val totalExpenses = allAccounts.filter { it.type == AccountType.EXPENSE }.sumOf { it.currentBalance }
    val netIncome = totalRevenue - totalCogs - totalExpenses

    val canModifyAccounts = currentUser?.role in listOf(UserRole.OWNER, UserRole.MANAGER)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with QuickBooks Export button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chart of Accounts",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Full double-entry general ledger dimensions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { viewModel.exportQuickBooksCsv() },
                        modifier = Modifier.testTag("export_qbo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV")
                    }
                }
            }

            // Accounting Equation Status Bar
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Accounting Equation",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Assets = Liabilities + Equity + Net Income",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Assets", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(viewModel.formatCad(totalAssets), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = EmeraldProfit)
                            }
                            Text("=", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.CenterVertically))
                            Column {
                                Text("Liabilities", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(viewModel.formatCad(totalLiabilities), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RubyExpense)
                            }
                            Text("+", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.CenterVertically))
                            Column {
                                Text("Equity", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(viewModel.formatCad(totalEquity), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = InfoBlue)
                            }
                            Text("+", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.CenterVertically))
                            Column {
                                Text("Net Income", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(viewModel.formatCad(netIncome), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.accountSearchQuery.value = it },
                    placeholder = { Text("Search by code, account title, or category...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.accountSearchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_search_input")
                )
            }

            // Account Type Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { viewModel.selectedAccountType.value = null },
                        label = { Text("All (${allAccounts.size})") }
                    )

                    AccountType.entries.forEach { type ->
                        val count = allAccounts.count { it.type == type }
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                viewModel.selectedAccountType.value = if (selectedType == type) null else type
                            },
                            label = { Text("${type.displayName} ($count)") }
                        )
                    }
                }
            }

            // List of Accounts
            items(filteredAccounts, key = { it.code }) { account ->
                AccountCard(
                    account = account,
                    formattedBalance = viewModel.formatCad(account.currentBalance),
                    canDelete = currentUser?.role == UserRole.OWNER && !account.isSystemAccount,
                    onDelete = { viewModel.deleteAccount(account) }
                )
            }
        }

        // Floating Action Button to Add New Account
        if (canModifyAccounts) {
            FloatingActionButton(
                onClick = { viewModel.setAddAccountDialogVisible(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("add_account_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Account")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Account", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Add Account Dialog
    if (showAddDialog) {
        AddAccountDialog(
            onDismiss = { viewModel.setAddAccountDialogVisible(false) },
            onSave = { code, name, type, category, desc, normal ->
                viewModel.addCustomAccount(code, name, type, category, desc, normal)
            }
        )
    }

    // QuickBooks CSV Dialog
    exportedCsv?.let { csv ->
        QuickBooksExportDialog(
            csvContent = csv,
            onDismiss = { viewModel.clearExportedCsv() }
        )
    }
}

@Composable
fun AccountCard(
    account: AccountEntity,
    formattedBalance: String,
    canDelete: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("account_card_${account.code}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = account.code,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = account.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (account.normalBalance) {
                        NormalBalance.DEBIT -> EmeraldProfit.copy(alpha = 0.12f)
                        NormalBalance.CREDIT -> InfoBlue.copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = "${account.type.name} • ${account.normalBalance.name.take(2)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (account.normalBalance) {
                            NormalBalance.DEBIT -> EmeraldProfit
                            NormalBalance.CREDIT -> InfoBlue
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (account.description.isNotBlank()) {
                        Text(
                            text = account.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formattedBalance,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = when (account.type) {
                            AccountType.REVENUE -> EmeraldProfit
                            AccountType.EXPENSE, AccountType.COGS -> RubyExpense
                            AccountType.ASSET -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = "CAD",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (canDelete) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Account",
                            tint = RubyExpense,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
