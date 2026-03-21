/* ============================================================
   gallery.js — GalleryCarousel
   Depends on: utils.js (useState, useEffect)
   ============================================================ */

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

