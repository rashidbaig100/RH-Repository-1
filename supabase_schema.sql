-- ====================================================================
-- EASTERN FLAVOURS FINANCE - SUPABASE POSTGRESQL SCHEMA WITH RLS
-- Phase 1: Core Master Data, Chart of Accounts, Double-Entry GL & Security
-- ====================================================================

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. ENUMS
CREATE TYPE account_type AS ENUM ('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'COGS', 'EXPENSE');
CREATE TYPE normal_balance AS ENUM ('DEBIT', 'CREDIT');
CREATE TYPE user_role AS ENUM ('OWNER', 'MANAGER', 'STAFF');
CREATE TYPE journal_status AS ENUM ('DRAFT', 'POSTED', 'REVERSED');
CREATE TYPE entry_source AS ENUM ('SALE', 'PURCHASE', 'EXPENSE', 'PAYROLL', 'INVENTORY', 'FIXED_ASSET', 'MANUAL');

-- 3. BRANCHES TABLE (Dimension)
CREATE TABLE IF NOT EXISTS branches (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(50),
    is_banquet_enabled BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. USERS & PROFILES TABLE
CREATE TABLE IF NOT EXISTS app_users (
    id BIGSERIAL PRIMARY KEY,
    auth_user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role user_role NOT NULL DEFAULT 'STAFF',
    pin_code VARCHAR(10) NOT NULL,
    branch_id BIGINT REFERENCES branches(id) ON DELETE SET NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. CHART OF ACCOUNTS (Dimension)
CREATE TABLE IF NOT EXISTS chart_of_accounts (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type account_type NOT NULL,
    normal_balance normal_balance NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT,
    is_system_account BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    current_balance NUMERIC(15, 2) DEFAULT 0.00,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. SALES CHANNELS (Dimension)
CREATE TABLE IF NOT EXISTS sales_channels (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    default_commission_rate NUMERIC(5, 4) DEFAULT 0.0000,
    receivable_account_code VARCHAR(50) REFERENCES chart_of_accounts(code),
    commission_expense_account_code VARCHAR(50) REFERENCES chart_of_accounts(code),
    is_active BOOLEAN DEFAULT TRUE
);

-- 7. JOURNAL ENTRIES (Fact - General Ledger Header)
CREATE TABLE IF NOT EXISTS journal_entries (
    id BIGSERIAL PRIMARY KEY,
    entry_number VARCHAR(100) UNIQUE NOT NULL,
    branch_id BIGINT NOT NULL REFERENCES branches(id) ON DELETE RESTRICT,
    date DATE NOT NULL,
    source entry_source NOT NULL,
    reference_number VARCHAR(255),
    description TEXT NOT NULL,
    total_amount NUMERIC(15, 2) NOT NULL,
    status journal_status DEFAULT 'POSTED',
    posted_by_user_id BIGINT REFERENCES app_users(id),
    posted_at TIMESTAMPTZ DEFAULT NOW(),
    is_period_locked BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 8. JOURNAL LINES (Fact - General Ledger Lines)
CREATE TABLE IF NOT EXISTS journal_lines (
    id BIGSERIAL PRIMARY KEY,
    journal_entry_id BIGINT NOT NULL REFERENCES journal_entries(id) ON DELETE CASCADE,
    account_code VARCHAR(50) NOT NULL REFERENCES chart_of_accounts(code) ON DELETE RESTRICT,
    debit_amount NUMERIC(15, 2) DEFAULT 0.00 CHECK (debit_amount >= 0),
    credit_amount NUMERIC(15, 2) DEFAULT 0.00 CHECK (credit_amount >= 0),
    memo TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 9. PERIOD LOCKS
CREATE TABLE IF NOT EXISTS period_locks (
    year_month VARCHAR(7) PRIMARY KEY, -- e.g. '2026-09'
    is_locked BOOLEAN DEFAULT TRUE,
    locked_by_user_id BIGINT REFERENCES app_users(id),
    locked_at TIMESTAMPTZ DEFAULT NOW(),
    notes TEXT
);

-- 10. AUDIT LOGS (Immutable History)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMPTZ DEFAULT NOW(),
    user_id BIGINT NOT NULL REFERENCES app_users(id),
    user_name VARCHAR(255) NOT NULL,
    user_role VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_identifier VARCHAR(255) NOT NULL,
    details TEXT NOT NULL,
    branch_id BIGINT REFERENCES branches(id)
);

-- ====================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ====================================================================

ALTER TABLE branches ENABLE ROW LEVEL SECURITY;
ALTER TABLE app_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE chart_of_accounts ENABLE ROW LEVEL SECURITY;
ALTER TABLE sales_channels ENABLE ROW LEVEL SECURITY;
ALTER TABLE journal_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE journal_lines ENABLE ROW LEVEL SECURITY;
ALTER TABLE period_locks ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_logs ENABLE ROW LEVEL SECURITY;

-- Helper function to get current user role
CREATE OR REPLACE FUNCTION get_current_user_role()
RETURNS user_role AS $$
    SELECT role FROM app_users WHERE auth_user_id = auth.uid() LIMIT 1;
$$ LANGUAGE sql SECURITY DEFINER;

-- 1. Chart of Accounts: Read by all authenticated users, Modified by Owner & Manager only
CREATE POLICY "Allow read accounts for authenticated users" ON chart_of_accounts
    FOR SELECT TO authenticated USING (true);

CREATE POLICY "Allow modify accounts for Owner and Manager" ON chart_of_accounts
    FOR ALL TO authenticated USING (get_current_user_role() IN ('OWNER', 'MANAGER'));

-- 2. Journal Entries:
CREATE POLICY "Allow read journal entries" ON journal_entries
    FOR SELECT TO authenticated USING (true);

CREATE POLICY "Allow insert journal entries" ON journal_entries
    FOR INSERT TO authenticated WITH CHECK (
        get_current_user_role() IN ('OWNER', 'MANAGER', 'STAFF')
    );

CREATE POLICY "Disallow modifying locked period journal entries" ON journal_entries
    FOR UPDATE TO authenticated USING (
        is_period_locked = false AND get_current_user_role() IN ('OWNER', 'MANAGER')
    );

-- 3. Audit Logs: Append-only for all, No update or delete allowed (Immutable Audit Trail)
CREATE POLICY "Allow insert audit logs" ON audit_logs
    FOR INSERT TO authenticated WITH CHECK (true);

CREATE POLICY "Allow read audit logs for Owner and Manager" ON audit_logs
    FOR SELECT TO authenticated USING (get_current_user_role() IN ('OWNER', 'MANAGER'));

-- 4. Period Locks: Controlled strictly by Owner
CREATE POLICY "Allow read period locks" ON period_locks
    FOR SELECT TO authenticated USING (true);

CREATE POLICY "Allow modify period locks for Owner only" ON period_locks
    FOR ALL TO authenticated USING (get_current_user_role() = 'OWNER');
