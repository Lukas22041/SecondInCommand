# Second-in-Command Skill Browser

A static React app (CDN + Babel, no build step) that renders all aptitude skill rows from `api/api.json` with hover tooltips — matching Starsector's dark sci-fi UI aesthetic.

## File Layout

```
frontend/
├── Caddyfile           — Caddy config for Railway (static hosting)
├── index.html          — Entry point
├── styles.css          — Starsector Theme
├── start.py            — Local dev server (Python 3)
├── start.bat           — Windows shortcut for start.py
├── api/
│   ├── api.json        — Exported skill data from the mod
│   └── assets/         — Skill icon PNGs referenced by api.json
├── appAssets/          — Fonts, background, banner, gallery images
└── js/
    ├── utils.js        — React hook globals, colour helpers, path resolver
    ├── tooltip.js      — Tooltip component tree
    ├── ui.js           — AnchorCopy, SectionHeading
    ├── skill.js        — SkillIcon, SectionSeparator, SkillSection
    ├── aptitude.js     — AptitudeRow
    ├── gallery.js      — GalleryCarousel
    ├── sections.js     — AboutSection, LinksSection, IncompatibilitiesSection
    ├── nav.js          — SideNav
    └── app.js          — App root + ReactDOM mount
```

---

## How to view locally

Requires Python 3 in `PATH`.

```
python start.py
```

Then open **http://localhost:8080** in any browser.  
An optional `--port` flag is available: `python start.py --port 9000`.

> **Note:** The app must be served over HTTP. Opening `index.html` directly
> via `file://` will not work because browsers block `fetch()` on that scheme.

---

## Deploying to Railway

1. Push this folder as the root of a GitHub repo.
2. Create a new Railway project → **Deploy from GitHub repo**.
3. Railway detects the `Caddyfile` and uses the Caddy static-site template automatically.
4. No build command or install step is needed.

---

## Features

- **All aptitude skill rows** rendered with correct ordering (SIC native first, third-party mods below a divider)
- **Skill icons** — origin skill at 72 × 72 px, others at 58 × 58 px, border coloured by aptitude RGBA
- **Hover tooltips** — follow the cursor, clamp to viewport; render all `tooltipElements` (title, label with inline highlights, spacer, imageWithText)
- **Pick-one sections** — `canChooseMultiple: false` sections show a coloured underline bracket
- **Section separators** — chevron arrows between skill sections
- **Filter panel** — toggle visibility by mod and by aptitude category
- **`hide_in_codex` / `dont_include_in_wiki` tags** respected (those aptitudes are hidden)
