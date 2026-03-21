/* ============================================================
   ui.js — AnchorCopy, SectionHeading
   Depends on: utils.js (useState)
   ============================================================ */

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

