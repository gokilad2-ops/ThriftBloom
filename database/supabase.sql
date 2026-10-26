create table if not exists public.campusloop_state (
  id text primary key check (id = 'state'),
  state jsonb not null,
  updated_at timestamptz not null default now()
);

alter table public.campusloop_state enable row level security;
revoke all on public.campusloop_state from anon, authenticated;
grant all on public.campusloop_state to service_role;
