
uniform sampler2D tex;      // the activeBorder sprite – used as a shape mask (rounded corners etc.)
uniform float alphaMult;

void main() {
    vec2 uv = gl_TexCoord[0].xy;

    // Sample the border sprite alpha as a mask so the glow follows its rounded corners exactly
    float borderMask = texture2D(tex, uv).a;

    vec3 gold = vec3(1.0, 0.65, 0.05);

    // Diagonal shine: top-left (uv.x=0, uv.y=1) bright → bottom-right (uv.x=1, uv.y=0) dark
    float diagGrad = clamp(1.0 - uv.x + uv.y - 0.5, 0.0, 1.0);
    float borderGlow = borderMask * diagGrad;

    // Interior fill: 0 on the border line, 1 well inside
    float distFromEdge = min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y));
    float interior = smoothstep(0.0, 0.1, distFromEdge);

    // Same diagonal direction as the border shine, covering the full interior background
    float interiorDiag = clamp(1.0 - uv.x + uv.y - 0.3, 0.0, 1.0);
    float interiorGlow = interior * interiorDiag * 0.20;

    gl_FragColor = vec4(gold, max(borderGlow, interiorGlow) * alphaMult);
}

