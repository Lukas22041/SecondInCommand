/* ============================================================
   app.js — App root component + mount
   Depends on: all other js/ files
   ============================================================ */

function App() {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  // ---- Load data ----------------------------------------------------------
  useEffect(() => {
    fetch('./api/api.json')
      .then(r => { if (!r.ok) throw new Error(`HTTP ${r.status}`); return r.json(); })
      .then(d  => { setData(d); setLoading(false); })
      .catch(() => {
        setError('Could not load api/api.json. Make sure you are serving this from an HTTP server (run start.py).');
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
      <SideNav showCrossMod={thirdPartyAptitudes.length > 0} />

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

