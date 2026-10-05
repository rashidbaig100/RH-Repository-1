package com.example

import com.example.data.local.SeedData
import com.example.data.model.AccountType
import com.example.data.model.NormalBalance
import com.example.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChartOfAccountsTest {

    @Test
    fun testDefaultAccounts_containAllMajorTypes() {
        val accounts = SeedData.defaultAccounts

        assertTrue("Should contain Assets", accounts.any { it.type == AccountType.ASSET })
        assertTrue("Should contain Liabilities", accounts.any { it.type == AccountType.LIABILITY })
        assertTrue("Should contain Equity", accounts.any { it.type == AccountType.EQUITY })
        assertTrue("Should contain Revenue", accounts.any { it.type == AccountType.REVENUE })
        assertTrue("Should contain COGS", accounts.any { it.type == AccountType.COGS })
        assertTrue("Should contain Operating Expenses", accounts.any { it.type == AccountType.EXPENSE })
    }

    @Test
    fun testDefaultAccounts_codesAreUnique() {
        val codes = SeedData.defaultAccounts.map { it.code }
        val uniqueCodes = codes.toSet()
        assertEquals("Account codes must be strictly unique", codes.size, uniqueCodes.size)
    }

    @Test
    fun testDefaultAccounts_normalBalancesMatchStandards() {
        SeedData.defaultAccounts.forEach { account ->
            when (account.type) {
                AccountType.ASSET -> {
                    // All assets except contra-assets (accumulated depreciation) are Debit
                    if (account.code != "1510") {
                        assertEquals(NormalBalance.DEBIT, account.normalBalance)
                    }
                }
                AccountType.LIABILITY -> assertEquals(NormalBalance.CREDIT, account.normalBalance)
                AccountType.EQUITY -> assertEquals(NormalBalance.CREDIT, account.normalBalance)
                AccountType.REVENUE -> assertEquals(NormalBalance.CREDIT, account.normalBalance)
                AccountType.COGS -> assertEquals(NormalBalance.DEBIT, account.normalBalance)
                AccountType.EXPENSE -> assertEquals(NormalBalance.DEBIT, account.normalBalance)
            }
        }
    }

    @Test
    fun testAccountingEquationReconciliation() {
        val assets = SeedData.defaultAccounts
            .filter { it.type == AccountType.ASSET }
            .sumOf { it.currentBalance }

        val liabilities = SeedData.defaultAccounts
            .filter { it.type == AccountType.LIABILITY }
            .sumOf { it.currentBalance }

        val equity = SeedData.defaultAccounts
            .filter { it.type == AccountType.EQUITY }
            .sumOf { it.currentBalance }

        val revenue = SeedData.defaultAccounts
            .filter { it.type == AccountType.REVENUE }
            .sumOf { it.currentBalance }

        val cogs = SeedData.defaultAccounts
            .filter { it.type == AccountType.COGS }
            .sumOf { it.currentBalance }

        val expenses = SeedData.defaultAccounts
            .filter { it.type == AccountType.EXPENSE }
            .sumOf { it.currentBalance }

        val netIncome = revenue - cogs - expenses
        val totalClaims = liabilities + equity + netIncome

        // Double-entry balance: Assets ($157,040.00) = Liabilities ($40,440.00) + Equity ($112,500.00) + Net Income ($4,100.00)
        assertEquals("Assets must exactly equal Liabilities + Equity + Net Income", assets, totalClaims, 0.01)
    }

    @Test
    fun testUserRoleLevels() {
        assertTrue(UserRole.OWNER.level > UserRole.MANAGER.level)
        assertTrue(UserRole.MANAGER.level > UserRole.STAFF.level)
    }

    @Test
    fun testChannelsIncludeMajorCanadianPlatforms() {
        val channelCodes = SeedData.defaultChannels.map { it.code }
        assertTrue(channelCodes.contains("DOORDASH"))
        assertTrue(channelCodes.contains("UBER_EATS"))
        assertTrue(channelCodes.contains("SKIP_THE_DISHES"))
        assertTrue(channelCodes.contains("BANQUET"))
    }
}
