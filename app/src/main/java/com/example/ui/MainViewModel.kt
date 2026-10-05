package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BranchEntity
import com.example.data.local.entity.ChannelEntity
import com.example.data.local.entity.PeriodLockEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.AccountType
import com.example.data.model.NormalBalance
import com.example.data.model.UserRole
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository

    val allAccounts: StateFlow<List<AccountEntity>>
    val allBranches: StateFlow<List<BranchEntity>>
    val allUsers: StateFlow<List<UserEntity>>
    val allChannels: StateFlow<List<ChannelEntity>>
    val auditLogs: StateFlow<List<AuditLogEntity>>
    val periodLocks: StateFlow<List<PeriodLockEntity>>

    // Current State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentBranch = MutableStateFlow<BranchEntity?>(null)
    val currentBranch: StateFlow<BranchEntity?> = _currentBranch.asStateFlow()

    // Chart of Accounts filter & search
    val selectedAccountType = MutableStateFlow<AccountType?>(null)
    val accountSearchQuery = MutableStateFlow("")

    val filteredAccounts: StateFlow<List<AccountEntity>>

    // Transient UI State
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _showAddAccountDialog = MutableStateFlow(false)
    val showAddAccountDialog: StateFlow<Boolean> = _showAddAccountDialog.asStateFlow()

    private val _showRoleSwitchDialog = MutableStateFlow(false)
    val showRoleSwitchDialog: StateFlow<Boolean> = _showRoleSwitchDialog.asStateFlow()

    private val _exportedQuickBooksCsv = MutableStateFlow<String?>(null)
    val exportedQuickBooksCsv: StateFlow<String?> = _exportedQuickBooksCsv.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = FinanceRepository(database)

        allAccounts = repository.allAccounts
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        allBranches = repository.activeBranches
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        allUsers = repository.allUsers
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        allChannels = repository.allChannels
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        auditLogs = repository.recentAuditLogs
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        periodLocks = repository.periodLocks
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        filteredAccounts = combine(
            allAccounts,
            selectedAccountType,
            accountSearchQuery
        ) { accounts, type, query ->
            accounts.filter { account ->
                val matchesType = type == null || account.type == type
                val matchesQuery = query.isBlank() ||
                        account.code.contains(query, ignoreCase = true) ||
                        account.name.contains(query, ignoreCase = true) ||
                        account.category.contains(query, ignoreCase = true)
                matchesType && matchesQuery
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            repository.ensureDataSeeded()

            allUsers.collect { users ->
                if (_currentUser.value == null && users.isNotEmpty()) {
                    // Default to Owner Rashid for full initial access
                    _currentUser.value = users.firstOrNull { it.role == UserRole.OWNER } ?: users.first()
                }
            }
        }

        viewModelScope.launch {
            allBranches.collect { branches ->
                if (_currentBranch.value == null && branches.isNotEmpty()) {
                    _currentBranch.value = branches.first()
                }
            }
        }
    }

    fun selectBranch(branch: BranchEntity) {
        _currentBranch.value = branch
        _uiMessage.value = "Active branch set to ${branch.name}"
    }

    fun switchUser(user: UserEntity, enteredPin: String): Boolean {
        if (user.pinCode == enteredPin) {
            _currentUser.value = user
            _showRoleSwitchDialog.value = false
            _uiMessage.value = "Switched to ${user.name} (${user.role.title})"
            return true
        } else {
            _uiMessage.value = "Invalid PIN for ${user.name}"
            return false
        }
    }

    fun switchUserDirectlyForDemo(user: UserEntity) {
        _currentUser.value = user
        _showRoleSwitchDialog.value = false
        _uiMessage.value = "Active user: ${user.name} (${user.role.title})"
    }

    fun setAddAccountDialogVisible(visible: Boolean) {
        _showAddAccountDialog.value = visible
    }

    fun setRoleSwitchDialogVisible(visible: Boolean) {
        _showRoleSwitchDialog.value = visible
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun addCustomAccount(
        code: String,
        name: String,
        type: AccountType,
        category: String,
        description: String,
        normalBalance: NormalBalance = type.normalBalance
    ) {
        val user = _currentUser.value ?: return
        if (user.role == UserRole.STAFF) {
            _uiMessage.value = "Access Denied: Staff cannot create accounts."
            return
        }

        viewModelScope.launch {
            // Check if code exists
            val exists = allAccounts.value.any { it.code == code.trim() }
            if (exists) {
                _uiMessage.value = "Account code $code already exists!"
                return@launch
            }

            val newAccount = AccountEntity(
                code = code.trim(),
                name = name.trim(),
                type = type,
                normalBalance = normalBalance,
                category = category.trim().ifBlank { type.displayName },
                description = description.trim(),
                isSystemAccount = false,
                currentBalance = 0.0
            )

            repository.addAccount(newAccount, user)
            _showAddAccountDialog.value = false
            _uiMessage.value = "Account $code - $name successfully created!"
        }
    }

    fun deleteAccount(account: AccountEntity) {
        val user = _currentUser.value ?: return
        if (user.role != UserRole.OWNER) {
            _uiMessage.value = "Only Owners can delete accounts."
            return
        }
        if (account.isSystemAccount) {
            _uiMessage.value = "System accounts cannot be deleted to preserve GL integrity."
            return
        }

        viewModelScope.launch {
            val success = repository.deleteCustomAccount(account.code, user)
            if (success) {
                _uiMessage.value = "Account ${account.code} deleted."
            } else {
                _uiMessage.value = "Could not delete account."
            }
        }
    }

    fun togglePeriodLock(yearMonth: String, currentLockState: Boolean) {
        val user = _currentUser.value ?: return
        if (user.role != UserRole.OWNER) {
            _uiMessage.value = "Security Alert: Only Owners can lock/unlock accounting periods."
            return
        }

        viewModelScope.launch {
            val newState = !currentLockState
            repository.setPeriodLock(
                yearMonth = yearMonth,
                isLocked = newState,
                user = user,
                notes = if (newState) "Closed & audited by ${user.name}" else "Unlocked for adjustment"
            )
            _uiMessage.value = "Period $yearMonth is now ${if (newState) "LOCKED (Read-Only)" else "OPEN"}"
        }
    }

    fun exportQuickBooksCsv(): String {
        val accounts = allAccounts.value
        val sb = StringBuilder()
        sb.append("Account Number,Account Name,Type,Detail Type,Description,Normal Balance,Current Balance (CAD)\n")
        accounts.forEach { acc ->
            val qboType = when (acc.type) {
                AccountType.ASSET -> "Bank / Other Asset"
                AccountType.LIABILITY -> "Other Current Liability"
                AccountType.EQUITY -> "Equity"
                AccountType.REVENUE -> "Income"
                AccountType.COGS -> "Cost of Goods Sold"
                AccountType.EXPENSE -> "Expense"
            }
            sb.append("\"${acc.code}\",\"${acc.name}\",\"$qboType\",\"${acc.category}\",\"${acc.description}\",\"${acc.normalBalance.name}\",${"%.2f".format(Locale.CANADA, acc.currentBalance)}\n")
        }
        val csvString = sb.toString()
        _exportedQuickBooksCsv.value = csvString
        _uiMessage.value = "QuickBooks CSV generated (${accounts.size} accounts export-ready)!"
        return csvString
    }

    fun clearExportedCsv() {
        _exportedQuickBooksCsv.value = null
    }

    fun formatCad(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.CANADA)
        return format.format(amount)
    }
}
