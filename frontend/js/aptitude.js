/* ============================================================
   aptitude.js — AptitudeRow
   Depends on: utils.js, skill.js, ui.js
   ============================================================ */

function AptitudeRow({ aptitude, sharedCategoryIds }) {
  const aptColor        = rgbaToCSS(aptitude.color);
  const isSIC           = aptitude.modName === 'Second-in-Command';

  const rowBg = `linear-gradient(to right, rgba(0,0,0,0.15), rgba(${aptitude.color.r},${aptitude.color.g},${aptitude.color.b},0.14))`;
  const borderLeftColor = aptColor;

  return (
    <div className="aptitude-row" id={`aptitude-${aptitude.id}`}>
      {/* Section title */}
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

