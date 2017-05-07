precision mediump float;
varying vec2  coord;
uniform sampler2D state1;
uniform float textureSwitch;

void main() {
	vec4 texture;

	texture = texture2D(state1, coord);

	if(textureSwitch == 0.0) {
		texture = texture * 0.7;
	}
		
	gl_FragColor = texture;
}