uniform vec3 uResolution;
uniform int uFrame;

uniform vec2 uTouch;
uniform int uPush;

float map(float value, float start1, float stop1, float start2, float stop2)
{
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

float norm(float value, float start, float stop)
{
    return map(value, start, stop, 0., 1.);
}

#define time float(uFrame) * (.02 + (.2 * smoothstep(0., 1., float(uPush) / float(uFrame))))

vec2 uv;

float sdOrientedBox(in vec2 p, in vec2 a, in vec2 b, float th)
{
    float l = length(b - a);
    vec2 d = (b - a) / l;
    vec2 q = (p - (a + b) * 0.5);
    q = mat2(d.x, -d.y, d.y, d.x) * q;
    q = abs(q) - vec2(l,th) * 0.5;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0);
}

float sdGridLine(vec2 cellPos, vec2 a, vec2 b){
    float lineLength = distance(a, b);
    float line = sdOrientedBox(cellPos, a, b, 0.);
    float smallLength = 0.25;
    float bigLength = 1.75;
    float closeness = smoothstep(bigLength, smallLength, lineLength);
    return closeness * smoothstep(0.05, 0., line);
}

vec2 getGridPoint(vec2 id){
	return vec2(0., sin(length(id) * 0.5 - time)) * 0.4;
}

float allLinesOnThisCell(vec2 cellPos, vec2[9] points){
 	float sum = 0.; //sdGridLine(cellPos, points[4], points[0]);
    sum += sdGridLine(cellPos, points[4], points[1]);
    //sum += sdGridLine(cellPos, points[4], points[2]);
    sum += sdGridLine(cellPos, points[4], points[3]);
    sum += sdGridLine(cellPos, points[4], points[5]);
    //sum += sdGridLine(cellPos, points[4], points[6]);
    sum += sdGridLine(cellPos, points[4], points[7]);
    //sum += sdGridLine(cellPos, points[4], points[8]);
    //sum += sdGridLine(cellPos, points[1], points[3]);
    //sum += sdGridLine(cellPos, points[1], points[5]);
    //sum += sdGridLine(cellPos, points[3], points[7]);
    return sum; // + sdGridLine(cellPos, points[5], points[7]);
}

void createPointMatrix(vec2 cellId, out vec2[9] pointMatrix){
    float range = 1.;
    int i = 0;
    for (float x = -range; x <= range; x++) {
        for (float y = -range; y <= range; y++) {
            vec2 offset = vec2(x,y);
            pointMatrix[i++] = getGridPoint(cellId + offset) + offset;
        }
    }
}

float render(vec2 uv){
    float scl = 45.;
    vec2 cellPos = fract(uv * scl) - .5;
    vec2 cellId = floor(uv * scl) + .5;

    vec2[9] pointMatrix;
    createPointMatrix(cellId, pointMatrix);

    return allLinesOnThisCell(cellPos, pointMatrix);
}

// anti-aliasing, pretty slow, unnoticeable when removed
float aarender(vec2 uv)
{
    float pixelHalf = (1. / uResolution.x) / 2.;
    vec2 aa = vec2(-pixelHalf, pixelHalf);
    float c1 = render(uv + aa.xx);
    float c2 = render(uv + aa.yy);
    return (c1 + c2) / 2.;
}

void main() {
    uv = (gl_FragCoord.xy - .5 * uResolution.xy) / uResolution.y;
    gl_FragColor = mix(vec4(vec3(0), 1.), vec4(vec3(1), 1.), norm(render(uv), 0., 1.));
    gl_FragColor.b *= (gl_FragCoord.xy / uResolution.xy).x;
    gl_FragColor.bg *= ((uResolution.xy - gl_FragCoord.xy) / uResolution.xy).x;
}