# Second-in-Command Skill Browser

A static React app (CDN + Babel, no build step) that renders all aptitude skill rows from `api.json` with hover tooltips — matching Starsector's dark sci-fi UI aesthetic.

## Files

| File | Purpose |
|------|---------|
| `index.html` | Entry point — loads CDN React/Babel, then `data.js` + `app.js` |
| `app.js` | All React components (JSX, transpiled by Babel in-browser) |
| `styles.css` | Dark sci-fi theme styles |
| `api.json` | Exported skill data from the mod |
| `assets/` | Skill icon PNGs referenced by `api.json` |
| `generate_data.ps1` | Generates `data.js` for `file://` access (see below) |
| `start_server.bat` | Starts a local HTTP server (recommended) |

---

## How to view

### Option A — HTTP server (recommended, works in all browsers)

1. Double-click **`start_server.bat`** (requires Python 3 in `PATH`).
2. Open **http://localhost:8080** in any browser.

### Option B — Direct file open (Firefox only)

Firefox allows `fetch()` on `file://` URIs, so you can simply open `index.html` directly.

### Option C — Generate `data.js` (Chrome / any browser via file://)

Chrome blocks network requests on `file://`. Run the helper script once:

```powershell
cd "path\to\frontend"
.\generate_data.ps1
```

This creates `data.js` which pre-loads the JSON as `window.SIC_DATA`.  
Re-run after every `api.json` update.  Then open `index.html` directly in Chrome.

---

## Features

- **All aptitude skill rows** rendered with correct ordering (SIC native first, third-party mods below a divider)
- **Skill icons** — origin skill at 72 × 72 px, others at 58 × 58 px, border coloured by aptitude RGBA
- **Hover tooltips** — follow the cursor, clamp to viewport; render all `tooltipElements` (title, label with inline highlights, spacer, imageWithText)
- **Pick-one sections** — `canChooseMultiple: false` sections show a coloured underline bracket
- **Section separators** — chevron arrows with required-previous-skills count
- **Filter panel** — toggle visibility by mod and by aptitude category
- **`hide_in_codex` / `dont_include_in_wiki` tags** respected (those aptitudes are hidden)

