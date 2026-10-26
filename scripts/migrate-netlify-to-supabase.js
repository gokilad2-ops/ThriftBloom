import { createClient } from '@supabase/supabase-js';
import { getStore } from '@netlify/blobs';

const required = [
  'NETLIFY_SITE_ID',
  'NETLIFY_AUTH_TOKEN',
  'SUPABASE_URL',
  'SUPABASE_SERVICE_ROLE_KEY',
  'CONFIRM_MIGRATION'
];
const missing = required.filter(name => !process.env[name]);
if (missing.length) {
  throw new Error(`Missing required migration settings: ${missing.join(', ')}`);
}
if (process.env.CONFIRM_MIGRATION !== 'CampusLoop') {
  throw new Error('Set CONFIRM_MIGRATION=CampusLoop to confirm the data import.');
}

const netlifyStore = getStore({
  name: 'campusloop',
  siteID: process.env.NETLIFY_SITE_ID,
  token: process.env.NETLIFY_AUTH_TOKEN
});
const database = createClient(process.env.SUPABASE_URL, process.env.SUPABASE_SERVICE_ROLE_KEY, {
  auth: { persistSession: false, autoRefreshToken: false }
});

const { data: existing, error: lookupError } = await database
  .from('campusloop_state')
  .select('id')
  .eq('id', 'state')
  .maybeSingle();
if (lookupError) throw lookupError;
if (existing) throw new Error('Supabase already has CampusLoop data; refusing to overwrite it.');

const state = await netlifyStore.get('state', { type: 'json' });
if (!state || !state.users || !Array.isArray(state.items)) {
  throw new Error('The Netlify Blobs state is missing or has an unexpected format.');
}

state.reports ||= [];
state.pid ||= state.reports.reduce((max, report) => Math.max(max, Number(report.id) || 0), 0) + 1;
state.reqs ||= [];
state.notes ||= [];
state.tickets ||= [];
state.wish ||= {};
state.viewed ||= {};
state.sessions ||= {};

const { error: importError } = await database.from('campusloop_state').insert({
  id: 'state',
  state,
  updated_at: new Date().toISOString()
});
if (importError) throw importError;

console.log(`Imported CampusLoop data: ${Object.keys(state.users).length} accounts, ${state.items.length} listings.`);
