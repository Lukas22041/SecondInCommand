/* ============================================================
   sections.js — AboutSection, LinksSection, IncompatibilitiesSection
   Depends on: ui.js (SectionHeading), gallery.js (GalleryCarousel)
   ============================================================ */

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

