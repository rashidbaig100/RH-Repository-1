package com.example.data.model

enum class AccountType(val displayName: String, val normalBalance: NormalBalance) {
    ASSET("Assets", NormalBalance.DEBIT),
    LIABILITY("Liabilities", NormalBalance.CREDIT),
    EQUITY("Equity", NormalBalance.CREDIT),
    REVENUE("Revenue", NormalBalance.CREDIT),
    COGS("Cost of Goods Sold (COGS)", NormalBalance.DEBIT),
    EXPENSE("Operating Expenses", NormalBalance.DEBIT)
}

enum class NormalBalance {
    DEBIT,
    CREDIT
}

enum class UserRole(val title: String, val level: Int) {
    OWNER("Owner (Full Access)", 3),
    MANAGER("Manager (Operations & Reports)", 2),
    STAFF("Staff (Sales & Wastage Only)", 1)
}

enum class JournalStatus {
    DRAFT,
    POSTED,
    REVERSED
}

enum class EntrySource(val displayName: String) {
    SALE("Sales Register"),
    PURCHASE("Supplier Bill / AP"),
    EXPENSE("Operating Expense"),
    PAYROLL("Payroll Run"),
    INVENTORY("Inventory FIFO / Wastage"),
    FIXED_ASSET("Depreciation / Capex"),
    MANUAL("Manual Journal")
}
