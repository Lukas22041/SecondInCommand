
// Spotlight / vignette cutout shader for the SiC tutorial overlay.
//
// Renders the entire screen dark, with a smooth transparent "window" cut
// out over the focus rectangle.  Uses a box SDF so the fade at the border
// is perfectly smooth regardless of rect size.
//
// Uniforms
//   screenSize  – viewport dimensions in pixels (unused here; kept for parity)
//   focusRect   – vec4(x, y, width, height) of the highlight window in GL
//                 screen space (y = 0 at bottom of screen).
//   fadeRadius  – width in pixels of the fade zone at the rect border.
//   darkenAlpha – maximum opacity of the dark overlay (0–1).

uniform vec2 screenSize;
uniform vec4 focusRect;
uniform float fadeRadius;
uniform float darkenAlpha;

// Box signed-distance field.
// Returns negative values inside the box, 0 on its border,
// positive values outside (distance to nearest edge).
float boxSDF(vec2 p, vec2 center, vec2 halfSize) {
    vec2 d = abs(p - center) - halfSize;
    return length(max(d, vec2(0.0, 0.0))) + min(max(d.x, d.y), 0.0);
}

void main() {
    vec2 fragPos = gl_FragCoord.xy;

    vec2 center   = vec2(focusRect.x + focusRect.z * 0.5,
                         focusRect.y + focusRect.w * 0.5);
    vec2 halfSize = vec2(focusRect.z * 0.5,
                         focusRect.w * 0.5);

    float dist = boxSDF(fragPos, center, halfSize);

    float alpha;
    if (dist <= 0.0) {
        // Inside the focus window – fully transparent.
        alpha = 0.0;
    } else if (dist < fadeRadius) {
        // Transition zone – smoothly ramp from clear to dark.
        alpha = smoothstep(0.0, fadeRadius, dist) * darkenAlpha;
    } else {
        // Fully outside – maximum darkness.
        alpha = darkenAlpha;
    }

    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);
}

