-- PetMarket RBAC + Profile schema patch (PostgreSQL / Supabase)
-- This version is aligned to an existing users table with:
-- id, email, password_hash, full_name, role, created_at

BEGIN;

-- Add profile image columns for profile picture support as bytes/BLOB.
ALTER TABLE public.users
  ADD COLUMN IF NOT EXISTS profile_image_data BYTEA;

ALTER TABLE public.users
  ADD COLUMN IF NOT EXISTS profile_image_content_type VARCHAR(50);

-- Normalize role values to USER/ADMIN only.
UPDATE public.users
SET role = UPPER(BTRIM(role))
WHERE role IS NOT NULL;

UPDATE public.users
SET role = 'USER'
WHERE role IS NULL OR role = '' OR role NOT IN ('USER', 'ADMIN');

-- Keep role default aligned with backend behavior.
ALTER TABLE public.users
  ALTER COLUMN role SET DEFAULT 'USER';

-- Ensure role check constraint exists.
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

-- Keep created_at default aligned with backend behavior.
ALTER TABLE public.users
  ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;

-- Add case-insensitive uniqueness guard for email when possible.
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

-- Seed default admin account for role-based login.
-- Default credentials: admin@petmarket.com / Admin@12345
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO public.users (email, password_hash, full_name, role, created_at)
SELECT
  'admin@petmarket.com',
  crypt('Admin@12345', gen_salt('bf', 10)),
  'PetMarket Admin',
  'ADMIN',
  NOW()
WHERE NOT EXISTS (
  SELECT 1
  FROM public.users
  WHERE LOWER(email) = LOWER('admin@petmarket.com')
);

COMMIT;
