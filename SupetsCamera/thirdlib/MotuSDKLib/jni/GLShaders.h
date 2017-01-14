#pragma once
#ifndef __GLSHADER_H__
#define __GLSHADER_H__

#include <GLES2/gl2.h>
#include <android/log.h>

#define GLSHADERS_LOG_TAG "GLShaders"

#define SHADER_LOGE(...) __android_log_print(ANDROID_LOG_ERROR,GLSHADERS_LOG_TAG,__VA_ARGS__)

#define CHECK_GL_ERROR_DEBUG() \
    do { \
        GLenum __error = glGetError(); \
        if(__error) { \
            SHADER_LOGE("OpenGL error %d in %s %s %d\n", __error, __FILE__, __FUNCTION__, __LINE__); \
        } \
    } while (false)

// 定义全部的shader脚本
extern const char * shader_curve_fragment;
extern const char * shader_linearburn_fragment;
extern const char * shader_multiply_fragment;
extern const char * shader_overlay_fragment;
extern const char * shader_screen_fragment;
extern const char * shader_darken_fragment;
extern const char * shader_coverage_fragment;
extern const char * shader_smooth_apply_fragment;
extern const char * shader_smooth_blur_fragment;
extern const char * shader_smooth_blur_horizontal_vertex;
extern const char * shader_smooth_blur_vertical_vertex;
extern const char * shader_smooth_extract_selection_fragment;
extern const char * shader_smooth_template_fragment;
extern const char * shader_vertex;
extern const char * shader_yuv_fragment;
extern const char * shader_saturation_fragment;
extern const char * shader_empty_fragment;
extern const char * shader_scene_enhance_fragment;
extern const char * shader_rgb_fragment;
extern const char * shader_vertex_2;
extern const char * shader_high_light_fragment;

//和java层定义的tag保存一致
extern const char * tag_curve_fragment_shader;
extern const char * tag_linearburn_fragment_shader;
extern const char * tag_multiply_fragment_shader;
extern const char * tag_overlay_fragment_shader;
extern const char * tag_screen_fragment_shader;
extern const char * tag_darken_fragment_shader;
extern const char * tag_coverage_fragment_shader;
extern const char * tag_smooth_apply_fragment_shader;
extern const char * tag_smooth_blur_fragment_shader;
extern const char * tag_smooth_blur_horizontal_vertex_shader;
extern const char * tag_smooth_blur_vertical_vertex_shader;
extern const char * tag_smooth_extract_selection_fragment_shader;
extern const char * tag_smooth_template_fragment_shader;
extern const char * tag_vertex_shader;
extern const char * tag_yuv_fragment_shader;
extern const char * tag_saturation_fragment_shader;
extern const char * tag_empty_fragment_shader;
extern const char * tag_scene_enhance_fragment_shader;
extern const char * tag_rgb_fragment_shader;
extern const char * tag_vertex_shader_2;
extern const char * tag_high_light_fragment_shader;

extern const char* tag_kirsch_vertex_shader ;
extern const char* tag_kirsch_fragment_shader ;
extern const char* tag_compression_fragment_shader ;
extern const char* tag_softlight_fragment_shader  ;
extern const char* tag_kirsch1_fragment_shader  ;
extern const char* tag_sobel_fragment_shader ;
extern const char* tag_rgb2gray_fragment_shader ;
extern const char* tag_posterize_fragment_shader ;
extern const char* tag_generate_blue_fragment_shader ;
extern const char* tag_pencil_OverLay_fragment_shader ;
extern const char* tag_hope_effect_fragment_shader ;
extern const char* tag_skin_detect_fragment_shader ;
extern const char* tag_skin_overlay_fragment_shader;

//struct ShaderInfo {
//	int program;
//	int attribute;
//
//}

GLuint gl_generate_shader_program(const GLchar* v_shader_byte_array, const GLchar* f_shader_byte_array, const char * attribute_name);

GLuint gl_generate_shader_program_for_tag(const char* v_shader_tag, const char* f_shader_tag, const char * attribute_name);

void get_shader_for_tag(const char* shader_tag, const GLchar** p_shader);

int compileShader(GLuint * shader, GLenum type, const GLchar* source);

#endif
