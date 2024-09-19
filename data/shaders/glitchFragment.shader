
//Source: https://github.com/kuravih/gllock/blob/master/shaders/glitch.fragment.glsl

uniform sampler2D tex;

uniform vec3 colorMult;
uniform float iTime;
uniform float alphaMult;

float RATE = 0.00050;
vec2 texCoord = gl_TexCoord[0].xy;


float rand(vec2 co){
  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453) * 2.0 - 1.0;
}

float offset(float blocks, vec2 uv) {
  float shaderTime = iTime*RATE;
  return rand(vec2(shaderTime, floor(uv.y * blocks)));
}

void main() {
 	vec2 uv = texCoord;


  	vec4 color = texture(tex, uv);
 
  	color.r = texture(tex, uv + vec2(offset(128.0, uv) * 0.03, 0.0)).r;
  	color.g = texture(tex, uv + vec2(offset(128.0, uv) * 0.03 * 0.16666666, 0.0)).g;
  	color.b = texture(tex, uv + vec2(offset(128.0, uv) * 0.03, 0.0)).b;

	color.r *= colorMult.r;
	color.g *= colorMult.g;
	color.b *= colorMult.b;

	color.a *= alphaMult;
   	gl_FragColor = color;
}