# CampusLoop

CampusLoop is a student marketplace for listing, requesting, and donating used items. The frontend is plain HTML, CSS, and JavaScript. The API supports accounts, listings, requests, notifications, support tickets, and private listing reports.

## Hosting

The app can run on Netlify with Netlify Blobs, or on Vercel with Supabase Postgres. The Vercel configuration serves the site from `frontend/` and exposes the shared API at `/api`.

### Move the existing site to Vercel and Supabase

Keep the current Netlify site available until the new host and migrated data have been checked.

1. Create a Supabase project and run [`database/supabase.sql`](./database/supabase.sql) in its SQL Editor.
2. Import this GitHub repository into Vercel. Use the repository root as the project root, no build command, and `frontend` as the output directory (already set in `vercel.json`).
3. Add these Vercel environment variables for **Production** and **Preview**:
   - `DATA_BACKEND` = `supabase`
   - `SUPABASE_URL` and `SUPABASE_SERVICE_ROLE_KEY` from the Supabase project settings. The service-role key is secret and must only be configured server-side.
   - `ADMIN_REG` and `ADMIN_EMAIL` for the admin account, plus a new strong `ADMIN_PASSWORD` of your choice. The migrated admin account will be updated to this password the first time the API runs. Do not commit or share it.
4. Before opening the deployed app for the first time, migrate the existing Netlify Blobs state. This prevents the app from creating a fresh database row before the import.
   - Create a Netlify personal access token and get the Netlify site ID from the existing project settings.
   - Copy `.env.example` to `.env.local`, then fill in the Netlify site ID/token and Supabase URL/service-role key. Keep `.env.local` private; it is ignored by Git.
   - With Node.js 20.6 or newer, run `npm install` and then `npm run migrate:netlify`. The script refuses to overwrite an existing Supabase state row.
5. Open the Vercel URL and test login, listings, and requests. Users may need to log in again on the new domain, but their account passwords and marketplace data are imported.
6. Keep the Netlify site as a rollback option until the Vercel site is confirmed working.

Do not put `SUPABASE_SERVICE_ROLE_KEY`, `NETLIFY_AUTH_TOKEN`, or `ADMIN_PASSWORD` in frontend code or commit them to Git.

## Admin
Log in with the `ADMIN_REG` number and `ADMIN_PASSWORD`. The admin account is created or updated automatically from these settings. Admin rights never come from anything a user can edit.

Admins can review private listing reports in the Help Center. Students can report another user's listing from its details; reports are visible only to admins, and repeat open reports for the same listing are prevented.

Explore Finds supports text search, category, listing type, price, availability, and sort filters. Owners can mark listings available or reserved; accepting a request reserves the item, and marking the handoff complete closes it.

## Updating the site later
Edit files, commit, and push to GitHub. Vercel deploys changes from the connected branch automatically.

## Known limits
No password reset or email verification. Listing photos are stored in the database JSON record. See [`database/README.md`](./database/README.md).
