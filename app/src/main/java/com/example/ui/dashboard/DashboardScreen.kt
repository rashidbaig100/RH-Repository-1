package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BranchEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.AccountType
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.RubyExpense

data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val allowedRoles: List<UserRole>,
    val tag: String
)

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    currentUser: UserEntity?,
    currentBranch: BranchEntity?,
    accounts: List<AccountEntity>,
    auditLogs: List<AuditLogEntity>,
    onNavigateToAccounts: () -> Unit,
    onNavigateToAudit: () -> Unit,
    onNavigateToBranches: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Financial Metrics from Chart of Accounts
    val bankAndCash = accounts.filter { it.code in listOf("1010", "1020") }.sumOf { it.currentBalance }
    val receivables = accounts.filter { it.code in listOf("1050", "1060", "1061", "1062", "1070") }.sumOf { it.currentBalance }
    val inventoryValue = accounts.filter { it.code in listOf("1200", "1210") }.sumOf { it.currentBalance }
    val banquetDepositsHeld = accounts.filter { it.code == "2100" }.sumOf { it.currentBalance }
    val totalRevenueYtd = accounts.filter { it.type == AccountType.REVENUE }.sumOf { it.currentBalance }
    val totalCogsYtd = accounts.filter { it.type == AccountType.COGS }.sumOf { it.currentBalance }
    val totalExpensesYtd = accounts.filter { it.type == AccountType.EXPENSE }.sumOf { it.currentBalance }

    val quickActions = listOf(
        QuickActionItem("Daily Sales", "Dine-In, Takeout, Delivery, Banquet", Icons.Default.TrendingUp, EmeraldProfit, listOf(UserRole.OWNER, UserRole.MANAGER, UserRole.STAFF), "action_sale"),
        QuickActionItem("Supplier Bill", "Sysco, Gordon, Meats, Produce (AP)", Icons.Default.ShoppingCart, InfoBlue, listOf(UserRole.OWNER, UserRole.MANAGER), "action_purchase"),
        QuickActionItem("Operating Expense", "Rent, Utilities, Maintenance, Ads", Icons.Default.ReceiptLong, RubyExpense, listOf(UserRole.OWNER, UserRole.MANAGER), "action_expense"),
        QuickActionItem("Vendor Payment", "Pay AP bills via TD Bank / Cheque", Icons.Default.Payments, GoldAccent, listOf(UserRole.OWNER, UserRole.MANAGER), "action_payment"),
        QuickActionItem("Payroll Run", "FOH & BOH Hours, Wages, CRA taxes", Icons.Default.Groups, Color(0xFF8B5CF6), listOf(UserRole.OWNER, UserRole.MANAGER), "action_payroll"),
        QuickActionItem("Wastage / Spoilage", "Kitchen trim, batch drops, FIFO loss", Icons.Default.DeleteSweep, AmberWarningColor, listOf(UserRole.OWNER, UserRole.MANAGER, UserRole.STAFF), "action_wastage")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_dashboard_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SINGLE-POINT ENTRY HUB",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "CAD System",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Integrated Restaurant & Banquet FP&A",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter any sale, invoice, or expense ONCE. The double-entry engine automatically updates the GL, Balance Sheet, P&L, and KPI models without double-keying.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Financial Position Row
        item {
            Text(
                text = "Live Balance Sheet Highlights (CAD)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSummaryCard(
                    title = "Liquid Cash & Float",
                    value = viewModel.formatCad(bankAndCash),
                    subtitle = "Bank & Till Float",
                    color = EmeraldProfit,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "Receivables Float",
                    value = viewModel.formatCad(receivables),
                    subtitle = "DoorDash, Uber, Cards",
                    color = InfoBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSummaryCard(
                    title = "Food & Bev Stock",
                    value = viewModel.formatCad(inventoryValue),
                    subtitle = "FIFO Inventory Assets",
                    color = GoldAccent,
                    modifier = Modifier.weight(1f)
                )

                MetricSummaryCard(
                    title = "Banquet Trust Deposits",
                    value = viewModel.formatCad(banquetDepositsHeld),
                    subtitle = "Event Advance Liabilities",
                    color = RubyExpense,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Entry Actions (Phase 1 Entry Points)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Entry Portals",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Role: ${currentUser?.role?.name ?: "Staff"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quickActions.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { action ->
                            val isAllowed = currentUser != null && currentUser.role in action.allowedRoles
                            QuickActionCard(
                                item = action,
                                isAllowed = isAllowed,
                                onClick = {
                                    if (isAllowed) {
                                        viewModel.formatCad(100.0) // trigger feedback
                                        // Trigger feedback for Phase 1
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Architecture Pipeline Card
        item {
            OutlinedCard(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Real-Time Pipeline (Star Schema)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Transactions (Sales, AP Bills, Payroll)  ➜  Double-Entry Engine (Debits = Credits)  ➜  General Ledger  ➜  Financial Statements (P&L, Balance Sheet)  ➜  Driver-Based Budget  ➜  KPIs",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilledTonalButton(
                            onClick = onNavigateToAccounts,
                            modifier = Modifier.testTag("nav_to_accounts_btn")
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chart of Accounts (${accounts.size})")
                        }

                        FilledTonalButton(
                            onClick = onNavigateToAudit,
                            modifier = Modifier.testTag("nav_to_audit_btn")
                        ) {
                            Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Audit Trail")
                        }
                    }
                }
            }
        }
    }
}

val AmberWarningColor = Color(0xFFF59E0B)

@Composable
fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickActionCard(
    item: QuickActionItem,
    isAllowed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAllowed) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isAllowed) 1.dp else 0.dp),
        modifier = modifier
            .clickable(enabled = isAllowed) { onClick() }
            .testTag(item.tag)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(item.color.copy(alpha = if (isAllowed) 0.15f else 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isAllowed) item.color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (!isAllowed) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Restricted",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isAllowed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
