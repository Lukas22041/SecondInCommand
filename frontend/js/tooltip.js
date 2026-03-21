/* ============================================================
   tooltip.js — HighlightedText, TooltipLabel, TooltipImageWithText,
                TooltipRenderer, TooltipOverlay
   Depends on: utils.js (rgbaToCSS, resolveApiPath, useRef,
               useState, useLayoutEffect)
   ============================================================ */

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
        src={resolveApiPath(el.exportPath)}
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

