/* ============================================================
   Second-in-Command Skill Browser — app.js
   React 18 + Babel Standalone (CDN, no build step)
   ============================================================ */

const { useState, useEffect, useRef, useCallback, useLayoutEffect, useMemo, Fragment } = React;

// ---- Colour helpers --------------------------------------------------------

function rgbaToCSS(c, alphaOverride) {
  if (!c) return 'rgba(200,200,200,1)';
  const a = alphaOverride !== undefined ? alphaOverride : c.a / 255;
  return `rgba(${c.r},${c.g},${c.b},${a.toFixed(3)})`;
}

// ---- Text with inline highlight ranges ------------------------------------

function HighlightedText({ text, highlightRanges, baseColor }) {
  if (!text) return null;
  if (!highlightRanges || highlightRanges.length === 0) {
    return <span style={{ color: baseColor }}>{text}</span>;
  }

  // Sort ranges by startIndex ascending
  const ranges = [...highlightRanges].sort((a, b) => a.startIndex - b.startIndex);
  const parts = [];
  let cursor = 0;

  ranges.forEach((range, i) => {
    if (range.startIndex > cursor) {
      parts.push(
        <span key={`b${cursor}`} style={{ color: baseColor }}>
          {text.slice(cursor, range.startIndex)}
        </span>
      );
    }
    parts.push(
      <span key={`h${i}`} style={{ color: rgbaToCSS(range.color) }}>
        {text.slice(range.startIndex, range.endIndex)}
      </span>
    );
    cursor = range.endIndex;
  });

  if (cursor < text.length) {
    parts.push(
      <span key={`e${cursor}`} style={{ color: baseColor }}>
        {text.slice(cursor)}
      </span>
    );
  }

  return <>{parts}</>;
}

// ---- Tooltip element renderers --------------------------------------------

function TooltipLabel({ el }) {
  const baseColor = rgbaToCSS(el.color);
  // Handle embedded newlines as separate lines
  const lines = (el.text || '').split('\n');
  if (lines.length === 1) {
    return (
      <div className="tooltip-label" style={{ paddingTop: el.padding || 0 }}>
        <HighlightedText text={el.text} highlightRanges={el.highlightRanges} baseColor={baseColor} />
      </div>
    );
  }
  // Multi-line: apply highlights only to first line for simplicity (they span single lines in practice)
  return (
    <div className="tooltip-label" style={{ paddingTop: el.padding || 0 }}>
      {lines.map((line, i) => (
        <div key={i}>
          {i === 0
            ? <HighlightedText text={line} highlightRanges={el.highlightRanges} baseColor={baseColor} />
            : <span style={{ color: baseColor }}>{line}</span>
          }
        </div>
      ))}
    </div>
  );
}

function TooltipImageWithText({ el }) {
  const imageSize = el.imageHeight || 48;
  // Sort children by y descending (game uses bottom-up coords)
  const children = el.children
    ? [...el.children].sort((a, b) => (b.position?.y ?? 0) - (a.position?.y ?? 0))
    : [];

  return (
    <div className="tooltip-image-with-text">
      <img
        className="tooltip-ability-image"
        src={el.exportPath}
        alt=""
        style={{ width: imageSize, height: imageSize }}
      />
      <div className="tooltip-image-text">
        {children.map((child, i) => {
          if (child.type === 'label') return <TooltipLabel key={i} el={child} />;
          if (child.type === 'spacer') {
            return <div key={i} className="tooltip-spacer" style={{ height: Math.max(2, child.height || 2) }} />;
          }
          return null;
        })}
      </div>
    </div>
  );
}

// ---- Tooltip renderer (all elements) --------------------------------------

function TooltipRenderer({ tooltipElements }) {
  if (!tooltipElements || tooltipElements.length === 0) return null;

  // Sort by position.y descending (highest y = rendered first / top of tooltip)
  const sorted = [...tooltipElements].sort(
    (a, b) => (b.position?.y ?? 0) - (a.position?.y ?? 0)
  );

  return (
    <div className="tooltip-content">
      {sorted.map((el, i) => {
        switch (el.type) {
          case 'title':
            return (
              <div key={i} className="tooltip-title" style={{ color: rgbaToCSS(el.color) }}>
                {el.text}
              </div>
            );
          case 'label':
            return <TooltipLabel key={i} el={el} />;
          case 'spacer':
            return (
              <div
                key={i}
                className="tooltip-spacer"
                style={{ height: Math.max(2, el.height || 2) }}
              />
            );
          case 'imageWithText':
            return <TooltipImageWithText key={i} el={el} />;
          default:
            return null;
        }
      })}
    </div>
  );
}

// ---- Tooltip Overlay (portal, fixed position, cursor-clamped) -------------

function TooltipOverlay({ skill, aptitudeColor, mouseX, mouseY }) {
  const ref = useRef(null);
  const [pos, setPos] = useState({ left: -9999, top: -9999, visible: false });

  useLayoutEffect(() => {
    if (!ref.current) return;
    const OFFSET = 16;
    const rect = ref.current.getBoundingClientRect();
    const vw = window.innerWidth;
    const vh = window.innerHeight;

    let left = mouseX + OFFSET;
    let top  = mouseY + OFFSET;

    if (left + rect.width  > vw - 10) left = Math.max(10, mouseX - rect.width  - OFFSET);
    if (top  + rect.height > vh - 10) top  = Math.max(10, vh      - rect.height - 10);
    left = Math.max(10, left);

    setPos({ left, top, visible: true });
  }, [mouseX, mouseY, skill]);

  const borderColor = rgbaToCSS(aptitudeColor);
  const glowColor   = rgbaToCSS(aptitudeColor, 0.25);
  const glowColor2  = rgbaToCSS(aptitudeColor, 0.10);

  return ReactDOM.createPortal(
    <div
      ref={ref}
      className="tooltip-overlay"
      style={{
        left: pos.left,
        top:  pos.top,
        visibility: pos.visible ? 'visible' : 'hidden',
        borderColor,
        boxShadow: `0 0 16px ${glowColor}, 0 0 4px ${glowColor2}, inset 0 0 20px rgba(0,0,0,0.6)`,
      }}
    >
      <TooltipRenderer tooltipElements={skill.tooltipElements} />
    </div>,
    document.body
  );
}

// ---- Skill Icon -----------------------------------------------------------

function SkillIcon({ skill, aptitude }) {
  const isOrigin = skill.isOriginSkill;
  const size     = 90;

  const [hovered, setHovered] = useState(false);
  const [mousePos, setMousePos] = useState({ x: 0, y: 0 });

  const borderColor = rgbaToCSS(aptitude.color);
  const glowColor   = rgbaToCSS(aptitude.color, hovered ? 0.6 : 0.2);
  const glowStrong  = rgbaToCSS(aptitude.color, 0.35);

  const handleMove = useCallback((e) => {
    setMousePos({ x: e.clientX, y: e.clientY });
  }, []);

  return (
    <div
      className={`skill-icon${isOrigin ? ' origin' : ''}`}
      style={{
        width:  size,
        height: size,
        borderColor,
        boxShadow: hovered
          ? `0 0 10px ${glowStrong}, 0 0 3px ${borderColor}, inset 0 0 6px rgba(0,0,0,0.5)`
          : `0 0 4px ${glowColor}`,
      }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      onMouseMove={handleMove}
    >
      <img src={skill.iconExportPath} alt={skill.name} />
      {hovered && (
        <TooltipOverlay
          skill={skill}
          aptitudeColor={aptitude.color}
          mouseX={mousePos.x}
          mouseY={mousePos.y}
        />
      )}
    </div>
  );
}

// ---- Section separator (chevron + required count) -------------------------

function SectionSeparator({ requiredPreviousSkills, aptitude }) {
  const { r, g, b } = aptitude.color;
  const strokeColor = `rgba(${r},${g},${b},0.6)`;
  const fillColor   = `rgba(${r},${g},${b},0.08)`;
  return (
    <div className="section-separator">
      <svg className="separator-arrow-svg" viewBox="0 0 14 90" width="14" height="90">
        <polygon
          points="0,8 8,0 14,45 8,90 0,82 5,45"
          fill={fillColor}
          stroke={strokeColor}
          strokeWidth="1.5"
          strokeLinejoin="round"
        />
      </svg>
      {requiredPreviousSkills > 0 && (
        <div className="separator-req" style={{ color: strokeColor }}>{requiredPreviousSkills}</div>
      )}
    </div>
  );
}

// ---- Skill Section (one section's flat icon row) -------------------------

function SkillSection({ section, aptitude }) {
  const { skills, canChooseMultiple } = section;
  const { r, g, b } = aptitude.color;
  const sepColor      = `rgba(${r},${g},${b},0.5)`;
  const ulColor       = `rgba(${r},${g},${b},0.75)`;
  const separatorGradient = `linear-gradient(to bottom, transparent 0%, ${sepColor} 15%, ${sepColor} 85%, transparent 100%)`;
  const underlineGradient = `linear-gradient(to right,  transparent 0%, ${ulColor}  15%, ${ulColor}  85%, transparent 100%)`;

  return (
    <div className={`skill-section${canChooseMultiple ? '' : ' pick-one'}`}>
      <div className="skill-section-icons">
        {skills.map((skill, i) => (
          <Fragment key={skill.id}>
            {i > 0 && (
              <div className="skill-separator" style={{ background: separatorGradient }} />
            )}
            <SkillIcon skill={skill} aptitude={aptitude} />
          </Fragment>
        ))}
      </div>
      {!canChooseMultiple && (
        <div
          className="pick-one-underline"
          style={{ background: underlineGradient }}
        />
      )}
    </div>
  );
}

// ---- Aptitude Row ---------------------------------------------------------

function AptitudeRow({ aptitude }) {
  const aptColor        = rgbaToCSS(aptitude.color);
  const isSIC           = aptitude.modName === 'Second-in-Command';

  // Row background: gradient from dark to aptitude color at low opacity (flat look)
  const rowBg = `linear-gradient(to right, rgba(0,0,0,0.15), rgba(${aptitude.color.r},${aptitude.color.g},${aptitude.color.b},0.08))`;
  const borderLeftColor = aptColor;

  return (
    <div className="aptitude-row" id={`aptitude-${aptitude.id}`}>
      {/* Section title */}
      <div className="aptitude-title">
        <a className="aptitude-title-link" href={`#aptitude-${aptitude.id}`}>
          {aptitude.categories && aptitude.categories.length > 0 && (
            <span className="aptitude-category">
              [{aptitude.categories[0].name}]
            </span>
          )}
          <span>{aptitude.name.toUpperCase()}</span>
        </a>
        {!isSIC && (
          <span className="aptitude-mod-badge">{aptitude.modName}</span>
        )}
      </div>

      {/* Colored accent bar */}
      <div className="aptitude-color-bar" style={{ background: aptColor }} />

      {/* Optional description (future-proofed) */}
      {aptitude.description && (
        <p className="aptitude-description">{aptitude.description}</p>
      )}

      {/* Skills */}
      <div
        className="aptitude-skills-row"
        style={{
          background: rowBg,
          borderLeftColor: borderLeftColor,
        }}
      >
        {aptitude.sections.map((section, i) => (
          <Fragment key={i}>
            {i > 0 && (
              <SectionSeparator requiredPreviousSkills={section.requiredPreviousSkills} aptitude={aptitude} />
            )}
            <SkillSection section={section} aptitude={aptitude} />
          </Fragment>
        ))}
      </div>
    </div>
  );
}

// ---- Filter Panel --------------------------------------------------------

function FilterPanel({
  allMods, allCategories,
  activeMods, activeCategories,
  onModToggle, onCategoryToggle, onReset,
}) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  // Close on outside click
  useEffect(() => {
    if (!open) return;
    const handler = (e) => { if (ref.current && !ref.current.contains(e.target)) setOpen(false); };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [open]);

  const activeCount =
    (activeMods ? allMods.length - activeMods.size : 0) +
    (activeCategories ? allCategories.length - activeCategories.size : 0);

  return (
    <div className="filter-panel" ref={ref}>
      <button className="filter-toggle-btn" onClick={() => setOpen(o => !o)}>
        Filters{activeCount > 0 ? ` (${activeCount} active)` : ''} {open ? '▲' : '▼'}
      </button>

      {open && (
        <div className="filter-dropdown">
          {/* Mod filters */}
          <div className="filter-section">
            <div className="filter-section-title">Mods</div>
            {allMods.map(mod => (
              <label key={mod} className="filter-option">
                <input
                  type="checkbox"
                  checked={activeMods.has(mod)}
                  onChange={() => onModToggle(mod)}
                />
                <span>{mod}</span>
              </label>
            ))}
          </div>

          {/* Category filters */}
          {allCategories.length > 0 && (
            <div className="filter-section">
              <div className="filter-section-title">Categories</div>
              {allCategories.map(cat => (
                <label key={cat.id} className="filter-option">
                  <input
                    type="checkbox"
                    checked={activeCategories.has(cat.id)}
                    onChange={() => onCategoryToggle(cat.id)}
                  />
                  <span style={{ color: rgbaToCSS(cat.color) }}>{cat.name}</span>
                </label>
              ))}
            </div>
          )}

          <button className="filter-reset-btn" onClick={onReset}>
            Reset All Filters
          </button>
        </div>
      )}
    </div>
  );
}

// ---- App (root) ----------------------------------------------------------

function App() {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  // Filter state: null = "all" (unset)
  const [activeMods,       setActiveMods]       = useState(null);
  const [activeCategories, setActiveCategories] = useState(null);

  // ---- Load data ----------------------------------------------------------
  useEffect(() => {
    if (window.SIC_DATA) {
      setData(window.SIC_DATA);
      setLoading(false);
      return;
    }
    fetch('./api.json')
      .then(r => { if (!r.ok) throw new Error(`HTTP ${r.status}`); return r.json(); })
      .then(d  => { setData(d); setLoading(false); })
      .catch(() => {
        setError(
          'Could not load api.json.\n' +
          '• If opening via file://, run generate_data.ps1 once to create data.js.\n' +
          '• Or run start_server.bat and open http://localhost:8080'
        );
        setLoading(false);
      });
  }, []);

  // ---- Derived data -------------------------------------------------------
  const { sicAptitudes, thirdPartyAptitudes, allMods, allCategories } = useMemo(() => {
    if (!data) return { sicAptitudes: [], thirdPartyAptitudes: [], allMods: [], allCategories: [] };

    const raw = (data.aptitudes || []).filter(apt => {
      const tags = apt.tags || [];
      return !tags.includes('hide_in_codex') && !tags.includes('dont_include_in_wiki');
    });

    const sic  = raw.filter(a => a.modName === 'Second-in-Command').sort((a, b) => a.order - b.order);
    const third = raw.filter(a => a.modName !== 'Second-in-Command').sort((a, b) => a.order - b.order);

    const thirdMods = [...new Set(third.map(a => a.modName))];
    const mods = ['Second-in-Command', ...thirdMods];

    // Collect unique categories from all aptitudes
    const catMap = {};
    raw.forEach(apt => {
      (apt.categories || []).forEach(cat => {
        if (cat.id) catMap[cat.id] = cat;
      });
    });
    const cats = Object.values(catMap);

    return { sicAptitudes: sic, thirdPartyAptitudes: third, allMods: mods, allCategories: cats };
  }, [data]);

  // Effective sets (null → everything)
  const effectiveMods = useMemo(
    () => activeMods || new Set(allMods),
    [activeMods, allMods]
  );
  const effectiveCategories = useMemo(
    () => activeCategories || new Set(allCategories.map(c => c.id)),
    [activeCategories, allCategories]
  );

  // ---- Filter helper ------------------------------------------------------
  const filterApt = useCallback((apt) => {
    if (!effectiveMods.has(apt.modName)) return false;
    // Category filter only applied when user has made a selection
    if (activeCategories !== null && allCategories.length > 0) {
      const aptCats = (apt.categories || []).map(c => c.id);
      if (aptCats.length === 0) return true; // no category = always pass
      return aptCats.some(id => effectiveCategories.has(id));
    }
    return true;
  }, [effectiveMods, effectiveCategories, activeCategories, allCategories]);

  const filteredSic   = useMemo(() => sicAptitudes.filter(filterApt),   [sicAptitudes,   filterApt]);
  const filteredThird = useMemo(() => thirdPartyAptitudes.filter(filterApt), [thirdPartyAptitudes, filterApt]);

  // ---- Filter handlers ----------------------------------------------------
  const handleModToggle = (mod) => {
    setActiveMods(prev => {
      const next = new Set(prev || allMods);
      next.has(mod) ? next.delete(mod) : next.add(mod);
      return next;
    });
  };

  const handleCategoryToggle = (catId) => {
    setActiveCategories(prev => {
      const next = new Set(prev || allCategories.map(c => c.id));
      next.has(catId) ? next.delete(catId) : next.add(catId);
      return next;
    });
  };

  const handleReset = () => { setActiveMods(null); setActiveCategories(null); };

  // ---- Render -------------------------------------------------------------
  if (loading) return <div className="loading">Loading skill matrix…</div>;

  if (error) return (
    <div className="error" style={{ whiteSpace: 'pre-line' }}>
      <strong>Failed to load data</strong>
      <br /><br />
      {error}
    </div>
  );

  if (!data) return null;

  const total = filteredSic.length + filteredThird.length;

  return (
    <div className="app">
      {/* ── Header ── */}
      <header className="app-header">
        <h1 className="app-title">
          Second-in-Command &nbsp;<span>Skill Browser</span>
        </h1>
        <FilterPanel
          allMods={allMods}
          allCategories={allCategories}
          activeMods={effectiveMods}
          activeCategories={effectiveCategories}
          onModToggle={handleModToggle}
          onCategoryToggle={handleCategoryToggle}
          onReset={handleReset}
        />
      </header>

      {/* ── Skill list ── */}
      <main className="skill-list">
        {filteredSic.length === 0 && filteredThird.length === 0 && (
          <div className="loading">No aptitudes match the current filters.</div>
        )}

        {/* Native SIC aptitudes */}
        {filteredSic.map(apt => <AptitudeRow key={apt.id} aptitude={apt} />)}

        {/* Third-party aptitudes */}
        {filteredThird.length > 0 && (
          <>
            <div className="third-party-divider">
              <span>Third-Party Mod Skills</span>
            </div>
            {filteredThird.map(apt => <AptitudeRow key={apt.id} aptitude={apt} />)}
          </>
        )}
      </main>

      {/* ── Footer ── */}
      <footer className="app-footer">
        <span>v{data.version}</span>
        &nbsp;·&nbsp;
        <span>exported {data.exportDate?.slice(0, 10)}</span>
        &nbsp;·&nbsp;
        <span>{total} aptitude{total !== 1 ? 's' : ''} shown</span>
      </footer>
    </div>
  );
}

// ── Mount ──
ReactDOM.createRoot(document.getElementById('root')).render(<App />);

