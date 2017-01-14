#include "GLShaders.h"
#include <string.h>

const char * shader_curve_fragment = 
	"precision mediump float; \n"

	"uniform sampler2D uTexture; \n"
	"uniform sampler2D uTextureCurve;\n"
	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"    vec4 color = texture2D(uTexture, vPosition.xy);\n"
	"    float r = texture2D(uTextureCurve, vec2(color.r, 0.0)).r;\n"
	"   float g = texture2D(uTextureCurve, vec2(color.g, 0.0)).g;\n"
	"    float b = texture2D(uTextureCurve, vec2(color.b, 0.0)).b;\n"
	"    gl_FragColor = vec4(r, g, b, 1.0);\n"
	"}\n";

const char * shader_linearburn_fragment = 
	"precision mediump float;\n"

	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureLayer;\n"
	"uniform float uLayerWeight;\n"
	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"    vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
	"    vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
	"    vec3 color = max(textureColor.rgb + layerColor.rgb - vec3(1.0), 0.0);\n"
	"    float weight = uLayerWeight * layerColor.a;\n"
	"    gl_FragColor = vec4(mix(textureColor.rgb, color, weight), textureColor.a);\n"
	"}\n";

const char * shader_multiply_fragment = 
	"precision mediump float; \n"

	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureLayer;\n"
	"uniform float uLayerWeight;\n"
	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"   vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
	"   vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
	"   float weight = uLayerWeight * layerColor.a;\n"
	"   vec3 color = layerColor.rgb * textureColor.rgb;\n"
	"   gl_FragColor = vec4(mix(textureColor.rgb, color, weight), textureColor.a);\n"
	"}\n";

const char * shader_overlay_fragment = 
	"precision mediump float; \n"
	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureLayer;\n"
	"uniform float uLayerWeight;\n"
	"varying vec4 vPosition;\n"

	"float BlendOverlayf(float base, float blend)\n"
	"{\n"
	"   return (base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)));\n"
	"}\n"

	"vec3 BlendOverlay(vec3 base, vec3 blend)\n"
	"{\n"
	"   return vec3(BlendOverlayf(base.r, blend.r), BlendOverlayf(base.g, blend.g), BlendOverlayf(base.b, blend.b));\n"
	"}\n"

	"void main()\n"
	"{\n"
	"   vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
	"   vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
	"   vec3 color = BlendOverlay(textureColor.rgb, layerColor.rgb);\n"
	"   float weight = uLayerWeight * layerColor.a;\n"
	"   gl_FragColor = vec4(mix(textureColor.rgb, color, weight), textureColor.a);\n"
	"}\n";

const char * shader_screen_fragment = 
	"precision mediump float; \n"
	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureLayer;\n"
	"uniform float uLayerWeight;\n"
	"varying vec4 vPosition;\n"

	"float BlendScreenf(float base, float blend)\n"
	"{\n"
	"   return (1.0 - ((1.0 - base) * (1.0 - blend)));\n"
	"}\n"

	"vec3 BlendScreen(vec3 base, vec3 blend)\n"
	"{\n"
	"   return vec3(BlendScreenf(base.r, blend.r), BlendScreenf(base.g, blend.g), BlendScreenf(base.b, blend.b));\n"
	"}\n"

	"void main()\n"
	"{\n"
	"   vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
	"   vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
	"   vec3 color = BlendScreen(textureColor.rgb, layerColor.rgb);\n"
	"   float weight = uLayerWeight * layerColor.a;\n"
	"   gl_FragColor = vec4(mix(textureColor.rgb, color, weight), textureColor.a);\n"
	"}\n";

const char * shader_darken_fragment = 
	"precision mediump float; \n"
	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureLayer;\n"
	"uniform float uLayerWeight;\n"
	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"   vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
	"   vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
	"   vec3 color = vec3(max(textureColor.r, layerColor.r), max(textureColor.g, layerColor.g), max(textureColor.b, layerColor.b));\n"
	"   float weight = uLayerWeight * layerColor.a;\n"
	"   gl_FragColor = vec4(mix(textureColor.rgb, color, weight), textureColor.a);\n"
	"}\n";

const char * shader_coverage_fragment = 
	"precision mediump float; \n"
	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureLayer;\n"
	"uniform float uLayerWeight;\n"
	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"   vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
	"   vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
	"   vec3 color = vec3(1.0, 1.0, 1.0);\n"
	"   color.r = (layerColor.r * layerColor.a + textureColor.r * (1.0-layerColor.a));\n"
	"   color.g = (layerColor.g * layerColor.a + textureColor.g * (1.0-layerColor.a));\n"
	"   color.b = (layerColor.b * layerColor.a + textureColor.b * (1.0-layerColor.a));\n"
	"   gl_FragColor = vec4(color.rgb, textureColor.a);\n"
	"}\n";

const char * shader_smooth_apply_fragment = 
	"precision mediump float;\n"

	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureTemplate;\n"
	"uniform sampler2D uTextureCurve;\n"

	"varying highp vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"    lowp vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
	"    lowp float t = texture2D(uTextureTemplate, vPosition.xy).g;\n"
	"    lowp float r = texture2D(uTextureCurve, vec2(textureColor.r, 0.0)).r;\n"
	"    lowp float g = texture2D(uTextureCurve, vec2(textureColor.g, 0.0)).g;\n"
	"    lowp float b = texture2D(uTextureCurve, vec2(textureColor.b, 0.0)).b;\n"
	"    lowp vec3 result = mix(vec3(r,g,b), textureColor.rgb, t);\n"
	"    gl_FragColor = vec4(result,1.0);\n"
	"}\n";

const char * shader_smooth_blur_fragment =
	"precision mediump float;\n"

	"uniform sampler2D inputImageTexture;\n"
	"uniform float texelWidthOffset;\n"
	"uniform float texelHeightOffset;\n"
	// uniform float radius;

	"varying vec2 centerTextureCoordinate;\n"
	"varying vec2 oneStepLeftTextureCoordinate;\n"
	"varying vec2 twoStepsLeftTextureCoordinate;\n"
	"varying vec2 threeStepsLeftTextureCoordinate;\n"
	" varying vec2 oneStepRightTextureCoordinate;\n"
	"varying vec2 twoStepsRightTextureCoordinate;\n"
	"varying vec2 threeStepsRightTextureCoordinate;\n"

	"void main()\n"
	" {\n"
	"    lowp vec4 fragmentColor = texture2D(inputImageTexture, centerTextureCoordinate) * 0.0772;\n"
	"    fragmentColor += texture2D(inputImageTexture, oneStepLeftTextureCoordinate) *  0.1538;\n"
	"    fragmentColor += texture2D(inputImageTexture, oneStepRightTextureCoordinate) * 0.1538;\n"
	"    fragmentColor += texture2D(inputImageTexture, twoStepsLeftTextureCoordinate) * 0.1538;\n"
	"    fragmentColor += texture2D(inputImageTexture, twoStepsRightTextureCoordinate) * 0.1538;\n"
	"    fragmentColor += texture2D(inputImageTexture, threeStepsRightTextureCoordinate) * 0.1538;\n"
	"    fragmentColor += texture2D(inputImageTexture, threeStepsLeftTextureCoordinate) * 0.1538;\n"
	"    gl_FragColor = fragmentColor;\n"
	" }\n";

const char * shader_smooth_blur_horizontal_vertex = 
	"attribute vec4 aPosition;\n"
	//attribute vec2 inputTextureCoordinate;

	"uniform mat4 uMVPMatrix;\n"
	"uniform float texelWidthOffset;\n"
	"uniform float texelHeightOffset;\n"

	"varying vec2 centerTextureCoordinate;\n"
	"varying vec2 oneStepLeftTextureCoordinate;\n"
	"varying vec2 twoStepsLeftTextureCoordinate;\n"
	"varying vec2 threeStepsLeftTextureCoordinate;\n"
	"varying vec2 oneStepRightTextureCoordinate;\n"
	"varying vec2 twoStepsRightTextureCoordinate;\n"
	"varying vec2 threeStepsRightTextureCoordinate;\n"

	"void main()\n"
	"{\n"
	"    gl_Position = uMVPMatrix * aPosition;\n"
	"    vec2 inputTextureCoordinate = aPosition.xy;\n"
	"    vec2 firstOffset = vec2(1.5 * texelWidthOffset, 0);\n"
	"   vec2 secondOffset = vec2(3.5 * texelWidthOffset, 0);\n"
	"   vec2 thirdOffset = vec2(5.5 * texelWidthOffset, 0);\n"

	"    centerTextureCoordinate = inputTextureCoordinate;\n"
	"    oneStepLeftTextureCoordinate = inputTextureCoordinate - firstOffset;\n"
	"    twoStepsLeftTextureCoordinate = inputTextureCoordinate - secondOffset;\n"
	"   threeStepsLeftTextureCoordinate = inputTextureCoordinate - thirdOffset;\n"
	"   oneStepRightTextureCoordinate = inputTextureCoordinate + firstOffset;\n"
	"   twoStepsRightTextureCoordinate = inputTextureCoordinate + secondOffset;\n"
	"    threeStepsRightTextureCoordinate = inputTextureCoordinate + thirdOffset;\n"
	"}\n";

const char * shader_smooth_blur_vertical_vertex = 
	"attribute vec4 aPosition;\n"
	//attribute vec2 inputTextureCoordinate;

	"uniform mat4 uMVPMatrix;\n"
	"uniform float texelWidthOffset;\n"
	"uniform float texelHeightOffset;\n"

	"varying vec2 centerTextureCoordinate;\n"
	"varying vec2 oneStepLeftTextureCoordinate;\n"
	"varying vec2 twoStepsLeftTextureCoordinate;\n"
	"varying vec2 threeStepsLeftTextureCoordinate;\n"
	"varying vec2 oneStepRightTextureCoordinate;\n"
	"varying vec2 twoStepsRightTextureCoordinate;\n"
	" varying vec2 threeStepsRightTextureCoordinate;\n"

	"void main()\n"
	"{\n"
	"    gl_Position = uMVPMatrix * aPosition;\n"
	"    vec2 inputTextureCoordinate = aPosition.xy;\n"
	"    vec2 firstOffset = vec2(0, 1.5 * texelHeightOffset);\n"
	"    vec2 secondOffset = vec2(0, 3.5 * texelHeightOffset);\n"
	"    vec2 thirdOffset = vec2(0, 5.5 * texelHeightOffset);\n"

	"    centerTextureCoordinate = inputTextureCoordinate;\n"
	"    oneStepLeftTextureCoordinate = inputTextureCoordinate - firstOffset;\n"
	"    twoStepsLeftTextureCoordinate = inputTextureCoordinate - secondOffset;\n"
	"    threeStepsLeftTextureCoordinate = inputTextureCoordinate - thirdOffset;\n"
	"    oneStepRightTextureCoordinate = inputTextureCoordinate + firstOffset;\n"
	"    twoStepsRightTextureCoordinate = inputTextureCoordinate + secondOffset;\n"
	"    threeStepsRightTextureCoordinate = inputTextureCoordinate + thirdOffset;\n"
	"}\n";

const char * shader_smooth_extract_selection_fragment = 
	"precision mediump float;\n"

	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureBlur;\n"

	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"    lowp float fragmentColor = (texture2D(uTexture, vPosition.xy).g - texture2D(uTextureBlur, vPosition.xy).g)*0.5 + 0.5;\n"
	"    gl_FragColor = vec4(0.0, fragmentColor , 0.0 ,1.0);\n"
	"}\n";

const char * shader_smooth_template_fragment = 
	"precision mediump float;\n"

	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureCurve;\n"

	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"   float fragmentColor = texture2D(uTexture, vPosition.xy).g;\n"
	"    //fragmentColor = (fragmentColor <= 0.5)? (2.0*fragmentColor*fragmentColor) : (1.0 - (1.0 - 2.0 * (fragmentColor - 0.5)) * (1.0 - fragmentColor));\n"
	"    fragmentColor = texture2D(uTextureCurve, vec2(fragmentColor, 0.0)).g;\n"
	"    gl_FragColor = vec4(0.0,fragmentColor,0.0,1.0);\n"
	"}\n";

const char * shader_vertex = 
	"attribute vec4 aPosition;\n"
	"uniform mat4 uMVPMatrix;\n"
	"varying vec4 vPosition;\n"
	"void main() {\n"
	"  vPosition = aPosition;\n"
	"  gl_Position = uMVPMatrix * vPosition;\n"
	"}\n";

const char * shader_yuv_fragment = 
	"precision mediump float;\n"

	"varying vec4 vPosition;\n"
	"uniform sampler2D uTextureY;\n"
	"uniform sampler2D uTextureUV;\n"

	"void main() {\n"
	"    vec2 texturePos = vPosition.xy;\n"
	"    vec3 yuv;\n"
	"    yuv.x = texture2D(uTextureY, texturePos).x;\n"
	"    yuv.yz = texture2D(uTextureUV, texturePos / vec2(2.0, 2.0)).ar - vec2(0.5, 0.5);\n"
	"//    yuv.yz = vec2(-0.5, -0.5);\n"
	"    vec3 rgb = mat3(     1,        1,       1, //first column\n"
	"                         -0.00093, -0.3437, 1.77216, //second column\n"
	"                         1.401687, -0.71417, 0.00099) //third column\n"
	"                   * yuv;\n"
	"    gl_FragColor = vec4(rgb, 1.0);\n"
	"}\n";

const char * shader_saturation_fragment = 
	"precision mediump float; \n"

	"uniform sampler2D uTexture; \n"
	"uniform float uSat;\n"
	"varying vec4 vPosition;\n"

	"vec3 ContrastSaturationBrightness(vec3 color, float brt, float sat, float con)\n"
	"{\n"
	"   const float AvgLumR = 0.5;\n"
	"   const float AvgLumG = 0.5;\n"
	"   const float AvgLumB = 0.5;\n"

	"   const vec3 LumCoeff = vec3(0.2125, 0.7154, 0.0721);\n"

	"  vec3 AvgLumin = vec3(AvgLumR, AvgLumG, AvgLumB);\n"
	"   vec3 brtColor = color * brt;\n"
	"   vec3 intensity = vec3(dot(brtColor, LumCoeff));\n"
	"   vec3 satColor = mix(intensity, brtColor, sat);\n"
	"   vec3 conColor = mix(AvgLumin, satColor, con);\n"
	"   return conColor;\n"
	"}\n"

	"void main()\n"
	"{\n"
	"    vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
	"    vec3 color = ContrastSaturationBrightness(textureColor.rgb, 1.0, uSat, 1.0);\n"
	"    gl_FragColor = vec4(color, 1.0);\n"
	"}\n";

const char * shader_empty_fragment = 
	"precision mediump float; \n"

	"uniform sampler2D uTexture; \n"
	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"    gl_FragColor = texture2D(uTexture, vPosition.xy);\n"
	"}\n";

const char * shader_scene_enhance_fragment = 
	"precision mediump float;\n"

	"uniform sampler2D uTexture;\n"
	"uniform sampler2D uTextureCurve;\n"
	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"    mat3 MAT_RGB2LAB = mat3(0.2126,  0.3259,  0.1218, 0.7152, -0.4993,  0.3786, 0.0722,  0.1733, -0.5004);\n"
	"    mat3 MAT_LAB2RGB = mat3(     1,       1,       1, 2.093, -0.6260,  0.0361, 0.8695, -0.0724, -1.844);\n"
	"    vec4 color = texture2D(uTexture, vPosition.xy);\n"
	"    vec3 lab = MAT_RGB2LAB * color.rgb;\n"
	"    float r = lab.r;\n"
	"    float g = texture2D(uTextureCurve, vec2(lab.g + 0.5, 0.0)).g - 0.5; //value a from -0.5 ~ 0.5\n"
	"    float b = texture2D(uTextureCurve, vec2(lab.b + 0.5, 0.0)).b - 0.5; //value b from -0.5 ~ 0.5\n"
	"    lab = vec3(r, g, b);\n"
	"    vec3 rgb = MAT_LAB2RGB * lab;\n"
	"    gl_FragColor = vec4(rgb, color.a);\n"
	"}\n";

const char * shader_rgb_fragment = 
	"#extension GL_OES_EGL_image_external : require\n"
	"precision mediump float;\n"

	"varying vec4 vPosition;\n"
	"uniform samplerExternalOES uTexture;\n"
	"//uniform sampler2D uTextureUV;\n"

	"void main() {\n"
	" //   vec2 texturePos = vPosition.xy;\n"
	" //   vec3 yuv;\n"
	"//    yuv.x = texture2D(uTextureY, texturePos).x;\n"
	"//    yuv.yz = texture2D(uTextureUV, texturePos / vec2(2.0, 2.0)).ar - vec2(0.5, 0.5);\n"
	"//    yuv.yz = vec2(-0.5, -0.5);\n"
	"//    vec3 rgb = mat3(     1,        1,       1, //first column\n"
	" //                        0,        -.21482, 2.12798, //second column\n"
	" //                        1.28033,  -.38059, 0) //third column\n"
	" //                  * yuv;\n"
	"  //  gl_FragColor = vec4(rgb, 1.0);\n"
	"gl_FragColor = texture2D(uTexture,vPosition.xy);\n"
	"}\n";

const char * shader_vertex_2 = 
	"attribute vec4 aPosition;\n"
	"uniform mat4 uMVPMatrix;\n"
	"varying vec4 vPosition;\n"

	"void main() {\n"
	"  gl_Position = uMVPMatrix * aPosition;\n"
	"  vPosition = aPosition;\n"

	"}\n";

const char * shader_high_light_fragment = 
	"precision mediump float;\n"

	"uniform sampler2D uTexture;\n"
	"varying vec4 vPosition;\n"

	"void main()\n"
	"{\n"
	"vec4 texture_color = texture2D(uTexture,vPosition.xy);\n"
	"float gray_src = (texture_color.r + texture_color.g + texture_color.b)/3.0;\n"
	"float gray_dst = gray_src > 0.5 ? (gray_src) : (0.5 -(gray_src-0.5)*(gray_src-0.5)/0.5);//1/2鎶涚墿绾縗n"
	"//float gray_dst = sqrt((gray_src*2.0000 - gray_src * gray_src));//1/4 鍦哱n"
	"//float gray_dst = 1.0000 - (gray_src-1.0000)*(gray_src-1.0000);//鎶涚墿绾縗n"
	"//float gray_dst = getMIN((gray_src*3.0000), 1.0000);//getMIN涓嶆槸鍘熺敓鏀寔鐨刓n"

	"vec4 mask_color;\n"
	"if(gray_src > 0.0001) {\n"
	"	mask_color = min(texture_color * gray_dst / gray_src, vec4(1.0000));\n"
	"} else {\n"
	"	mask_color = texture_color;\n"
	"}\n"
	"float gray_mean = (mask_color.r+mask_color.g+mask_color.b+texture_color.r+texture_color.g+texture_color.b)/6.0000;\n"
	"mask_color = texture_color * gray_mean + mask_color * (1.0000 - gray_mean);\n"
	"gl_FragColor = vec4(mask_color.rgb,texture_color.a);\n"
	"}\n";
// [3/25/2014 yubowen] 鍒嗗壊绾�
const char* shader_kirsch_vertex =
		"attribute vec4 aPosition;\n"
		"uniform mat4 uMVPMatrix;\n"
		"uniform float texelWidth;\n"
		"uniform float texelHeight;\n"
		"varying vec4 vPosition;\n"
		"varying vec2 vPos1;\n"
		"varying vec2 vPos2;\n"
		"varying vec2 vPos3;\n"
		"varying vec2 vPos4;\n"
		"varying vec2 vPos5;\n"
		"varying vec2 vPos6;\n"
		"varying vec2 vPos7;\n"
		"varying vec2 vPos8;\n"
		"varying vec2 vPos9;\n"
		"void main()\n"
		"{\n"
		    "vPosition = aPosition;\n"
		    "gl_Position = uMVPMatrix * aPosition;\n"
		    "vPos1 = aPosition.xy + vec2(-texelWidth, -texelHeight);\n"
		    "vPos2 = aPosition.xy + vec2(0, -texelHeight);\n"
		    "vPos3 = aPosition.xy + vec2(texelWidth, -texelHeight);\n"
		    "vPos4 = aPosition.xy + vec2(-texelWidth, 0);\n"
		    "vPos5 = aPosition.xy + vec2(0, 0);\n"
		    "vPos6 = aPosition.xy + vec2(texelWidth, 0);\n"
		    "vPos7 = aPosition.xy + vec2(-texelWidth, texelHeight);\n"
		    "vPos8 = aPosition.xy + vec2(0, texelHeight);\n"
		    "vPos9 = aPosition.xy + vec2(texelWidth, texelHeight);\n"
		"}\n";
const char* shader_kirsch_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture;\n"
		"varying vec4 vPosition;\n"
		"varying vec2 vPos1;\n"
		"varying vec2 vPos2;\n"
		"varying vec2 vPos3;\n"
		"varying vec2 vPos4;\n"
		"varying vec2 vPos5;\n"
		"varying vec2 vPos6;\n"
		"varying vec2 vPos7;\n"
		"varying vec2 vPos8;\n"
		"varying vec2 vPos9;\n"

		"void main() {\n"

		   "vec4 d = vec4(0.333333, 0.333333, 0.333333, 0.0);\n"
		   "float c1 = dot(texture2D(uTexture, vPos1.xy), d);\n"
		   "float c2 = dot(texture2D(uTexture, vPos2.xy), d);\n"
		   "float c3 = dot(texture2D(uTexture, vPos3.xy), d);\n"
		   "float c4 = dot(texture2D(uTexture, vPos4.xy), d);\n"
		   "float c6 = dot(texture2D(uTexture, vPos6.xy), d);\n"
		   "float c7 = dot(texture2D(uTexture, vPos7.xy), d);\n"
		   "float c8 = dot(texture2D(uTexture, vPos8.xy), d);\n"
		   "float c9 = dot(texture2D(uTexture, vPos9.xy), d);\n"

		   "float top = - 1.25 * c1 - 1.25 * c2 - 1.25 * c3 + 0.75 * c4 + 0.75 * c6 + 0.75 * c7 + 0.75 * c8 + 0.75 * c9;\n"
		   "float bottom = 0.75 * c1 + 0.75 * c2 + 0.75 * c3 + 0.75 * c4 + 0.75 * c6 - 1.25 * c7 - 1.25 * c8 - 1.25 * c9;\n"
		   "float left = - 1.25 * c1 + 0.75 * c2 + 0.75 * c3 - 1.25 * c4 + 0.75 * c6 - 1.25 * c7 + 0.75 * c8 + 0.75 * c9;\n"
		   "float right = 0.75 * c1 + 0.75 * c2 - 1.25 * c3 + 0.75 * c4 - 1.25 * c6 + 0.75 * c7 + 0.75 * c8 - 1.25 * c9;\n"

		   "float m = min(max(max(max(top, bottom), left), right), 1.0);\n"
		   "float ret = clamp(0.7843 - m, 0.3922, 1.0);\n"
		   "gl_FragColor = vec4(ret, ret, ret, 1.0);\n"
		"}\n";
const char* shader_compression_fragment =
		"precision mediump float;\n"

		"uniform sampler2D uTexture;\n"
		"uniform float uLowEdge, uHighEdge;\n"
		"varying vec4 vPosition;\n"

		"void main()\n"
		"{\n"
		    "vec3 color = texture2D(uTexture, vPosition.xy).xyz;\n"
		    "color.x = color.x * (uHighEdge - uLowEdge) + uLowEdge;\n"
		    "color.y = color.y * (uHighEdge - uLowEdge) + uLowEdge;\n"
		    "color.z = color.z * (uHighEdge - uLowEdge) + uLowEdge;\n"
		    "gl_FragColor = vec4(color, 1.0);\n"
		"}\n";
const char* shader_softlight_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture;\n"
		"uniform sampler2D uTextureLayer;\n"
		"uniform float uLayerWeight;\n"
		"varying vec4 vPosition;\n"

		"float BlendSoftLightf(float base, float blend)\n"
		"{\n"
		   "return ((blend < 0.5) ? (2.0 * base * blend + base * base * (1.0 - 2.0 * blend)) : (sqrt(base) * (2.0 * blend - 1.0) + 2.0 * base * (1.0 - blend)));\n"
		"}\n"

		"vec3 BlendSoftLight(vec3 base, vec3 blend)\n"
		"{\n"
		   "return vec3(BlendSoftLightf(base.r, blend.r), BlendSoftLightf(base.g, blend.g),BlendSoftLightf(base.b, blend.b));\n"
		"}\n"

		"void main()\n"
		"{\n"
		  " vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
		  " vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
		   "vec3 color = BlendSoftLight(textureColor.rgb, layerColor.rgb);\n"
		   "float weight = uLayerWeight * layerColor.a;\n"
		  " gl_FragColor = vec4(mix(textureColor.rgb, color, weight), textureColor.a);\n"
		"}\n";
const char* shader_kirsch1_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture;\n"
		"varying vec4 vPosition;\n"
		"varying vec2 vPos1;\n"
		"varying vec2 vPos2;\n"
		"varying vec2 vPos3;\n"
		"varying vec2 vPos4;\n"
		"varying vec2 vPos5;\n"
		"varying vec2 vPos6;\n"
		"varying vec2 vPos7;\n"
		"varying vec2 vPos8;\n"
		"varying vec2 vPos9;\n"

		"void main() {\n"

		   "vec4 d = vec4(0.333333, 0.333333, 0.333333, 0.0);\n"
		   "float c1 = dot(texture2D(uTexture, vPos1.xy), d);\n"
		   "float c2 = dot(texture2D(uTexture, vPos2.xy), d);\n"
		   "float c3 = dot(texture2D(uTexture, vPos3.xy), d);\n"
		   "float c4 = dot(texture2D(uTexture, vPos4.xy), d);\n"
		   "float c6 = dot(texture2D(uTexture, vPos6.xy), d);\n"
		   "float c7 = dot(texture2D(uTexture, vPos7.xy), d);\n"
		   "float c8 = dot(texture2D(uTexture, vPos8.xy), d);\n"
		   "float c9 = dot(texture2D(uTexture, vPos9.xy), d);\n"

		   "float top = - 1.25 * c1 - 1.25 * c2 - 1.25 * c3 + 0.75 * c4 + 0.75 * c6 + 0.75 * c7 + 0.75 * c8 + 0.75 * c9;\n"
		   "float bottom = 0.75 * c1 + 0.75 * c2 + 0.75 * c3 + 0.75 * c4 + 0.75 * c6 - 1.25 * c7 - 1.25 * c8 - 1.25 * c9;\n"
		   "float left = - 1.25 * c1 + 0.75 * c2 + 0.75 * c3 - 1.25 * c4 + 0.75 * c6 - 1.25 * c7 + 0.75 * c8 + 0.75 * c9;\n"
		   "float right = 0.75 * c1 + 0.75 * c2 - 1.25 * c3 + 0.75 * c4 - 1.25 * c6 + 0.75 * c7 + 0.75 * c8 - 1.25 * c9;\n"

		   "float m = min(max(max(max(top, bottom), left), right), 1.0);\n"
		   "float ret = clamp(1.0 - m, 0.3922, 1.0);\n"
		   "gl_FragColor = vec4(ret, ret, ret, 1.0);\n"
		"}\n";
const char* shader_sobel_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture;\n"
		"varying vec4 vPosition;\n"
		"varying vec2 vPos1, vPos2, vPos3, vPos4, vPos5, vPos6, vPos7, vPos8, vPos9;\n"
		"//1, 4, 7\n"
		"//2, 5, 8\n"
		"//3, 6, 9\n"
		"void main()\n"
		"{\n"
		   "//caculate the gray value of each related pixel\n"
		   "vec4 d = vec4(0.333333, 0.333333, 0.333333, 0.0);\n"
		   "float c1 = dot(texture2D(uTexture, vPos1.xy), d);\n"
		   "float c2 = dot(texture2D(uTexture, vPos2.xy), d);\n"
		   "float c3 = dot(texture2D(uTexture, vPos3.xy), d);\n"
		   "float c4 = dot(texture2D(uTexture, vPos4.xy), d);\n"
		"//   float c5 = dot(texture2D(uTexture, vPos5.xy), d);\n"
		   "float c6 = dot(texture2D(uTexture, vPos6.xy), d);\n"
		   "float c7 = dot(texture2D(uTexture, vPos7.xy), d);\n"
		   "float c8 = dot(texture2D(uTexture, vPos8.xy), d);\n"
		   "float c9 = dot(texture2D(uTexture, vPos9.xy), d);\n"
		   "float sobel_x = abs((c7-c1) + 2.0*(c8-c2) + (c9-c3));\n"
		   "float sobel_y = abs((c1-c3) + 2.0*(c4-c6) + (c7-c9));\n"
		   "float sobel = sobel_x + sobel_y;\n"
		   "float m = max(min((sobel-0.196)/3.0,1.0),0.0);\n"
		   "float ret = clamp(0.7843 - m, 0.3922, 1.0);\n"
		   "gl_FragColor = vec4(ret, ret, ret, 1.0);\n"
		"}\n";

const char* shader_rgb2gray_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture; \n"
		"varying vec4 vPosition;\n"
		"void main()\n"
		"{\n"
		    "vec3 color = texture2D(uTexture, vPosition.xy).xyz;\n"
			"float gray = (color.x + color.y + color.z) /3.0;\n"
		    "color.x = gray;\n"
		    "color.y = gray;\n"
		    "color.z = gray;\n"
		    "gl_FragColor = vec4(color, 1.0);\n"
		"}\n";

const char* shader_posterize_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture;\n"
		"uniform float uLevels;\n"
		"varying vec4 vPosition;\n"
		"void main()\n"
		"{\n"
		    "vec3 color = texture2D(uTexture, vPosition.xy).xyz;\n"
		    "float step = (1.0/uLevels);\n"
		    "color.x = floor (color.x /step) * step;\n"
		    "color.y = floor (color.y /step) * step;\n"
		    "color.z = floor (color.z /step) * step;\n"
		    "gl_FragColor = vec4(color, 1.0);\n"
		"}\n";
const char* shader_generate_blue_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture;\n"
		"varying vec4 vPosition;\n"
		"void main()\n"
		"{\n"
		    "vec3 color = texture2D(uTexture, vPosition.xy).xyz;\n"
		    "color.x = 0.15686;\n"
		    "color.y = 0.17647;\n"
		    "color.z = 0.57647;\n"
		    "gl_FragColor = vec4(color, 1.0);\n"
		"}\n";

const char* shader_pencil_OverLay_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture; //camera image\n"
		"uniform sampler2D uTextureLayer; // pencilstroke\n"
		"uniform float uLayerWeight;\n"
		"varying vec4 vPosition;\n"
		"void main()\n"
		"{\n"
		    "vec3 color = texture2D(uTexture, vPosition.xy).xyz;\n"
			"vec3 colorLayer = texture2D(uTextureLayer, vPosition.xy).xyz;\n"
		    "float thre0 = 0.3137;\n"
			"float thre1 = 0.3529;\n"
			"float thre2 = 0.6471;\n"
			"float thre3 = 0.6863;\n"
			"float gray = (color.x + color.y + color.z)/3.0;\n"
			"//high light\n"
			"float alpha = gray>thre2 ? gray:0.0;\n"
			"color.x = color.x*(1.0-alpha)+colorLayer.x*alpha;\n"
			"color.y = color.y*(1.0-alpha)+colorLayer.x*alpha;\n"
			"color.z = color.z*(1.0-alpha)+colorLayer.x*alpha;\n"
		 	"//middle light\n"
			"alpha = gray>thre3 ? 0.0:gray;\n"
			"alpha = alpha<thre0 ? 0.0:alpha;\n"
			"color.x = color.x*(1.0-alpha)+colorLayer.y*alpha;\n"
			"color.y = color.y*(1.0-alpha)+colorLayer.y*alpha;\n"
			"color.z = color.z*(1.0-alpha)+colorLayer.y*alpha;\n"
			"//dark\n"
			"alpha = gray<thre1 ? gray:0.0;\n"
			"color.x = color.x*(1.0-alpha)+colorLayer.z*alpha;\n"
			"color.y = color.y*(1.0-alpha)+colorLayer.z*alpha;\n"
			"color.z = color.z*(1.0-alpha)+colorLayer.z*alpha;\n"
		    "gl_FragColor = vec4(color, 1.0);\n"
		"}\n";

const char* shader_hope_effect_fragment =
		"precision mediump float; \n"
		"uniform sampler2D uTexture;\n"
		"uniform sampler2D uTextureLayer;\n"
		"varying vec4 vPosition;\n"

		"void main()\n"
		"{\n"
		"   vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
		"   vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
	    "   mat3 MAT_RGB2LAB = mat3(0.2126,  0.3259,  0.1218, 0.7152, -0.4993,  0.3786, 0.0722,  0.1733, -0.5004);\n"
	    "   vec3 lab = MAT_RGB2LAB * textureColor.rgb;\n"
	    "   vec3 color = textureColor.rgb;\n"
		"   float r = lab.r;\n"
	    "	if (r < 0.2353) {\n"
	    "		color =  vec3(0.0, 0.1961, 0.2314);\n"
	    "	} else if (r >= 0.2353 && r < 0.3922) {\n"
	    "		color =  vec3(0.8431, 0.1019, 0.1294);\n"
	    "	} else if (r >= 0.3922 && r < 0.5882) {\n"
	    "		color = vec3(0.4863, 0.6431, 0.6824);\n"
	    "	} else if(r >=  0.5882 && r < 0.6980) {\n"
	    "		color = layerColor.rgb;\n"
	    "	} else if(r >= 0.6980) {\n"
	    "		color = vec3(0.9882, 0.8941, 0.6588);\n"
	    "	}\n"

		"   gl_FragColor = vec4(color.rgb, textureColor.a);\n"
		"}\n";

const char* shader_skin_detect_fragment =
			"precision highp float;\n"

			"uniform sampler2D uTexture; \n"
			"varying vec4 vPosition;\n"

			"void main()\n"
			"{\n"
			    "vec3 color = texture2D(uTexture, vPosition.xy).xyz;\n"
	    		"gl_FragColor = vec4(color, 1.0);\n"
			    "float flag = 0.0;\n"
				"float min,max,sum;\n"
			    "if (color.x>0.2745 && color.y>0.07843 && color.z>0.03922 && color.x>color.y && color.x>color.z && (color.x-color.y>0.02745 || color.y - color.x > 0.02745))\n"
			    "{\n"
				    "min = color.x<color.y?color.x:color.y;\n"
					"min = min    <color.z?min    :color.z;\n"
					"max = color.x>color.y?color.x:color.y;\n"
					"max = max    >color.z?max    :color.z;\n"
					"if((max-min)>0.02745) flag = 1.0;\n"
					"else                  flag = 0.0;\n"
				"}\n"
				"else flag = 0.0;\n"
				"if(0.0 == flag)\n"
				"{\n"
			        "if((color.x-color.y)<0.05882) flag = 0.0;\n"
					"else if( (color.x<=color.y)||(color.y<=color.z)) flag = 0.0;\n"
					"else\n"
					"{\n"
					    "sum = color.x + color.y + color.z;\n"
						"if(((156.0*color.x - 52.0*sum)*(156.0*color.x - 52.0*sum) + (156.0*color.y - 52.0*sum)*(156.0*color.y - 52.0*sum))<(sum * sum / 16.0)) flag = 0.0;\n"
			            "else\n"
			            "{\n"
						    "float T1 = 10000.0 * color.y * sum;\n"
						    "float Lower = - 7760.0 * color.x * color.x + 5601.0 * color.x * sum + 1766.0 * sum * sum;\n"
							"float Upper = - 13767.0 * color.x * color.x + 10743.0 * color.x * sum + 1452.0 * sum * sum ;\n"
							"if(T1<=Lower || T1>=Upper) flag = 0.0;\n"
							"else flag = 1.0;\n"
			            "}\n"
					"}\n"
				"}\n"


			    "gl_FragColor = vec4(color, flag);\n"
			"}\n";
const char* shader_skin_overlay_fragment =
		"precision mediump float; \n"
		"uniform sampler2D uTexture;\n"
		"uniform sampler2D uTextureLayer;\n"
		"uniform sampler2D uTextureWeight;\n"
		"varying vec4 vPosition;\n"

		"void main()\n"
		"{\n"
		"   vec4 textureColor = texture2D(uTexture, vPosition.xy);\n"
		"   vec4 layerColor = texture2D(uTextureLayer, vPosition.xy);\n"
		"   vec4 weight = texture2D(uTextureWeight, vPosition.xy);\n"
		"   vec3 color;\n"
		"   color.r = (layerColor.r * weight.a + textureColor.r * (1.0-weight.a));\n"
		"   color.g = (layerColor.g * weight.a + textureColor.g * (1.0-weight.a));\n"
		"   color.b = (layerColor.b * weight.a + textureColor.b * (1.0-weight.a));\n"



		"   gl_FragColor = vec4(color.rgb, textureColor.a);\n"
		"}\n";
const char* shader_alpha_meansmooth_fragment =
		"precision mediump float;\n"
		"uniform sampler2D uTexture;\n"
		"varying vec4 vPosition;\n"
		"varying vec2 vPos1;\n"
		"varying vec2 vPos2;\n"
		"varying vec2 vPos3;\n"
		"varying vec2 vPos4;\n"
		"varying vec2 vPos5;\n"
		"varying vec2 vPos6;\n"
		"varying vec2 vPos7;\n"
		"varying vec2 vPos8;\n"
		"varying vec2 vPos9;\n"

		"void main()\n"
		"{\n"
		"   vec4 c1 = texture2D(uTexture, vPos1.xy);\n"
		"   vec4 c2 = texture2D(uTexture, vPos2.xy);\n"
		"   vec4 c3 = texture2D(uTexture, vPos3.xy);\n"
		"   vec4 c4 = texture2D(uTexture, vPos4.xy);\n"
		"   vec4 c5 = texture2D(uTexture, vPos5.xy);\n"
		"   vec4 c6 = texture2D(uTexture, vPos6.xy);\n"
		"   vec4 c7 = texture2D(uTexture, vPos7.xy);\n"
		"   vec4 c8 = texture2D(uTexture, vPos8.xy);\n"
		"   vec4 c9 = texture2D(uTexture, vPos9.xy);\n"
		"   float rmean = (c1.r + c2.r + c3.r + c4.r + c5.r + c6.r + c7.r + c8.r + c9.r)/9.0;\n"
		"   float gmean = (c1.g + c2.g + c3.g + c4.g + c5.g + c6.g + c7.g + c8.g + c9.g)/9.0;\n"
		"   float bmean = (c1.b + c2.b + c3.b + c4.b + c5.b + c6.b + c7.b + c8.b + c9.b)/9.0;\n"
		"   float amean = (c1.a + c2.a + c3.a + c4.a + c5.a + c6.a + c7.a + c8.a + c9.a)/9.0;\n"

		"   gl_FragColor = vec4(rmean,gmean,bmean, amean);\n"
		"}\n"
		;
//  [3/12/2014 zhuchen] 鍒嗗壊绾�

const char * tag_curve_fragment_shader = "curve_fragment_shader";
const char * tag_linearburn_fragment_shader = "linearburn_fragment_shader";
const char * tag_multiply_fragment_shader = "multiply_fragment_shader";
const char * tag_overlay_fragment_shader = "overlay_fragment_shader";
const char * tag_screen_fragment_shader = "screen_fragment_shader";
const char * tag_darken_fragment_shader = "darken_fragment_shader";
const char * tag_coverage_fragment_shader = "coverage_fragment_shader";
const char * tag_smooth_apply_fragment_shader = "smooth_apply_fragment_shader";
const char * tag_smooth_blur_fragment_shader = "smooth_blur_fragment_shader";
const char * tag_smooth_blur_horizontal_vertex_shader = "smooth_blur_horizontal_vertex_shader";
const char * tag_smooth_blur_vertical_vertex_shader = "smooth_blur_vertical_vertex_shader";
const char * tag_smooth_extract_selection_fragment_shader = "smooth_extract_selection_fragment_shader";
const char * tag_smooth_template_fragment_shader = "smooth_template_fragment_shader";
const char * tag_vertex_shader = "vertex_shader";
const char * tag_yuv_fragment_shader = "yuv_fragment_shader";
const char * tag_saturation_fragment_shader = "saturation_fragment_shader";
const char * tag_empty_fragment_shader = "empty_fragment_shader";
const char * tag_scene_enhance_fragment_shader = "scene_enhance_fragment_shader";
const char * tag_rgb_fragment_shader = "rgb_fragment_shader";
const char * tag_vertex_shader_2 = "vertex_shader_2";
const char * tag_high_light_fragment_shader = "high_light_fragment_shader";
// [3/25/2014 yubowen] 鍒嗗壊绾�
const char* tag_kirsch_vertex_shader = "kirsch_vertex_shader";
const char* tag_kirsch_fragment_shader = "kirsch_fragment_shader";
const char* tag_compression_fragment_shader = "levels_compression_fragment_shader";
const char* tag_softlight_fragment_shader = "softlight_fragment_shader";
const char* tag_kirsch1_fragment_shader = "kirsch1_fragment_shader";
const char* tag_sobel_fragment_shader = "sobel_fragment_shader";
const char* tag_rgb2gray_fragment_shader = "rgb2gray_fragment_shader";
const char* tag_posterize_fragment_shader = "posterize_fragment_shader";
const char* tag_generate_blue_fragment_shader = "generate_blue_fragment_shader";
const char* tag_pencil_OverLay_fragment_shader = "pencil_overlay_fragment_shader";
const char* tag_hope_effect_fragment_shader = "hope_effect_fragment_shader";
const char* tag_skin_detect_fragment_shader = "skin_detect_fragment_shader";
const char* tag_skin_overlay_fragment_shader = "skin_overlay_fragment_shader";
//cheng 3.27
const char* tag_alpha_meansmooth_fragment_shader = "alpha_meansmooth_fragment_shader";
// 娣诲姞鑲よ壊妫�祴閮ㄥ垎
GLuint gl_generate_shader_program(const GLchar* v_shader_byte_array, const GLchar* f_shader_byte_array, const char * attribute_name)
{
	GLuint program = glCreateProgram();
	CHECK_GL_ERROR_DEBUG();
	GLuint u_Vert_Shader = 0;
	GLuint u_frag_shader = 0;

	if( v_shader_byte_array )
	{
		if (!compileShader(&u_Vert_Shader, GL_VERTEX_SHADER, v_shader_byte_array)) {
			SHADER_LOGE("GLShaders: ERROR: Failed to compile vertex shader");
		}

	}

	// Create and compile fragment shader
	if( f_shader_byte_array )
	{
		if (!compileShader(&u_frag_shader, GL_FRAGMENT_SHADER, f_shader_byte_array)) {
			SHADER_LOGE("GLShaders: ERROR: Failed to compile fragment shader");
		}
	}

	if( u_Vert_Shader ) {
		glAttachShader(program, u_Vert_Shader);
	}
	CHECK_GL_ERROR_DEBUG();

	if( u_frag_shader ) {
		glAttachShader(program, u_frag_shader);
	}

	glBindAttribLocation(program, 0, attribute_name);
	CHECK_GL_ERROR_DEBUG();

	glLinkProgram(program);

	GLint program_status;
	glGetProgramiv(program, GL_LINK_STATUS, &program_status);
	if( program_status == GL_FALSE )
	{
		if( u_Vert_Shader )
		{
			glDeleteShader(u_Vert_Shader);
		}
		if( u_frag_shader )
		{
			glDeleteShader(u_frag_shader);
		}
		glDeleteProgram(program);
		u_Vert_Shader = u_frag_shader = program = 0;
		SHADER_LOGE("GLShaders: ERROR: Failed to glLinkProgram");
	}

	if( u_Vert_Shader )
	{
		glDeleteShader(u_Vert_Shader);
	}
	if( u_frag_shader )
	{
		glDeleteShader(u_frag_shader);
	}

	//"uMVPMatrix","uTexture", "uTextureLayer", "uLayerWeight"
	/*GLint uMVPMatrix = glGetUniformLocation(program, "uMVPMatrix");
	GLint attr = glGetAttribLocation(program, attribute_name);
	SHADER_LOGE("GLShaders: uMVPMatrix:%d, m_uProgram:%d, attr:%d",uMVPMatrix, program, attr);*/
	return program;
}

GLuint gl_generate_shader_program_for_tag(const char* v_shader_tag, const char* f_shader_tag, const char * attribute_name)
{
	GLuint program;
	const GLchar * v_shader;
	const GLchar * f_shader;
	get_shader_for_tag(v_shader_tag, &v_shader);
	get_shader_for_tag(f_shader_tag, &f_shader);
	program = gl_generate_shader_program(v_shader, f_shader, attribute_name);
	return program;
}

void get_shader_for_tag(const char* shader_tag,const GLchar** p_shader)
{
	if( strcmp(shader_tag, tag_vertex_shader) == 0 )
	{
		*p_shader = shader_vertex;
		return;
	}else if( strcmp(shader_tag, tag_vertex_shader_2) == 0 )
	{
		*p_shader = shader_vertex_2;
		return;
	}else if( strcmp(shader_tag, tag_smooth_blur_horizontal_vertex_shader) == 0 )
	{
		*p_shader = shader_smooth_blur_horizontal_vertex;
		return;
	}else if( strcmp(shader_tag, tag_smooth_blur_vertical_vertex_shader) == 0 )
	{
		*p_shader = shader_smooth_blur_vertical_vertex;
		return;
	}else if( strcmp(shader_tag, tag_kirsch_vertex_shader) == 0)
	{
		*p_shader = shader_kirsch_vertex;
		return ;
	}

	if( strcmp(shader_tag, tag_curve_fragment_shader) == 0 )
	{
		*p_shader = shader_curve_fragment;
	}else if( strcmp(shader_tag, tag_linearburn_fragment_shader) == 0 )
	{
		*p_shader = shader_linearburn_fragment;
	}else if( strcmp(shader_tag, tag_multiply_fragment_shader) == 0 )
	{
		*p_shader = shader_multiply_fragment;
	}else if( strcmp(shader_tag, tag_overlay_fragment_shader) == 0 )
	{
		*p_shader = shader_overlay_fragment;
	}else if( strcmp(shader_tag, tag_screen_fragment_shader) == 0 )
	{
		*p_shader = shader_screen_fragment;
	}else if( strcmp(shader_tag, tag_darken_fragment_shader) == 0 )
	{
		*p_shader = shader_darken_fragment;
	}else if( strcmp(shader_tag, tag_coverage_fragment_shader) == 0 )
	{
		*p_shader = shader_coverage_fragment;
	}else if( strcmp(shader_tag, tag_smooth_apply_fragment_shader) == 0 )
	{
		*p_shader = shader_smooth_apply_fragment;
	}else if( strcmp(shader_tag, tag_smooth_blur_fragment_shader) == 0 )
	{
		*p_shader = shader_smooth_blur_fragment;
	}else if( strcmp(shader_tag, tag_smooth_extract_selection_fragment_shader) == 0 )
	{
		*p_shader = shader_smooth_extract_selection_fragment;
	}else if( strcmp(shader_tag, tag_smooth_template_fragment_shader) == 0 )
	{
		*p_shader = shader_smooth_template_fragment;
	}else if( strcmp(shader_tag, tag_yuv_fragment_shader) == 0 )
	{
		*p_shader = shader_yuv_fragment;
	}else if( strcmp(shader_tag, tag_saturation_fragment_shader) == 0 )
	{
		*p_shader = shader_saturation_fragment;
	}else if( strcmp(shader_tag, tag_empty_fragment_shader) == 0 )
	{
		*p_shader = shader_empty_fragment;
	}else if( strcmp(shader_tag, tag_scene_enhance_fragment_shader) == 0 )
	{
		*p_shader = shader_scene_enhance_fragment;
	}else if( strcmp(shader_tag, tag_rgb_fragment_shader) == 0 )
	{
		*p_shader = shader_rgb_fragment;
	}else if( strcmp(shader_tag, tag_high_light_fragment_shader) == 0 )
	{
		*p_shader = shader_high_light_fragment;
	}else if( strcmp(shader_tag, tag_kirsch_fragment_shader) == 0 )
	{
		*p_shader = shader_kirsch_fragment;
	}else if( strcmp(shader_tag, tag_compression_fragment_shader) == 0 )
	{
		*p_shader = shader_compression_fragment;
	}else if( strcmp(shader_tag, tag_softlight_fragment_shader) == 0 )
	{
		*p_shader = shader_softlight_fragment;
	}else if( strcmp(shader_tag, tag_kirsch1_fragment_shader) == 0)
	{
		*p_shader = shader_kirsch1_fragment;
	}else if( strcmp(shader_tag, tag_sobel_fragment_shader) == 0)
	{
		*p_shader = shader_sobel_fragment;
	}else if( strcmp(shader_tag, tag_rgb2gray_fragment_shader) == 0)
	{
		*p_shader  = shader_rgb2gray_fragment;
	}else if( strcmp(shader_tag, tag_posterize_fragment_shader) == 0)
	{
		*p_shader = shader_posterize_fragment;
	}else if( strcmp(shader_tag, tag_generate_blue_fragment_shader) == 0)
	{
		*p_shader  = shader_generate_blue_fragment;
	}else if( strcmp(shader_tag, tag_pencil_OverLay_fragment_shader) == 0)
	{
		*p_shader = shader_pencil_OverLay_fragment;
	}else if( strcmp(shader_tag, tag_hope_effect_fragment_shader) == 0)
	{
		*p_shader = shader_hope_effect_fragment;
	}else if( strcmp(shader_tag, tag_skin_detect_fragment_shader) == 0)
	{
		*p_shader = shader_skin_detect_fragment;
	}else if( strcmp(shader_tag, tag_skin_overlay_fragment_shader) == 0)
	{
		*p_shader = shader_skin_overlay_fragment;
	}else if( strcmp(shader_tag, tag_alpha_meansmooth_fragment_shader) == 0)
	{
		*p_shader = shader_alpha_meansmooth_fragment;
	}
}

int compileShader(GLuint * shader, GLenum type, const GLchar* source)
{
	GLint status;
	if (!source)
	return false;
	*shader = glCreateShader(type);
	glShaderSource(*shader, 1, &source, NULL);
	CHECK_GL_ERROR_DEBUG();
	glCompileShader(*shader);
	CHECK_GL_ERROR_DEBUG();

	glGetShaderiv(*shader, GL_COMPILE_STATUS, &status);
	CHECK_GL_ERROR_DEBUG();
	if( !status )
	{
		GLsizei bufSize = 4096;
		GLchar *infoLog = (GLchar *)malloc(bufSize * sizeof(GLchar));
		GLsizei logSize;
		glGetShaderInfoLog(*shader, bufSize, &logSize, infoLog);
		SHADER_LOGE("GLShaders: ERROR Source: %s", source);
		if( type == GL_VERTEX_SHADER )
			SHADER_LOGE("GLShaders: ERROR GL_VERTEX_SHADER glCompileShader: %d : %s", logSize, infoLog);
		else
			SHADER_LOGE("GLShaders: ERROR GL_FRAGMENT_SHADER glCompileShader: %d : %s", logSize, infoLog);
		*shader = 0;
		//glDeleteShader(*shader);
	}
	return ( status == GL_TRUE );
}
