#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "opengljni.h"
#include "GLShaders.h"

#define LOG_TAG "OPENGLES_SHADER"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


	JNIEXPORT jint JNICALL Java_cn_jingling_lib_livefilter_Opengl20JniLib_getShaderProgram(JNIEnv *env, jclass obj, jstring vertexShaderTag, jstring fragmentShaderTag, jstring attrib)
	{
		const char* vertex_shader_tag;
		const char* fragment_shader_tag;
		const char* attribute_name;
		vertex_shader_tag = env->GetStringUTFChars(vertexShaderTag, NULL);
		fragment_shader_tag = env->GetStringUTFChars(fragmentShaderTag, NULL);
		attribute_name = env->GetStringUTFChars(attrib, NULL);

		//LOGE("vertex_shader_tag = %s, fragment_shader_tag = %s, attribute_name = %s", vertex_shader_tag, fragment_shader_tag, attribute_name);
		GLuint program = gl_generate_shader_program_for_tag(vertex_shader_tag, fragment_shader_tag, attribute_name);

		env->ReleaseStringUTFChars(vertexShaderTag, vertex_shader_tag);
		env->ReleaseStringUTFChars(fragmentShaderTag, fragment_shader_tag);
		env->ReleaseStringUTFChars(attrib, attribute_name);
		return program;
	}

/*	JNIEXPORT jstring JNICALL Java_cn_jingling_lib_livefilter_Opengl20JniLib_testInitShader(JNIEnv *env,jclass jclazz)
	{
		const GLchar vertex_shader[] = {
			"attribute vec4 aPosition;\n"
			"uniform mat4 uMVPMatrix;\n"
			"varying vec4 vPosition;\n"

			"void main() {\n"
			"  gl_Position = uMVPMatrix * aPosition;\n"
			"  vPosition = aPosition;\n"

			"}\n"
		};

		const GLchar yuv_fragment_shader[] = {
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
			"}\n"
		};

		initShader(vertex_shader, yuv_fragment_shader);
		LOGE("opengljni: initShader()");
		jstring rtstr = env->NewStringUTF("Hello");
		return rtstr;
	}


	int initShader(const GLchar* vShaderByteArray, const GLchar* fShaderByteArray)
	{
		GLuint m_uProgram = glCreateProgram();
		CHECK_GL_ERROR_DEBUG();
		GLuint m_uVertShader = 0;
		GLuint m_uFragShader = 0;

		if( vShaderByteArray )
		{
			if (!compileShader(&m_uVertShader, GL_VERTEX_SHADER, vShaderByteArray)) {
				LOGE("opengljni: ERROR: Failed to compile vertex shader");
			}

		}

		// Create and compile fragment shader
		if( fShaderByteArray )
		{
			if (!compileShader(&m_uFragShader, GL_FRAGMENT_SHADER, fShaderByteArray)) {
				LOGE("cocos2d: ERROR: Failed to compile fragment shader");
			}
		}

		if( m_uVertShader ) {
			glAttachShader(m_uProgram, m_uVertShader);
		}
		CHECK_GL_ERROR_DEBUG();

	    if( m_uFragShader ) {
	        glAttachShader(m_uProgram, m_uFragShader);
	    }

	    glBindAttribLocation(m_uProgram, 0, "aPosition");
	    CHECK_GL_ERROR_DEBUG();

	    glLinkProgram(m_uProgram);

	    //"uMVPMatrix","uTexture", "uTextureLayer", "uLayerWeight"
	    GLint uMVPMatrix = glGetUniformLocation(m_uProgram, "uMVPMatrix");
	    LOGE("cocos2d: uMVPMatrix:%d, m_uProgram:%d",uMVPMatrix, m_uProgram);
	    return 1;
	}

	bool compileShader(GLuint * shader, GLenum type, const GLchar* source)
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
			if( type == GL_VERTEX_SHADER )
				LOGE("opengljni: ERROR GL_VERTEX_SHADER glCompileShader");
			else
				LOGE("opengljni: ERROR glCompileShader");
			glDeleteShader(*shader);
		}
		return ( status == GL_TRUE );
	}*/



