/* ============================================================
   skill.js — SkillIcon, SectionSeparator, SkillSection
   Depends on: utils.js, tooltip.js
   ============================================================ */

// ---- Skill Icon -----------------------------------------------------------

function SkillIcon({ skill, aptitude }) {
  const isOrigin = skill.isOriginSkill;
  const size     = 90;

  const [hovered, setHovered] = useState(false);
  const iconRef = useRef(null);

  // Immediately hide during scroll; recover hover if the pointer is already
  // over this icon when the scroll settles (~150 ms of no scroll events).
  useEffect(() => {
    const el = iconRef.current;
    if (!el) return;
    let endTimer = null;
    const onScroll = () => {
      setHovered(false);
      clearTimeout(endTimer);
      endTimer = setTimeout(() => {
        const r = el.getBoundingClientRect();
        if (_ptrX >= r.left && _ptrX <= r.right && _ptrY >= r.top && _ptrY <= r.bottom) {
          setHovered(true);
        }
      }, 150);
    };
    window.addEventListener('scroll', onScroll, true);
    return () => { window.removeEventListener('scroll', onScroll, true); clearTimeout(endTimer); };
  }, []); // registered once per mount; _ptrX/_ptrY are module-level refs

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
      <img src={resolveApiPath(skill.iconExportPath)} alt={skill.name} />
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

// ---- Section separator (chevron) ------------------------------------------

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

// ---- Skill Section (one section's flat icon row) --------------------------

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

