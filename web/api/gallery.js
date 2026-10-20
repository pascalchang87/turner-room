// Stores the gallery layout so the projector and your phone share one gallery.
// Needs a Redis database from Vercel's Storage tab (Upstash) and an EDIT_KEY
// environment variable. Reading is public; saving requires the edit key.
const crypto = require("crypto");

const KEY = "turner-room:gallery";
const MAX_BYTES = 400 * 1024;

function store() {
  const url = process.env.KV_REST_API_URL || process.env.UPSTASH_REDIS_REST_URL;
  const token = process.env.KV_REST_API_TOKEN || process.env.UPSTASH_REDIS_REST_TOKEN;
  return url && token ? { url: url.replace(/\/$/, ""), token } : null;
}

function sameKey(given, expected) {
  const a = Buffer.from(String(given || ""));
  const b = Buffer.from(String(expected || ""));
  return a.length === b.length && crypto.timingSafeEqual(a, b);
}

module.exports = async (req, res) => {
  res.setHeader("Cache-Control", "no-store");
  const s = store();
  if (!s) {
    return res.status(501).json({ error: "No database connected. In Vercel, open Storage, add Upstash Redis to this project, then redeploy." });
  }

  if (req.method === "GET") {
    const r = await fetch(`${s.url}/get/${encodeURIComponent(KEY)}`, { headers: { Authorization: `Bearer ${s.token}` } });
    if (!r.ok) return res.status(502).json({ error: "The database didn't answer." });
    const j = await r.json();
    let gallery = null;
    try { gallery = j.result ? JSON.parse(j.result) : null; } catch { gallery = null; }
    return res.status(200).json({ gallery, editable: Boolean(process.env.EDIT_KEY) });
  }

  if (req.method === "PUT") {
    if (!process.env.EDIT_KEY) return res.status(403).json({ error: "Saving is off. Add an EDIT_KEY environment variable in Vercel." });
    if (!sameKey(req.headers["x-edit-key"], process.env.EDIT_KEY)) return res.status(403).json({ error: "Wrong edit key." });
    const body = typeof req.body === "string" ? req.body : JSON.stringify(req.body || null);
    if (Buffer.byteLength(body) > MAX_BYTES) return res.status(413).json({ error: "Gallery is too large." });
    let parsed;
    try { parsed = JSON.parse(body); } catch { return res.status(400).json({ error: "Body must be JSON." }); }
    if (!parsed || !Array.isArray(parsed.walls)) return res.status(400).json({ error: "A gallery needs a walls list." });
    const r = await fetch(`${s.url}/set/${encodeURIComponent(KEY)}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${s.token}` },
      body
    });
    if (!r.ok) return res.status(502).json({ error: "The database refused the save." });
    return res.status(200).json({ ok: true });
  }

  res.setHeader("Allow", "GET, PUT");
  return res.status(405).json({ error: "Use GET or PUT." });
};
