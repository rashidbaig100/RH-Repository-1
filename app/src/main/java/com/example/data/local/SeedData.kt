package com.example.data.local

import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.BranchEntity
import com.example.data.local.entity.ChannelEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.JournalLineEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.AccountType
import com.example.data.model.EntrySource
import com.example.data.model.JournalStatus
import com.example.data.model.NormalBalance
import com.example.data.model.UserRole

object SeedData {
    val defaultAccounts = listOf(
        // ASSETS (1000 - 1999)
        AccountEntity("1010", "Operating Bank Account (CAD)", AccountType.ASSET, NormalBalance.DEBIT, "Cash & Equivalents", "TD Commercial Checking Account", isSystemAccount = true, currentBalance = 64700.00),
        AccountEntity("1020", "Cash Drawer / Till Float", AccountType.ASSET, NormalBalance.DEBIT, "Cash & Equivalents", "Front of House & Bar cash float", isSystemAccount = true, currentBalance = 1500.00),
        AccountEntity("1050", "Card Settlement Clearing", AccountType.ASSET, NormalBalance.DEBIT, "Receivables & Clearing", "Moneris / Stripe POS daily card transit", isSystemAccount = true, currentBalance = 4250.00),
        AccountEntity("1060", "DoorDash Clearing / Receivable", AccountType.ASSET, NormalBalance.DEBIT, "Receivables & Clearing", "DoorDash weekly net settlement payout", isSystemAccount = true, currentBalance = 3120.00),
        AccountEntity("1061", "Uber Eats Clearing / Receivable", AccountType.ASSET, NormalBalance.DEBIT, "Receivables & Clearing", "Uber Eats weekly net settlement payout", isSystemAccount = true, currentBalance = 2480.00),
        AccountEntity("1062", "SkipTheDishes Receivable", AccountType.ASSET, NormalBalance.DEBIT, "Receivables & Clearing", "SkipTheDishes weekly direct deposit", isSystemAccount = true, currentBalance = 1890.00),
        AccountEntity("1070", "Banquet Receivables (Event Balances)", AccountType.ASSET, NormalBalance.DEBIT, "Receivables & Clearing", "Invoiced corporate & wedding banquet balances", isSystemAccount = true, currentBalance = 6500.00),
        AccountEntity("1200", "Food Inventory", AccountType.ASSET, NormalBalance.DEBIT, "Inventory", "Meats, poultry, dairy, produce, spices & dry stock", isSystemAccount = true, currentBalance = 14200.00),
        AccountEntity("1210", "Beverage Inventory", AccountType.ASSET, NormalBalance.DEBIT, "Inventory", "Soft drinks, juices, specialty mocktails, tea & syrups", isSystemAccount = true, currentBalance = 3800.00),
        AccountEntity("1500", "Commercial Kitchen & Banquet Equipment", AccountType.ASSET, NormalBalance.DEBIT, "Fixed Assets", "Tandoor ovens, combi-steamers, walk-in chillers, chafing sets", isSystemAccount = true, currentBalance = 85000.00),
        AccountEntity("1510", "Accumulated Depreciation - Equipment", AccountType.ASSET, NormalBalance.CREDIT, "Fixed Assets", "Contra-asset: cumulative monthly straight-line depreciation", isSystemAccount = true, currentBalance = -14200.00),

        // LIABILITIES (2000 - 2999)
        AccountEntity("2010", "Accounts Payable (Suppliers/Vendors)", AccountType.LIABILITY, NormalBalance.CREDIT, "Current Liabilities", "Sysco, Gordon Food Service, local meat & halal suppliers", isSystemAccount = true, currentBalance = 11400.00),
        AccountEntity("2100", "Banquet Deposits Held in Trust", AccountType.LIABILITY, NormalBalance.CREDIT, "Current Liabilities", "Customer advance deposits for future weddings and galas", isSystemAccount = true, currentBalance = 18500.00),
        AccountEntity("2200", "Sales Tax Payable (HST/GST)", AccountType.LIABILITY, NormalBalance.CREDIT, "Current Liabilities", "Harmonized Sales Tax collected less input tax credits (ITCs)", isSystemAccount = true, currentBalance = 6420.00),
        AccountEntity("2250", "Payroll Tax Liabilities", AccountType.LIABILITY, NormalBalance.CREDIT, "Current Liabilities", "CRA payroll source deductions: CPP, EI, tax withholdings", isSystemAccount = true, currentBalance = 4120.00),

        // EQUITY (3000 - 3999)
        AccountEntity("3010", "Owner's Capital", AccountType.EQUITY, NormalBalance.CREDIT, "Equity", "Initial capital injected by owners", isSystemAccount = true, currentBalance = 75000.00),
        AccountEntity("3020", "Retained Earnings", AccountType.EQUITY, NormalBalance.CREDIT, "Equity", "Cumulative net earnings reinvested in the business", isSystemAccount = true, currentBalance = 37500.00),

        // REVENUE (4000 - 4999)
        AccountEntity("4010", "Dine-In Food & Beverage Sales", AccountType.REVENUE, NormalBalance.CREDIT, "Operating Revenue", "Dining room table service revenues", isSystemAccount = true, currentBalance = 45200.00),
        AccountEntity("4020", "Takeout & Curbside Sales", AccountType.REVENUE, NormalBalance.CREDIT, "Operating Revenue", "Direct pickup and counter orders", isSystemAccount = true, currentBalance = 16800.00),
        AccountEntity("4030", "Third-Party Delivery Sales", AccountType.REVENUE, NormalBalance.CREDIT, "Operating Revenue", "Gross sales through DoorDash, Uber Eats, Skip", isSystemAccount = true, currentBalance = 24500.00),
        AccountEntity("4040", "Banquet & Event Booking Revenue", AccountType.REVENUE, NormalBalance.CREDIT, "Operating Revenue", "Completed wedding, corporate, and private hall events", isSystemAccount = true, currentBalance = 38900.00),
        AccountEntity("4050", "Gratuities & Service Charges", AccountType.REVENUE, NormalBalance.CREDIT, "Other Revenue", "Auto-gratuity for large parties and banquets", isSystemAccount = true, currentBalance = 5400.00),

        // COGS (5000 - 5999)
        AccountEntity("5010", "Food Cost - Meat, Poultry & Seafood", AccountType.COGS, NormalBalance.DEBIT, "Direct Food Cost", "Halal lamb, beef, chicken, prawns, and fish", isSystemAccount = true, currentBalance = 22400.00),
        AccountEntity("5020", "Food Cost - Dairy, Produce & Dry Goods", AccountType.COGS, NormalBalance.DEBIT, "Direct Food Cost", "Basmati rice, ghee, paneer, spices, fresh vegetables", isSystemAccount = true, currentBalance = 10800.00),
        AccountEntity("5030", "Beverage Cost", AccountType.COGS, NormalBalance.DEBIT, "Direct Beverage Cost", "Dairy, mango pulp, tea leaves, soft drinks", isSystemAccount = true, currentBalance = 3100.00),
        AccountEntity("5040", "Packaging & Disposables", AccountType.COGS, NormalBalance.DEBIT, "Direct Packaging", "Takeout containers, delivery bags, cutlery sets", isSystemAccount = true, currentBalance = 2450.00),
        AccountEntity("5050", "Kitchen Wastage & Spoilage", AccountType.COGS, NormalBalance.DEBIT, "Inventory Variance", "Logged kitchen trim waste, expired prep, batch drops", isSystemAccount = true, currentBalance = 1250.00),

        // OPERATING EXPENSES (6000 - 6999)
        AccountEntity("6010", "Front of House Wages", AccountType.EXPENSE, NormalBalance.DEBIT, "Labor Costs", "Servers, hosts, bussers, bartenders", isSystemAccount = true, currentBalance = 14200.00),
        AccountEntity("6020", "Back of House / Kitchen Wages", AccountType.EXPENSE, NormalBalance.DEBIT, "Labor Costs", "Head chef, tandoor cooks, prep cooks, dishwashers", isSystemAccount = true, currentBalance = 19600.00),
        AccountEntity("6030", "Banquet Event Staff Wages", AccountType.EXPENSE, NormalBalance.DEBIT, "Labor Costs", "Hourly event servers and banquet hall setup crew", isSystemAccount = true, currentBalance = 6300.00),
        AccountEntity("6040", "Employer Payroll Taxes (CPP/EI/EHT)", AccountType.EXPENSE, NormalBalance.DEBIT, "Labor Costs", "Mandatory statutory contributions", isSystemAccount = true, currentBalance = 3800.00),
        AccountEntity("6110", "Delivery Platform Commissions", AccountType.EXPENSE, NormalBalance.DEBIT, "Selling & Marketing", "DoorDash (25%), Uber (28%), Skip (22%) commissions", isSystemAccount = true, currentBalance = 6350.00),
        AccountEntity("6120", "Merchant Card Processing Fees", AccountType.EXPENSE, NormalBalance.DEBIT, "Financial Expenses", "Interac, Visa, Mastercard processing rates", isSystemAccount = true, currentBalance = 1680.00),
        AccountEntity("6210", "Restaurant Rent & Occupancy", AccountType.EXPENSE, NormalBalance.DEBIT, "Occupancy", "Base monthly rent and common area maintenance (TMI)", isSystemAccount = true, currentBalance = 9500.00),
        AccountEntity("6220", "Utilities (Gas, Hydro, Water)", AccountType.EXPENSE, NormalBalance.DEBIT, "Occupancy", "Commercial gas stoves, HVAC, water usage", isSystemAccount = true, currentBalance = 3200.00),
        AccountEntity("6230", "Cleaning & Restaurant Supplies", AccountType.EXPENSE, NormalBalance.DEBIT, "Operating Overhead", "Sanitation chemicals, linen services, paper goods", isSystemAccount = true, currentBalance = 1450.00),
        AccountEntity("6240", "Repairs & Maintenance", AccountType.EXPENSE, NormalBalance.DEBIT, "Operating Overhead", "Hood cleaning, refrigeration servicing, tandoor repair", isSystemAccount = true, currentBalance = 890.00),
        AccountEntity("6250", "Marketing & Local Advertising", AccountType.EXPENSE, NormalBalance.DEBIT, "Selling & Marketing", "Social ads, Google business promotion, event flyers", isSystemAccount = true, currentBalance = 1100.00),
        AccountEntity("6260", "Commercial Insurance & Licenses", AccountType.EXPENSE, NormalBalance.DEBIT, "Administrative", "Liability insurance, city health licenses", isSystemAccount = true, currentBalance = 1250.00),
        AccountEntity("6270", "Equipment Depreciation Expense", AccountType.EXPENSE, NormalBalance.DEBIT, "Non-Cash Expenses", "Monthly straight-line depreciation allowance", isSystemAccount = true, currentBalance = 1180.00)
    )

    val defaultBranches = listOf(
        BranchEntity(
            id = 1,
            name = "Eastern Flavours - Main Dining & Banquets",
            code = "BR-01",
            address = "124 King Street West, Toronto, ON",
            phone = "(416) 555-0199",
            isBanquetEnabled = true
        ),
        BranchEntity(
            id = 2,
            name = "Eastern Flavours - Express & Delivery Kitchen",
            code = "BR-02",
            address = "780 Lawrence Ave East, North York, ON",
            phone = "(416) 555-0244",
            isBanquetEnabled = false
        )
    )

    val defaultUsers = listOf(
        UserEntity(
            id = 1,
            name = "Rashid (Owner)",
            email = "rashid@easternflavours.ca",
            role = UserRole.OWNER,
            pinCode = "1234",
            branchId = null
        ),
        UserEntity(
            id = 2,
            name = "Priya (General Manager)",
            email = "priya@easternflavours.ca",
            role = UserRole.MANAGER,
            pinCode = "2345",
            branchId = 1
        ),
        UserEntity(
            id = 3,
            name = "Tariq (Cashier & Floor Staff)",
            email = "tariq@easternflavours.ca",
            role = UserRole.STAFF,
            pinCode = "3456",
            branchId = 1
        )
    )

    val defaultChannels = listOf(
        ChannelEntity("DINE_IN", "Dine-In Service", 0.0, "1050", "6120"),
        ChannelEntity("TAKEOUT", "Takeout & Counter Pickup", 0.0, "1050", "6120"),
        ChannelEntity("DOORDASH", "DoorDash Marketplace", 0.25, "1060", "6110"),
        ChannelEntity("UBER_EATS", "Uber Eats Delivery", 0.28, "1061", "6110"),
        ChannelEntity("SKIP_THE_DISHES", "SkipTheDishes", 0.22, "1062", "6110"),
        ChannelEntity("BANQUET", "Banquet Hall & Event Bookings", 0.0, "1070", "6120")
    )
}
