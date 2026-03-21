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
  // Multi-line: adjust highlight indices per-line so each line gets the correct highlights
  return (
    <div className="tooltip-label" style={{ paddingTop: el.padding || 0 }}>
      {lines.map((line, i) => {
        // Calculate start offset of this line in the original text
        let offset = 0;
        for (let j = 0; j < i; j++) offset += lines[j].length + 1; // +1 for the '\n'
        const lineEnd = offset + line.length;
        // Filter ranges that overlap this line and adjust to line-local indices
        const lineRanges = (el.highlightRanges || [])
          .filter(r => r.endIndex > offset && r.startIndex < lineEnd)
          .map(r => ({
            ...r,
            startIndex: Math.max(0, r.startIndex - offset),
            endIndex:   Math.min(line.length, r.endIndex - offset),
          }));
        return (
          <div key={i}>
            <HighlightedText text={line} highlightRanges={lineRanges} baseColor={baseColor} />
          </div>
        );
      })}
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
          if (child.type === 'title') return (
            <div key={i} className="tooltip-title" style={{ color: rgbaToCSS(child.color) }}>
              {child.text}
            </div>
          );
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
              <div key={i} className="tooltip-spacer" style={{ height: Math.max(2, el.height || 2) }} />
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

// ---- Tooltip Overlay (portal, fixed position, anchored below skill icon) --

function TooltipOverlay({ skill, aptitudeColor, anchorRef }) {
  const ref = useRef(null);
  const [pos, setPos] = useState({ left: -9999, top: -9999, visible: false });

  useLayoutEffect(() => {
    if (!ref.current || !anchorRef.current) return;
    const tooltipRect = ref.current.getBoundingClientRect();
    const iconRect    = anchorRef.current.getBoundingClientRect();
    const vw = window.innerWidth;
    const vh = window.innerHeight;

    // Anchor top-left of tooltip to bottom-left of skill icon
    let left = iconRect.left;
    let top  = iconRect.bottom;

    // Shift left if the tooltip would overflow the right edge of the viewport
    if (left + tooltipRect.width > vw - 22) {
      left = Math.max(10, vw - tooltipRect.width - 22);
    }

    // Only offset vertically when the tooltip would be cut off at the bottom
    if (top + tooltipRect.height > vh - 10) {
      top = Math.max(10, vh - tooltipRect.height - 10);
    }

    setPos({ left, top, visible: true });
  }, [skill]);

  const glowColor  = rgbaToCSS(aptitudeColor, 0.25);
  const glowColor2 = rgbaToCSS(aptitudeColor, 0.10);

  return ReactDOM.createPortal(
    <div
      ref={ref}
      className="tooltip-overlay"
      style={{
        left: pos.left,
        top:  pos.top,
        visibility: pos.visible ? 'visible' : 'hidden',
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
  const iconRef = useRef(null);

  // Hide tooltip immediately when the user scrolls (capture phase catches any scrollable ancestor)
  useEffect(() => {
    if (!hovered) return;
    const hide = () => setHovered(false);
    window.addEventListener('scroll', hide, true);
    return () => window.removeEventListener('scroll', hide, true);
  }, [hovered]);

  const borderColor = rgbaToCSS(aptitude.color);
  const glowColor   = rgbaToCSS(aptitude.color, hovered ? 0.6 : 0.2);
  const glowStrong  = rgbaToCSS(aptitude.color, 0.35);

  return (
    <div
      ref={iconRef}
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
    >
      <img src={skill.iconExportPath} alt={skill.name} />
      {hovered && (
        <TooltipOverlay
          skill={skill}
          aptitudeColor={aptitude.color}
          anchorRef={iconRef}
        />
      )}
    </div>
  );
}

// ---- Section separator (chevron + required count) -------------------------

function SectionSeparator({ aptitude }) {
  const { r, g, b } = aptitude.color;
  const strokeColor = `rgba(${r},${g},${b},0.5)`;
  return (
    <div className="section-separator">
      <svg className="separator-arrow-svg" viewBox="-1 0 22 72" width="22" height="72" overflow="visible">
        <polygon
          points="0,0 12,0 20,36 12,72 0,72"
          fill="none"
          stroke={strokeColor}
          strokeWidth="2"
          strokeLinejoin="miter"
          strokeMiterlimit="10"
        />
      </svg>
    </div>
  );
}

// ---- Skill Section (one section's flat icon row) -------------------------

function SkillSection({ section, aptitude }) {
  const { skills, canChooseMultiple } = section;
  const { r, g, b } = aptitude.color;
  const sepColor      = `rgba(${r},${g},${b},0.5)`;
  const ulColor       = `rgba(${r},${g},${b},0.75)`;
  const separatorGradient = `linear-gradient(to bottom, transparent 0%, ${sepColor} 70%, transparent 100%)`;
  const underlineGradient = `linear-gradient(to right,  transparent 0%, ${ulColor}  70%, transparent 100%)`;

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

function AptitudeRow({ aptitude, sharedCategoryIds }) {
  const aptColor        = rgbaToCSS(aptitude.color);
  const isSIC           = aptitude.modName === 'Second-in-Command';

  const rowBg = `linear-gradient(to right, rgba(0,0,0,0.15), rgba(${aptitude.color.r},${aptitude.color.g},${aptitude.color.b},0.14))`;
  const borderLeftColor = aptColor;

  return (
    <div className="aptitude-row" id={`aptitude-${aptitude.id}`}>
      {/* Section title — text is not a link; # anchor appears on hover */}
      <div className="aptitude-title">
        <span className="aptitude-title-text">{aptitude.name}</span>
        <AnchorCopy id={`aptitude-${aptitude.id}`} extraClass="aptitude-anchor" />
        {!isSIC && (
          <span
            className="aptitude-mod-badge"
            style={{
              color:       rgbaToCSS(aptitude.color, 0.85),
              borderColor: rgbaToCSS(aptitude.color, 0.35),
              background:  rgbaToCSS(aptitude.color, 0.07),
            }}
          >{aptitude.modName}</span>
        )}
      </div>

      {/* Colored accent bar */}
      <div className="aptitude-color-bar" style={{ background: aptColor }} />

      {/* Optional description */}
      {aptitude.description && (
        <p className="aptitude-description">{aptitude.description}</p>
      )}

      {/* Skills */}
      <div
        className="aptitude-skills-row"
        style={{ background: rowBg, borderLeftColor }}
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

      {/* Category notice */}
      {aptitude.categories && aptitude.categories.length > 0 && (() => {
        const cat = aptitude.categories[0];
        if (!sharedCategoryIds || !sharedCategoryIds.has(cat.id)) return null;
        const catColor = rgbaToCSS(cat.color);
        return (
          <div className="aptitude-category-notice">
            <img className="aptitude-category-notice-icon" src="appAssets/16x_star_circle.webp" alt="" />
            <span>
              This aptitude is part of the{' '}
              <span style={{ color: catColor }}>{cat.name}</span>
              {' '}category. It can not be used together with other aptitudes of the same category.
            </span>
          </div>
        );
      })()}
    </div>
  );
}

// ---- Share / copy-to-clipboard anchor ------------------------------------

function AnchorCopy({ id, extraClass = '' }) {
  const [copied, setCopied] = useState(false);

  const handleCopy = (e) => {
    e.preventDefault();
    const url = `${window.location.href.split('#')[0]}#${id}`;
    const write = () => {
      setCopied(true);
      setTimeout(() => setCopied(false), 1600);
    };
    if (navigator.clipboard) {
      navigator.clipboard.writeText(url).then(write).catch(write);
    } else {
      // fallback for file:// or older browsers
      const ta = document.createElement('textarea');
      ta.value = url;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.select();
      try { document.execCommand('copy'); } catch (_) {}
      document.body.removeChild(ta);
      write();
    }
  };

  return (
    <button
      className={`heading-anchor${extraClass ? ' ' + extraClass : ''}${copied ? ' copied' : ''}`}
      onClick={handleCopy}
      title="Copy link to clipboard"
    >
      {copied ? 'copied!' : 'share'}
    </button>
  );
}

// ---- Section Heading with hoverable anchor --------------------------------

function SectionHeading({ id, level = 2, children }) {
  const Tag = `h${level}`;
  const cls = level === 2 ? 'section-heading' : 'section-subheading';
  return (
    <Tag id={id} className={cls}>
      {children}
      <AnchorCopy id={id} />
    </Tag>
  );
}

// ---- Gallery Carousel -----------------------------------------------------

const GALLERY_IMAGES = [
  'appAssets/MainGallery1.png',
  'appAssets/MainGallery2.png',
  'appAssets/MainGallery3.png',
  'appAssets/MainGallery4.png',
];

function GalleryCarousel() {
  const COUNT   = GALLERY_IMAGES.length;
  const VISIBLE = 3;
  const [startIdx,    setStartIdx]    = useState(0);
  const [lightboxIdx, setLightboxIdx] = useState(null);

  const prev = () => setStartIdx(i => (i - 1 + COUNT) % COUNT);
  const next = () => setStartIdx(i => (i + 1) % COUNT);

  // ESC closes lightbox
  useEffect(() => {
    if (lightboxIdx === null) return;
    const handler = (e) => { if (e.key === 'Escape') setLightboxIdx(null); };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [lightboxIdx]);

  const visibleIndices = Array.from({ length: VISIBLE }, (_, i) => (startIdx + i) % COUNT);

  return (
    <>
      <div className="carousel">
        <div className="carousel-track">
          <button className="carousel-btn" onClick={prev} aria-label="Previous">&#8249;</button>
          <div className="carousel-multi">
            {visibleIndices.map((imgIdx) => (
              <div
                key={imgIdx}
                className="carousel-thumb-wrap"
                onClick={() => setLightboxIdx(imgIdx)}
                title="Click to enlarge"
              >
                <img
                  src={GALLERY_IMAGES[imgIdx]}
                  alt={`Gallery image ${imgIdx + 1}`}
                  className="carousel-thumb"
                />
              </div>
            ))}
          </div>
          <button className="carousel-btn" onClick={next} aria-label="Next">&#8250;</button>
        </div>
        <div className="carousel-dots">
          {GALLERY_IMAGES.map((_, i) => (
            <button
              key={i}
              className={`carousel-dot${i === startIdx ? ' active' : ''}`}
              onClick={() => setStartIdx(i)}
              aria-label={`Starting at image ${i + 1}`}
            />
          ))}
        </div>
      </div>

      {lightboxIdx !== null && ReactDOM.createPortal(
        <div className="lightbox-overlay" onClick={() => setLightboxIdx(null)}>
          <button
            className="lightbox-nav-btn lightbox-nav-prev"
            onClick={e => { e.stopPropagation(); setLightboxIdx(i => (i - 1 + COUNT) % COUNT); }}
            aria-label="Previous image"
          >&#8249;</button>
          <img
            src={GALLERY_IMAGES[lightboxIdx]}
            alt={`Gallery image ${lightboxIdx + 1}`}
            className="lightbox-image"
            onClick={e => e.stopPropagation()}
          />
          <button
            className="lightbox-nav-btn lightbox-nav-next"
            onClick={e => { e.stopPropagation(); setLightboxIdx(i => (i + 1) % COUNT); }}
            aria-label="Next image"
          >&#8250;</button>
          <button className="lightbox-close" onClick={() => setLightboxIdx(null)} aria-label="Close">✕</button>
        </div>,
        document.body
      )}
    </>
  );
}

// ---- About Section --------------------------------------------------------

function AboutSection({ sicAptitudeCount, sicSkillCount }) {
  return (
    <section className="page-section">
      <SectionHeading id="about" level={2}>About the Mod</SectionHeading>
      <div className="section-body">
        <p>
          Second-in-Command (SiC) is a feature overhaul mod for the game Starsector.
          It replaces the game's skill system with an alternative approach that still tries to keep it
          relatively familiar to the original system. Its focus is to allow further specialisation and to
          remove the issue of having to choose between fleet-wide and personal skills.
        </p>
        <p>
          Overall the mod currently adds <strong>{sicAptitudeCount}</strong> aptitudes with a total
          of <strong>{sicSkillCount}</strong> skills. Additional skills can be found through crossover
          content. The most defining aspect of the mod is that only three of these aptitudes can be
          active at a time.
        </p>
        <hr className="section-divider" />
        <p>
          In Vanilla, you have the Combat, <strong>Leadership</strong>, <strong>Technology</strong> and{' '}
          <strong>Industry</strong> aptitudes. In Second-in-Command, only the Combat tree remains, and
          the other aptitudes have been replaced with adjustable slots for Executive Officers.
        </p>
        <p>
          <strong>Executive Officers (XOs)</strong> are a new, separate type of officer that you can
          find by hiring them from the comm directory or coming across them during exploration. Executive
          Officers always have their own aptitude. Whatever Aptitude they have decides which skills will
          be available when they are put into one of the just mentioned slots. These officers have their
          own experience gain and skillpoints, and as such each Aptitude has their own skillpoints to
          allocate. They always have one skill pre-unlocked, and are able to gain 5 additional skill
          points over level ups.
        </p>
        <p>
          The player character themself now only gains a skillpoint on every second level, with some
          effects filling in the blanks. The player's skillpoints can now only be put into the combat
          skill tree, which has been extended with the four other combat skills from other vanilla
          aptitudes.
        </p>
        <p>
          Notably, as there are only three slots available, the player now has to make a more defined
          choice on what aptitudes they feel is important to what they want to go for. Another large
          change is that NPC Fleets can now also acquire skills that benefit their fleet as well,
          balancing the playfield to some extent.
        </p>
        <GalleryCarousel />
      </div>
    </section>
  );
}

// ---- Links Section --------------------------------------------------------

function LinksSection() {
  return (
    <section className="page-section">
      <SectionHeading id="links" level={2}>Links</SectionHeading>
      <div className="section-body">
        <ul className="links-list">
          <li>
            <a rel="nofollow noopener noreferrer" href="https://fractalsoftworks.com/forum/index.php?topic=30407.0" target="_blank">
              Forum Page
            </a>
          </li>
          <li>
            <a rel="nofollow noopener noreferrer" href="https://discord.gg/wgDCgS7PF3" target="_blank">
              Discord Server
            </a>
          </li>
          <li>
            <a rel="nofollow noopener noreferrer" href="https://github.com/Lukas22041/SecondInCommand/releases/latest/download/Second-in-Command.zip" target="_blank">
              Latest Download
            </a>
          </li>
          <li>
            <a rel="nofollow noopener noreferrer" href="https://github.com/Lukas22041/SecondInCommand/releases" target="_blank">
              Changelog &amp; Older Versions
            </a>
          </li>
        </ul>
      </div>
    </section>
  );
}

// ---- Incompatibilities Section --------------------------------------------

function IncompatibilitiesSection() {
  return (
    <section className="page-section">
      <SectionHeading id="incompatibilities" level={2}>Incompatibilities</SectionHeading>
      <div className="section-body">
        <p>
          Second-in-Command does heavy changes to the skill system, and due to that is{' '}
          <span style={{ color: '#ffd200' }}>not compatible with mods that themselves influence the original system</span>.
          <br />
          <strong>As such using any of the following mods will result in a crash upon loading the game:</strong>
        </p>
        <ul className="incompat-list incompat-crash">
          <li><span style={{ color: '#ff6400' }}>Quality Captains</span></li>
          <li><span style={{ color: '#ff6400' }}>A New Level of Confidence</span></li>
          <li><span style={{ color: '#ff6400' }}>Truly Automated Ships</span></li>
          <li><span style={{ color: '#ff6400' }}>Adjustable Skill Thresholds</span></li>
        </ul>
        <p><strong>Other mods are generally compatible, however the following should be noted:</strong></p>
        <ul className="incompat-list">
          <li>Skills from mods that do not appear on the skill menu will continue to work just fine (i.e. Digital Soul, Skills from RAT).</li>
          <li>Skills/Aptitudes from mods that would appear on the original skill menu are not visible.</li>
          <li>Some mods that check for Vanilla Skills may be unable to have their conditions fulfilled.</li>
          <li>Skill changes by other mods won't be applied, with the exception of changes to combat skills.</li>
          <li>If a mod provides you with vanilla skills, aside from combat ones, they will be automatically disabled. (i.e. UNGP)</li>
        </ul>
      </div>
    </section>
  );
}

// ---- App (root) ----------------------------------------------------------

function App() {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

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
  const { sicAptitudes, thirdPartyAptitudes, sharedCategoryIds } = useMemo(() => {
    if (!data) return { sicAptitudes: [], thirdPartyAptitudes: [], sharedCategoryIds: new Set() };

    const raw = (data.aptitudes || []).filter(apt => {
      const tags = apt.tags || [];
      return !tags.includes('hide_in_codex') && !tags.includes('dont_include_in_wiki');
    });

    const catCounts = {};
    raw.forEach(apt => {
      (apt.categories || []).forEach(cat => {
        if (cat.id) catCounts[cat.id] = (catCounts[cat.id] || 0) + 1;
      });
    });
    const sharedIds = new Set(Object.keys(catCounts).filter(id => catCounts[id] > 1));

    const sic   = raw.filter(a => a.modName === 'Second-in-Command').sort((a, b) => a.order - b.order);
    const third = raw.filter(a => a.modName !== 'Second-in-Command').sort((a, b) => a.order - b.order);

    return { sicAptitudes: sic, thirdPartyAptitudes: third, sharedCategoryIds: sharedIds };
  }, [data]);

  const sicSkillCount = useMemo(() =>
    sicAptitudes.reduce((total, apt) =>
      total + apt.sections.reduce((s, sec) => s + (sec.skills || []).length, 0), 0),
    [sicAptitudes]
  );

  const thirdPartySkillCount = useMemo(() =>
    thirdPartyAptitudes.reduce((total, apt) =>
      total + apt.sections.reduce((s, sec) => s + (sec.skills || []).length, 0), 0),
    [thirdPartyAptitudes]
  );

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

  return (
    <div className="app">
      {/* ── Banner ── */}
      <div className="page-banner">
        <img src="appAssets/Banner.png" alt="Second-in-Command" />
      </div>


      {/* ── Content sections ── */}
      <AboutSection sicAptitudeCount={sicAptitudes.length} sicSkillCount={sicSkillCount} />
      <LinksSection />
      <IncompatibilitiesSection />

      {/* ── Aptitudes ── */}
      <section className="page-section">
        <SectionHeading id="aptitudes" level={2}>Aptitudes</SectionHeading>

        <div className="aptitudes-subsection">
          <SectionHeading id="sic-aptitudes" level={3}>Second-in-Command Aptitudes</SectionHeading>
          <p className="subsection-description">Aptitudes and Skills from Second-in-Command itself.</p>
          <div className="skill-list">
            {sicAptitudes.map(apt => (
              <AptitudeRow key={apt.id} aptitude={apt} sharedCategoryIds={sharedCategoryIds} />
            ))}
          </div>
        </div>

        {thirdPartyAptitudes.length > 0 && (
          <div className="aptitudes-subsection">
            <SectionHeading id="cross-mod-aptitudes" level={3}>Cross-Mod Aptitudes</SectionHeading>
            <p className="subsection-description">
              Aptitudes added by mods other than Second-in-Command.{' '}
              Some of them are part of a larger mod, some are additional add-on mods that have to be
              installed separately.{' '}
              Currently <strong>{thirdPartyAptitudes.length}</strong> aptitudes with a total
              of <strong>{thirdPartySkillCount}</strong> skills.
            </p>
            <div className="skill-list">
              {thirdPartyAptitudes.map(apt => (
                <AptitudeRow key={apt.id} aptitude={apt} sharedCategoryIds={sharedCategoryIds} />
              ))}
            </div>
          </div>
        )}
      </section>
    </div>
  );
}

// ── Mount ──
ReactDOM.createRoot(document.getElementById('root')).render(<App />);

