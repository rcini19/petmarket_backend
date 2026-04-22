-- PetMarket feature pack migration for Supabase PostgreSQL
-- Covers: RBAC hardening, pet listings, purchases, trades, admin moderation fields.

BEGIN;

-- =========================
-- users table compatibility
-- =========================
ALTER TABLE public.users
  ADD COLUMN IF NOT EXISTS suspended BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE public.users
  ADD COLUMN IF NOT EXISTS profile_image_data BYTEA;

ALTER TABLE public.users
  ADD COLUMN IF NOT EXISTS profile_image_content_type VARCHAR(50);

UPDATE public.users
SET role = UPPER(BTRIM(role))
WHERE role IS NOT NULL;

UPDATE public.users
SET role = 'USER'
WHERE role IS NULL OR role = '' OR role NOT IN ('USER', 'ADMIN');

ALTER TABLE public.users
  ALTER COLUMN role SET DEFAULT 'USER';

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'users_role_check'
      AND conrelid = 'public.users'::regclass
  ) THEN
    ALTER TABLE public.users
      ADD CONSTRAINT users_role_check CHECK (role IN ('USER', 'ADMIN'));
  END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (
    SELECT LOWER(email)
    FROM public.users
    GROUP BY LOWER(email)
    HAVING COUNT(*) > 1
  ) THEN
    RAISE NOTICE 'Skipping users_email_lower_uq creation because duplicate case-insensitive emails exist.';
  ELSE
    EXECUTE 'CREATE UNIQUE INDEX IF NOT EXISTS users_email_lower_uq ON public.users ((LOWER(email)))';
  END IF;
END$$;

-- =========================
-- pets table
-- =========================
CREATE TABLE IF NOT EXISTS public.pets (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  species VARCHAR(80) NOT NULL,
  breed VARCHAR(120) NOT NULL,
  age INTEGER NOT NULL DEFAULT 0,
  listing_type VARCHAR(20) NOT NULL,
  price NUMERIC(12,2),
  description TEXT,
  image_url TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
  owner_id BIGINT NOT NULL REFERENCES public.users(id) ON DELETE RESTRICT,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITHOUT TIME ZONE
);

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'pets_listing_type_check' AND conrelid = 'public.pets'::regclass
  ) THEN
    ALTER TABLE public.pets
      ADD CONSTRAINT pets_listing_type_check CHECK (listing_type IN ('SALE', 'TRADE', 'BOTH'));
  END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'pets_status_check' AND conrelid = 'public.pets'::regclass
  ) THEN
    ALTER TABLE public.pets
      ADD CONSTRAINT pets_status_check CHECK (status IN ('AVAILABLE', 'SOLD', 'TRADED'));
  END IF;
END$$;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'pets_status_check' AND conrelid = 'public.pets'::regclass
  ) THEN
    ALTER TABLE public.pets DROP CONSTRAINT pets_status_check;
    ALTER TABLE public.pets
      ADD CONSTRAINT pets_status_check CHECK (status IN ('AVAILABLE', 'SOLD', 'TRADED'));
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_pets_owner_id ON public.pets(owner_id);
CREATE INDEX IF NOT EXISTS idx_pets_status ON public.pets(status);
CREATE INDEX IF NOT EXISTS idx_pets_listing_type ON public.pets(listing_type);

-- =========================
-- orders table
-- =========================
DO $$
BEGIN
  IF to_regclass('public.orders') IS NULL AND to_regclass('public.purchases') IS NOT NULL THEN
    ALTER TABLE public.purchases RENAME TO orders;
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS public.orders (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES public.users(id) ON DELETE RESTRICT,
  pet_id BIGINT NOT NULL UNIQUE REFERENCES public.pets(id) ON DELETE RESTRICT,
  order_number VARCHAR(40) UNIQUE,
  total_price NUMERIC(12,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'orders' AND column_name = 'buyer_id'
  ) AND NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'orders' AND column_name = 'user_id'
  ) THEN
    ALTER TABLE public.orders RENAME COLUMN buyer_id TO user_id;
  END IF;
END$$;

ALTER TABLE public.orders
  ADD COLUMN IF NOT EXISTS order_number VARCHAR(40);

ALTER TABLE public.orders
  ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED';

UPDATE public.orders
SET order_number = CONCAT('ORD-', LPAD(CAST(id AS TEXT), 8, '0'))
WHERE order_number IS NULL;

ALTER TABLE public.orders
  ALTER COLUMN order_number SET NOT NULL;

ALTER TABLE public.orders
  ALTER COLUMN status SET DEFAULT 'COMPLETED';

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'orders_status_check' AND conrelid = 'public.orders'::regclass
  ) THEN
    ALTER TABLE public.orders
      ADD CONSTRAINT orders_status_check CHECK (status IN ('COMPLETED', 'CANCELLED'));
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON public.orders(user_id);

-- =========================
-- trade_offers table
-- =========================
DO $$
BEGIN
  IF to_regclass('public.trade_offers') IS NULL AND to_regclass('public.trades') IS NOT NULL THEN
    ALTER TABLE public.trades RENAME TO trade_offers;
  END IF;
END$$;

CREATE TABLE IF NOT EXISTS public.trade_offers (
  id BIGSERIAL PRIMARY KEY,
  offered_pet_id BIGINT NOT NULL REFERENCES public.pets(id) ON DELETE RESTRICT,
  requested_pet_id BIGINT NOT NULL REFERENCES public.pets(id) ON DELETE RESTRICT,
  offered_by_user_id BIGINT NOT NULL REFERENCES public.users(id) ON DELETE RESTRICT,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  responded_at TIMESTAMP WITHOUT TIME ZONE
);

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'trade_offers' AND column_name = 'offering_user_id'
  ) AND NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'trade_offers' AND column_name = 'offered_by_user_id'
  ) THEN
    ALTER TABLE public.trade_offers RENAME COLUMN offering_user_id TO offered_by_user_id;
  END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'trade_offers_status_check' AND conrelid = 'public.trade_offers'::regclass
  ) THEN
    ALTER TABLE public.trade_offers
      ADD CONSTRAINT trade_offers_status_check CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED'));
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_trade_offers_offered_by_user_id ON public.trade_offers(offered_by_user_id);
CREATE INDEX IF NOT EXISTS idx_trade_offers_requested_pet_id ON public.trade_offers(requested_pet_id);
CREATE INDEX IF NOT EXISTS idx_trade_offers_status ON public.trade_offers(status);

-- =========================
-- refresh_tokens table
-- =========================
CREATE TABLE IF NOT EXISTS public.refresh_tokens (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  token VARCHAR(500) NOT NULL UNIQUE,
  expiry_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON public.refresh_tokens(user_id);

-- =========================
-- default admin seed
-- =========================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO public.users (email, password_hash, full_name, role, created_at, suspended)
SELECT
  'admin@petmarket.com',
  crypt('Admin@12345', gen_salt('bf', 10)),
  'PetMarket Admin',
  'ADMIN',
  NOW(),
  FALSE
WHERE NOT EXISTS (
  SELECT 1 FROM public.users WHERE LOWER(email) = LOWER('admin@petmarket.com')
);

COMMIT;
