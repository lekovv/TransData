-- initial migration

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public."admin" (
  id uuid DEFAULT uuid_generate_v4() NOT NULL,
  username varchar(255) NOT NULL,
  "password" varchar(255) NOT NULL,
  CONSTRAINT admin_pkey PRIMARY KEY (id),
  CONSTRAINT admin_username_key UNIQUE (username)
);

INSERT INTO public."admin" (username, "password") VALUES ('admin', 'admin');

CREATE TABLE IF NOT EXISTS public."user" (
  id uuid NOT NULL,
  email varchar(255) NOT NULL,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  country varchar(255) NOT NULL,
  created timestamptz NOT NULL,
  CONSTRAINT user_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public."transaction" (
  id uuid NOT NULL,
  user_id uuid NOT NULL,
  amount numeric(15, 2) NOT NULL,
  transaction_type varchar(50) NOT NULL,
  description text NULL,
  created timestamptz NOT NULL,
  CONSTRAINT transaction_pkey PRIMARY KEY (id),
  CONSTRAINT transaction_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id)
);

CREATE TABLE IF NOT EXISTS public.amount (
  total_amount numeric(20, 2) NOT NULL,
  avg_amount numeric(20, 2) NOT NULL,
  created date NOT NULL
);

CREATE TABLE IF NOT EXISTS public.country_stats (
  country varchar(50) NOT NULL,
  transaction_count int4 NOT NULL,
  total_amount numeric(20, 2) NOT NULL,
  created date NOT NULL
);

CREATE TABLE IF NOT EXISTS public.top_users (
  fio varchar(255) NULL,
  email varchar(50) NULL,
  total_user_amount numeric(20, 2) NOT NULL,
  created date NOT NULL
);
