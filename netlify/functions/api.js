// CampusLoop API: one Netlify Function handling all actions.
// Data lives in Netlify Blobs (shared across users and devices).
// Passwords are hashed with scrypt. Admin is set from environment variables.
import crypto from 'crypto';
import { createClient } from '@supabase/supabase-js';

const ADMIN_EMAIL = (process.env.ADMIN_EMAIL || '').trim().toLowerCase();
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || '';
const ADMIN_REG = process.env.ADMIN_REG || 'ADMIN';
const CATS = ['Books', 'Study Materials', 'Electronics', 'Clothing', 'Stationery', 'Accessories', 'Hostel Essentials', 'Other'];
const CONDS = ['New', 'Like New', 'Good', 'Fair'];
const DEPTS = ['CSE', 'ECE', 'EEE', 'IT', 'Mechanical', 'Civil'];
const YEARS = ['1st Year', '2nd Year', '3rd Year', '4th Year'];
const TOPICS = ['Account or login', 'Listing problem', 'Request problem', 'Report inappropriate content', 'Bug or suggestion', 'Other'];
const REPORT_REASONS = ['Misleading or inaccurate listing', 'Prohibited or unsafe item', 'Inappropriate content', 'Suspected scam', 'Other'];
const TSTAT = ['Open', 'In Progress', 'Resolved'];
const SESSION_MS = 7 * 24 * 60 * 60 * 1000;

class HttpError extends Error {
  constructor(status, msg) { super(msg); this.status = status; }
}
const fail = (s, m) => { throw new HttpError(s, m); };
const json = (status, obj) => new Response(JSON.stringify(obj), {
  status,
  headers: { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' }
});
const clean = (v, max) => String(v ?? '').trim().slice(0, max);
let database;
function store() {
  if (!database) {
    const url = process.env.SUPABASE_URL;
    const key = process.env.SUPABASE_SERVICE_ROLE_KEY;
    if (!url || !key) throw new Error('SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY must be configured.');
    database = createClient(url, key, { auth: { persistSession: false, autoRefreshToken: false } });
  }
  return database;
}
const useSupabase = () => process.env.DATA_BACKEND === 'supabase';
const netlifyStore = async () => {
  const { getStore } = await import('@netlify/blobs');
  return getStore('campusloop');
};

function seedDb() {
  const I = (id, title, cat, cond, price, days, desc) => ({
    id, title, cat, cond, type: price ? 'Sell' : 'Donate', price, days, desc,
    owner: ADMIN_REG, phone: '', dept: 'CSE', status: 'Available', img: '', ts: Date.now() - id * 36e5
  });
  return {
    users: {},
    items: [
      I(1, 'Engineering Drawing Kit', 'Stationery', 'Good', 250, 120, 'Mini drafter, set squares and compass. Complete set.'),
      I(2, 'Scientific Calculator', 'Electronics', 'Good', 450, 200, 'Works perfectly, new batteries.'),
      I(3, 'Data Structures Textbook', 'Books', 'Like New', 0, 90, 'Barely used, free to a good home.'),
      I(4, 'Lab Coat (M)', 'Clothing', 'Good', 0, 150, 'Washed and ready for your next lab.'),
      I(5, 'Handwritten DSA Notes', 'Study Materials', 'Good', 0, 60, 'Neat notes covering the whole semester.'),
      I(6, 'Black Backpack', 'Accessories', 'Fair', 300, 400, 'Roomy, 3 compartments, zip needs a little care.'),
      I(7, 'Wired Headphones', 'Electronics', 'Good', 180, 220, 'Clear sound, 3.5mm jack.'),
      I(8, 'Bucket & Mug Set', 'Hostel Essentials', 'Good', 90, 100, 'Handy starter set for the hostel.'),
      I(9, 'Gel Pen Pack (10)', 'Stationery', 'New', 40, 5, 'Unopened pack, assorted colours.'),
      I(10, 'Study Desk Lamp', 'Hostel Essentials', 'Good', 150, 180, 'Warm LED lamp with adjustable neck.')
    ],
    reqs: [], notes: [], tickets: [], reports: [], wish: {}, viewed: {}, sessions: {},
    nid: 1, rid: 1, iid: 20, tid: 1, pid: 1
  };
}

function hash(pw) {
  return new Promise((res, rej) => {
    const salt = crypto.randomBytes(16).toString('hex');
    crypto.scrypt(pw, salt, 64, (e, k) => e ? rej(e) : res(salt + ':' + k.toString('hex')));
  });
}
function verify(pw, stored) {
  return new Promise(res => {
    const [salt, h] = String(stored).split(':');
    crypto.scrypt(pw, salt, 64, (e, k) => res(!e && crypto.timingSafeEqual(Buffer.from(h, 'hex'), k)));
  });
}

async function load() {
  let db;
  if (useSupabase()) {
    const { data, error } = await store().from('campusloop_state').select('state').eq('id', 'state').maybeSingle();
    if (error) throw error;
    db = data?.state || seedDb();
  } else {
    db = (await (await netlifyStore()).get('state', { type: 'json' })) || seedDb();
  }
  db.reports ||= [];
  db.pid ||= db.reports.reduce((max, report) => Math.max(max, Number(report.id) || 0), 0) + 1;
  // Create the admin account from environment variables if it doesn't exist yet
  // Admin account is controlled only by environment variables (ADMIN_REG, ADMIN_EMAIL, ADMIN_PASSWORD).
  // It is created or promoted automatically, and the password updates when the variable changes.
  if (ADMIN_EMAIL && ADMIN_PASSWORD) {
    const sig = crypto.createHash('sha256').update(ADMIN_PASSWORD + '|' + ADMIN_EMAIL).digest('hex');
    let a = db.users[ADMIN_REG];
    if (!a) a = db.users[ADMIN_REG] = { reg: ADMIN_REG, name: 'CampusLoop Admin', dept: 'CSE', year: '', phone: '', email: ADMIN_EMAIL };
    if (a.role !== 'admin' || a.sig !== sig || !a.hash) {
      a.role = 'admin'; a.email = ADMIN_EMAIL; a.sig = sig; a.hash = await hash(ADMIN_PASSWORD);
    }
    for (const u of Object.values(db.users)) if (u.reg !== ADMIN_REG && u.role === 'admin') u.role = 'student';
  }
  const now = Date.now();
  for (const t of Object.keys(db.sessions)) if (db.sessions[t].exp < now) delete db.sessions[t];
  return db;
}
async function save(db) {
  if (useSupabase()) {
    const { error } = await store().from('campusloop_state').upsert({
      id: 'state',
      state: db,
      updated_at: new Date().toISOString()
    });
    if (error) throw error;
  } else {
    await (await netlifyStore()).setJSON('state', db);
  }
}

function me(db, token) {
  const s = token && db.sessions[token];
  if (!s || s.exp < Date.now()) fail(401, 'Please log in again.');
  const u = db.users[s.reg];
  if (!u) fail(401, 'Please log in again.');
  return u;
}
const isAdmin = u => u.role === 'admin';
const pub = u => ({ reg: u.reg, name: u.name, dept: u.dept || '', year: u.year || '', phone: u.phone || '', email: u.email || '', role: u.role || 'student' });
const addN = (db, to, k, msg) => db.notes.unshift({ id: db.nid++, to, k, msg, read: false, ts: Date.now() });
const findItem = (db, id) => { const i = db.items.find(x => x.id == id); if (!i) fail(404, 'Listing not found.'); return i; };

const A = {
  async signup(db, b) {
    const reg = clean(b.reg, 10), name = clean(b.name, 80), pw = String(b.password ?? '');
    if (!/^202550\d{4}$/.test(reg) || reg === ADMIN_REG) fail(400, 'Registration number must be 202550 followed by exactly 4 digits.');
    if (!name) fail(400, 'Name is required.');
    if (pw.length < 6 || pw.length > 200) fail(400, 'Password must be 6 to 200 characters.');
    if (db.users[reg]) fail(400, 'That registration number is already registered.');
    db.users[reg] = { reg, name, dept: 'CSE', year: '1st Year', phone: '', email: '', role: 'student', hash: await hash(pw) };
    return { ok: true };
  },

  async login(db, b) {
    const reg = clean(b.reg, 10), u = db.users[reg];
    const ok = u && await verify(String(b.password ?? ''), u.hash);
    if (!ok) fail(401, 'Login unsuccessful. Check your registration number and password.');
    const token = crypto.randomBytes(32).toString('hex');
    db.sessions[token] = { reg, exp: Date.now() + SESSION_MS };
    return { token, me: pub(u) };
  },

  async logout(db, b) { delete db.sessions[b.token]; return { ok: true }; },

  async state(db, b) {
    const u = me(db, b.token), adm = isAdmin(u);
    return {
      me: pub(u),
      users: Object.fromEntries(Object.values(db.users).map(x => [x.reg, pub(x)])),
      items: db.items,
      reqs: db.reqs.filter(r => r.from === u.reg || r.to === u.reg),
      notes: db.notes.filter(n => n.to === u.reg),
      tickets: adm ? db.tickets : db.tickets.filter(t => t.from === u.reg),
      reports: adm ? db.reports : [],
      wish: db.wish[u.reg] || [],
      viewed: db.viewed[u.reg] || []
    };
  },

  async updateProfile(db, b) {
    const u = me(db, b.token), name = clean(b.name, 80);
    if (!name) fail(400, 'Name is required.');
    if (!YEARS.includes(b.year) || !DEPTS.includes(b.dept)) fail(400, 'Invalid year or department.');
    Object.assign(u, { name, year: b.year, dept: b.dept, phone: clean(b.phone, 15), email: clean(b.email, 120) });
    return { me: pub(u) };
  },

  async publish(db, b) {
    const u = me(db, b.token);
    const title = clean(b.title, 80), desc = clean(b.desc, 1000), phone = clean(b.phone, 15).replace(/\D/g, '');
    const type = b.type === 'Donate' ? 'Donate' : 'Sell';
    const price = type === 'Donate' ? 0 : Math.floor(Number(b.price));
    const days = b.days === '' || b.days == null ? NaN : Math.floor(Number(b.days));
    const img = typeof b.img === 'string' ? b.img : '';
    if (!title || !desc) fail(400, 'Please fill in title and description.');
    if (!CATS.includes(b.cat) || !CONDS.includes(b.cond) || !DEPTS.includes(b.dept)) fail(400, 'Invalid category, condition or department.');
    if (phone.length < 10) fail(400, 'Enter a valid 10-digit contact number.');
    if (type === 'Sell' && !(price > 0)) fail(400, 'Enter a price above ₹0, or choose Donate.');
    if (!(days >= 0)) fail(400, 'Enter how many days the item was used.');
    if (img && (!img.startsWith('data:image/') || img.length > 400000)) fail(400, 'Image is too large. Please choose a smaller photo.');
    const f = { title, desc, cat: b.cat, cond: b.cond, type, price, days, phone, dept: b.dept, img };
    if (b.id) {
      const i = findItem(db, b.id);
      if (i.owner !== u.reg) fail(403, 'You can only edit your own listing.');
      Object.assign(i, f);
      return { ok: true };
    }
    db.items.unshift({ id: db.iid++, owner: u.reg, status: 'Available', ts: Date.now(), ...f });
    return { ok: true };
  },

  async deleteItem(db, b) {
    const u = me(db, b.token), i = findItem(db, b.id);
    if (i.owner !== u.reg) fail(403, 'You can only delete your own listing.');
    db.reqs.filter(r => r.item === i.id && r.status === 'Pending').forEach(r => addN(db, r.from, 'no', 'A listing you requested was removed by its owner.'));
    db.items = db.items.filter(x => x.id !== i.id);
    db.reqs = db.reqs.filter(r => r.item !== i.id);
    return { ok: true };
  },

  async setStatus(db, b) {
    const u = me(db, b.token), i = findItem(db, b.id);
    if (i.owner !== u.reg) fail(403, 'Not allowed.');
    if (!['Available', 'Reserved'].includes(b.status)) fail(400, 'Invalid status.');
    i.status = b.status;
    return { ok: true };
  },

  async reportListing(db, b) {
    const u = me(db, b.token), i = findItem(db, b.id);
    if (i.owner === u.reg) fail(400, "You can't report your own listing.");
    if (!REPORT_REASONS.includes(b.reason)) fail(400, 'Please choose a report reason.');
    if (db.reports.some(r => r.item === i.id && r.from === u.reg && r.status === 'Open')) {
      fail(400, 'You already have an open report for this listing.');
    }
    const report = {
      id: db.pid++, item: i.id, title: i.title, owner: i.owner, from: u.reg,
      reason: b.reason, details: clean(b.details, 500), status: 'Open', ts: Date.now()
    };
    db.reports.unshift(report);
    addN(db, ADMIN_REG, 'req', `${u.name} reported the listing "${i.title}" for admin review.`);
    return { ok: true };
  },

  async setReportStatus(db, b) {
    const u = me(db, b.token);
    if (!isAdmin(u)) fail(403, 'Admin access required.');
    const report = db.reports.find(r => r.id === Number(b.id));
    if (!report) fail(404, 'Report not found.');
    if (!['Reviewed', 'Dismissed'].includes(b.status)) fail(400, 'Invalid report status.');
    if (report.status !== 'Open') fail(400, 'This report has already been reviewed.');
    report.status = b.status;
    addN(db, report.from, 'ok', `Your report about "${report.title}" was ${b.status.toLowerCase()}.`);
    return { ok: true };
  },

  async request(db, b) {
    const u = me(db, b.token), i = findItem(db, b.id);
    if (i.owner === u.reg) fail(400, "You can't request your own item.");
    if (i.status !== 'Available') fail(400, 'This item is no longer available.');
    if (db.reqs.some(r => r.item === i.id && r.from === u.reg && r.status === 'Pending')) fail(400, 'You already have a pending request for this item.');
    db.reqs.unshift({ id: db.rid++, item: i.id, from: u.reg, to: i.owner, status: 'Pending', date: new Date().toLocaleDateString('en-IN') });
    addN(db, i.owner, 'req', `${u.name} (${u.reg}) requested your item "${i.title}".`);
    return { ok: true };
  },

  async respond(db, b) {
    const u = me(db, b.token), r = db.reqs.find(x => x.id == b.id);
    if (!r || r.to !== u.reg) fail(403, 'Not allowed.');
    const i = db.items.find(x => x.id === r.item);
    if (!i) fail(404, 'This item was removed.');
    if (!['Accepted', 'Declined', 'Completed'].includes(b.status)) fail(400, 'Invalid status.');
    r.status = b.status;
    if (b.status === 'Accepted') {
      i.status = 'Reserved';
      db.reqs.filter(x => x.item === i.id && x.status === 'Pending').forEach(x => {
        x.status = 'Declined';
        addN(db, x.from, 'no', `Sorry, "${i.title}" was reserved for another student.`);
      });
      addN(db, r.from, 'ok', `Great news! The owner accepted your request for ${i.title}.`);
    }
    if (b.status === 'Declined') addN(db, r.from, 'no', `Sorry, the owner declined your request for ${i.title}.`);
    if (b.status === 'Completed') {
      i.status = 'Completed';
      addN(db, r.from, 'ok', `Handoff of ${i.title} marked as complete. Enjoy! ✿`);
    }
    return { ok: true };
  },

  async wish(db, b) {
    const u = me(db, b.token), id = Number(b.id);
    const w = db.wish[u.reg] = db.wish[u.reg] || [];
    const k = w.indexOf(id);
    k < 0 ? w.push(id) : w.splice(k, 1);
    return { saved: k < 0 };
  },

  async viewed(db, b) {
    const u = me(db, b.token), id = Number(b.id);
    db.viewed[u.reg] = [id, ...(db.viewed[u.reg] || []).filter(v => v !== id)].slice(0, 6);
    return { ok: true };
  },

  async readNotes(db, b) {
    const u = me(db, b.token);
    db.notes.forEach(n => { if (n.to === u.reg && (!b.id || n.id === Number(b.id))) n.read = true; });
    return { ok: true };
  },

  async ticket(db, b) {
    const u = me(db, b.token);
    if (!TOPICS.includes(b.topic)) fail(400, 'Please choose a topic.');
    const message = clean(b.message, 1500);
    if (!message) fail(400, 'Please enter a message.');
    db.tickets.unshift({ id: db.tid++, from: u.reg, topic: b.topic, message, email: clean(b.email, 120), status: 'Open', reply: '', ts: Date.now() });
    return { ok: true };
  },

  async ticketReply(db, b) {
    const u = me(db, b.token);
    if (!isAdmin(u)) fail(403, 'Admin access required.');
    const t = db.tickets.find(x => x.id == b.id);
    if (!t) fail(404, 'Ticket not found.');
    const reply = clean(b.reply, 2000);
    if (!reply) fail(400, 'Write a reply first.');
    t.reply = reply;
    if (t.status === 'Open') t.status = 'In Progress';
    addN(db, t.from, 'ok', `Support replied to your ticket "${t.topic}".`);
    return { ok: true };
  },

  async ticketStatus(db, b) {
    const u = me(db, b.token);
    if (!isAdmin(u)) fail(403, 'Admin access required.');
    const t = db.tickets.find(x => x.id == b.id);
    if (!t) fail(404, 'Ticket not found.');
    if (!TSTAT.includes(b.status)) fail(400, 'Invalid status.');
    t.status = b.status;
    return { ok: true };
  }
};

export default async (req) => {
  if (req.method !== 'POST') return json(405, { error: 'Method not allowed.' });
  let b;
  try { b = JSON.parse((await req.text()) || '{}'); } catch { return json(400, { error: 'Invalid request.' }); }
  if (!Object.prototype.hasOwnProperty.call(A, b.action)) return json(400, { error: 'Unknown action.' });
  try {
    const db = await load();
    const out = await A[b.action](db, b);
    await save(db);
    return json(200, out);
  } catch (e) {
    if (e instanceof HttpError) return json(e.status, { error: e.message });
    console.error(e);
    return json(500, { error: 'Server error. Please try again.' });
  }
};
