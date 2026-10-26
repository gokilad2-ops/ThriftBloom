# Database

CampusLoop supports two persistence backends:

- On Netlify, it uses **Netlify Blobs**, in the `campusloop` store under the `state` key.
- On Vercel, it uses the `campusloop_state` table in Supabase Postgres. Create the table by running [`supabase.sql`](./supabase.sql).

Both backends store the application state as one JSON document. Its shape is in [`schema.json`](./schema.json). The starting demo listings are defined in `seedDb()` in `netlify/functions/api.js`.

To view the old data: Netlify dashboard -> your site -> Data & storage -> Blobs -> `campusloop`. The `npm run migrate:netlify` script copies this state into Supabase without changing the Netlify copy. It refuses to overwrite a Supabase state row.

To view the Vercel database, use the Supabase dashboard's Table Editor and select `campusloop_state`. Do not expose the service-role key to the browser.

Limitation: application data is stored as one shared JSON record, which is suitable for a small campus launch but can lose concurrent updates under heavy use. For higher traffic, normalize the data into relational tables and use transaction-safe updates.
