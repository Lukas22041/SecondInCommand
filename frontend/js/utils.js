/* ============================================================
   utils.js — React hook globals, colour helpers, path resolver,
              pointer tracker
   ============================================================ */

const { useState, useEffect, useRef, useCallback, useLayoutEffect, useMemo, Fragment } = React;

// ---- Colour helpers --------------------------------------------------------

function rgbaToCSS(c, alphaOverride) {
  if (!c) return 'rgba(200,200,200,1)';
  const a = alphaOverride !== undefined ? alphaOverride : c.a / 255;
  return `rgba(${c.r},${c.g},${c.b},${a.toFixed(3)})`;
}

// ---- API path resolver -----------------------------------------------------
// api.json image paths are relative (e.g. "assets/foo.png").
// Since api.json and assets/ now live under api/, prefix them here so every
// component can use resolveApiPath() without knowing the folder layout.

function resolveApiPath(p) {
  if (!p) return p;
  if (p.startsWith('assets/')) return 'api/' + p;
  return p;
}

// ---- Module-level pointer tracker ------------------------------------------
// Browsers do not fire mouseenter/mouseover during scroll, so we track the
// pointer position here and use it to recover hover state after scroll settles.
let _ptrX = -1, _ptrY = -1;
window.addEventListener('mousemove', e => { _ptrX = e.clientX; _ptrY = e.clientY; }, { passive: true });

