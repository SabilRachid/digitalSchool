/*
  # Système de gestion des écoles

  1. Nouvelles Tables
    - `schools` : Informations des écoles
    - `school_subscriptions` : Abonnements des écoles
    - `school_settings` : Paramètres des écoles
    - `school_admins` : Administrateurs des écoles
    - `school_statistics` : Statistiques des écoles
    - `subscription_plans` : Plans d'abonnement
    - `school_payments` : Paiements des écoles

  2. Sécurité
    - Enable RLS sur toutes les tables
    - Politiques d'accès pour super admin et admin d'école
*/

-- Table des écoles
CREATE TABLE IF NOT EXISTS schools (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(255),
    logo_url TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Table des plans d'abonnement
CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    duration_months INTEGER NOT NULL,
    max_students INTEGER,
    max_teachers INTEGER,
    features JSONB,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Table des abonnements des écoles
CREATE TABLE IF NOT EXISTS school_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT REFERENCES schools(id),
    plan_id BIGINT REFERENCES subscription_plans(id),
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    auto_renew BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Table des paramètres des écoles
CREATE TABLE IF NOT EXISTS school_settings (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT REFERENCES schools(id),
    setting_key VARCHAR(100) NOT NULL,
    setting_value TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(school_id, setting_key)
);

-- Table des administrateurs des écoles
CREATE TABLE IF NOT EXISTS school_admins (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT REFERENCES schools(id),
    user_id UUID REFERENCES auth.users(id),
    role VARCHAR(50) DEFAULT 'ADMIN',
    is_primary BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(school_id, user_id)
);

-- Table des statistiques des écoles
CREATE TABLE IF NOT EXISTS school_statistics (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT REFERENCES schools(id),
    stat_date DATE NOT NULL,
    total_students INTEGER DEFAULT 0,
    total_teachers INTEGER DEFAULT 0,
    total_classes INTEGER DEFAULT 0,
    total_courses INTEGER DEFAULT 0,
    active_users INTEGER DEFAULT 0,
    storage_used BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(school_id, stat_date)
);

-- Table des paiements des écoles
CREATE TABLE IF NOT EXISTS school_payments (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT REFERENCES schools(id),
    subscription_id BIGINT REFERENCES school_subscriptions(id),
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMPTZ NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable Row Level Security
ALTER TABLE schools ENABLE ROW LEVEL SECURITY;
ALTER TABLE subscription_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE school_subscriptions ENABLE ROW LEVEL SECURITY;
ALTER TABLE school_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE school_admins ENABLE ROW LEVEL SECURITY;
ALTER TABLE school_statistics ENABLE ROW LEVEL SECURITY;
ALTER TABLE school_payments ENABLE ROW LEVEL SECURITY;

-- Policies for schools
CREATE POLICY "Schools are viewable by super admin and school admins"
    ON schools FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM users u
            WHERE u.id = auth.uid() AND u.role = 'ROLE_SUPER_ADMIN'
        ) OR
        EXISTS (
            SELECT 1 FROM school_admins sa
            WHERE sa.user_id = auth.uid() AND sa.school_id = schools.id
        )
    );

CREATE POLICY "Schools are manageable by super admin"
    ON schools FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM users u
            WHERE u.id = auth.uid() AND u.role = 'ROLE_SUPER_ADMIN'
        )
    );

-- Policies for subscription plans
CREATE POLICY "Subscription plans are viewable by all authenticated users"
    ON subscription_plans FOR SELECT
    USING (auth.uid() IS NOT NULL);

CREATE POLICY "Subscription plans are manageable by super admin"
    ON subscription_plans FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM users u
            WHERE u.id = auth.uid() AND u.role = 'ROLE_SUPER_ADMIN'
        )
    );

-- Policies for school subscriptions
CREATE POLICY "School subscriptions are viewable by super admin and school admins"
    ON school_subscriptions FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM users u
            WHERE u.id = auth.uid() AND u.role = 'ROLE_SUPER_ADMIN'
        ) OR
        EXISTS (
            SELECT 1 FROM school_admins sa
            WHERE sa.user_id = auth.uid() AND sa.school_id = school_subscriptions.school_id
        )
    );

CREATE POLICY "School subscriptions are manageable by super admin"
    ON school_subscriptions FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM users u
            WHERE u.id = auth.uid() AND u.role = 'ROLE_SUPER_ADMIN'
        )
    );

-- Indexes
CREATE INDEX idx_schools_status ON schools(status);
CREATE INDEX idx_schools_city ON schools(city);
CREATE INDEX idx_schools_country ON schools(country);

CREATE INDEX idx_subscription_plans_active ON subscription_plans(is_active);
CREATE INDEX idx_subscription_plans_price ON subscription_plans(price);

CREATE INDEX idx_school_subscriptions_dates ON school_subscriptions(start_date, end_date);
CREATE INDEX idx_school_subscriptions_status ON school_subscriptions(status);

CREATE INDEX idx_school_admins_user ON school_admins(user_id);
CREATE INDEX idx_school_admins_role ON school_admins(role);

CREATE INDEX idx_school_statistics_date ON school_statistics(stat_date);

CREATE INDEX idx_school_payments_date ON school_payments(payment_date);
CREATE INDEX idx_school_payments_status ON school_payments(status);