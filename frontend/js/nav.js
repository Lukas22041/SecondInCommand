/* ============================================================
   nav.js — SideNav
   Depends on: utils.js (useState, useEffect, useMemo)
   ============================================================ */

const SIDE_NAV_SECTIONS = [
  { id: 'about',               label: 'About the Mod'            },
  { id: 'links',               label: 'Links'                    },
  { id: 'incompatibilities',   label: 'Incompatibilities'         },
  { id: 'aptitudes',           label: 'Aptitudes'                },
  { id: 'sic-aptitudes',       label: 'SiC Aptitudes',  sub: true },
  { id: 'cross-mod-aptitudes', label: 'Cross-Mod',      sub: true },
];

function SideNav({ showCrossMod }) {
  const [active, setActive] = useState('about');

  const sections = useMemo(
    () => showCrossMod
      ? SIDE_NAV_SECTIONS
      : SIDE_NAV_SECTIONS.filter(s => s.id !== 'cross-mod-aptitudes'),
    [showCrossMod]
  );

  useEffect(() => {
    const ids = sections.map(s => s.id);
    const update = () => {
      const offset = 90;
      let current = ids[0];
      for (const id of ids) {
        const el = document.getElementById(id);
        if (el && el.getBoundingClientRect().top <= offset) current = id;
      }
      setActive(current);
    };
    window.addEventListener('scroll', update, { passive: true });
    update();
    return () => window.removeEventListener('scroll', update);
  }, [sections]);

  return (
    <nav className="side-nav">
      {sections.map(({ id, label, sub }) => (
        <a
          key={id}
          href={`#${id}`}
          className={`side-nav-item${sub ? ' sub' : ''}${active === id ? ' active' : ''}`}
          onClick={e => {
            e.preventDefault();
            document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
          }}
        >
          {label}
        </a>
      ))}
    </nav>
  );
}

