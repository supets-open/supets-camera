package cn.jingling.lib.livefilter;

public class Opengl20JniLib {

	static{
		System.loadLibrary("opengljni");
	}
	
	/** @deprecated
	 * @param tag
	 * @return
	 */
	public static String getShader(String tag){
		if(tag.equals("curve_fragment_shader")){
			return getCurveFragmentShader();
		}else if(tag.equals("linearburn_fragment_shader")){
			return getLinearburnFragmentShader();
		}else if(tag.equals("multiply_fragment_shader")){
			return getMultiplyFragmentShader();
		}else if(tag.equals("overlay_fragment_shader")){
			return getOverlayFragmentShader();
		}else if(tag.equals("screen_fragment_shader")){
			return getScreenFragmentShader();
		}else if(tag.equals("darken_fragment_shader")){
			return getDarkenFragmentShader();
		}else if(tag.equals("coverage_fragment_shader")){
		    return getCoverageFragmentShader();
		}else if(tag.equals("smooth_apply_fragment_shader")){
			return getSmoothApplyFragmentShader();
		}else if(tag.equals("smooth_blur_fragment_shader")){
			return getSmoothBlurFragmentShader();
		}else if(tag.equals("smooth_blur_horizontal_vertex_shader")){
			return getSmoothBlurHorizontalVertexShader();
		}else if(tag.equals("smooth_blur_vertical_vertex_shader")){
			return getSmoothBlurVerticalVertexShader();
		}else if(tag.equals("smooth_extract_selection_fragment_shader")){
			return getSmoothExtractSelectionFragmentShader();
		}else if(tag.equals("smooth_template_fragment_shader")){
			return getSmoothTemplateFragmentShader();
		}else if(tag.equals("vertex_shader")){
			return getVertexShader();
		}else if(tag.equals("yuv_fragment_shader")){
			return getYuvFragmentShader();
		}else if(tag.equals("saturation_fragment_shader")) {
			return getSaturationFragmentShader();
		}else if(tag.equals("empty_fragment_shader")) {
			return getEmptyFragmentShader();
		}else if(tag.equals("scene_enhance_fragment_shader")) {
			return getSceneEnhanceFragmentShader();
		}else if(tag.equals("rgb_fragment_shader")){
			return getRgbFragmentShader();
		}else if(tag.equals("vertex_shader_2")){//mtk的构架流程 用到的定点着色器
			return getVertexShader();
		}else if(tag.equals("high_light_fragment_shader")){//暗部提亮，夜拍会使用
			return getHighLightFragmentShader();
		}else if(tag.equals("kirsch_vertex_shader")){
			return getKirschVertexShader();
		}else if(tag.equals("kirsch_fragment_shader")){
			return getKirschFragmentShader();
		}else if(tag.equals("levels_compression_fragment_shader")){
			return getLevelsCompressionShader();
		}else if(tag.equals("softlight_fragment_shader")){
			return getSoftlightFragmentShader();
		}else if(tag.equals("sobel_fragment_shader")){
			return getSobelFragmentShader();
		}else if(tag.equals("rgb2gray_fragment_shader")){
			return getRgb2grayFragmentShader();
		}else if(tag.equals("posterize_fragment_shader")){
			return getPosterizeFragmentShader();
		}else if(tag.equals("generate_blue_fragment_shader")){
			return getGenerateBlueFragmentShader();
		}else if(tag.equals("pencil_overlay_fragment_shader")){
			return getPencilOverLayFragmentShader();
		}else if(tag.equals("hope_effect_fragment_shader")){
			return getHopeEffectFragmentShader();
		}else if(tag.equals("kirsch1_fragment_shader")){
			return getKirsch1FragmentShader();
		}
		return "";
	}
	private static native String getEmptyFragmentShader();
	private static native String getSaturationFragmentShader();
	private static native String getCurveFragmentShader();
	private static native String getLinearburnFragmentShader();
	private static native String getMultiplyFragmentShader();
	private static native String getDarkenFragmentShader();
	private static native String getCoverageFragmentShader();
	private static native String getOverlayFragmentShader();
	private static native String getScreenFragmentShader();
	private static native String getSmoothApplyFragmentShader();
	private static native String getSmoothBlurFragmentShader();
	private static native String getSmoothBlurHorizontalVertexShader();
	private static native String getSmoothBlurVerticalVertexShader();
	private static native String getSmoothExtractSelectionFragmentShader();
	private static native String getSmoothTemplateFragmentShader();
	private static native String getVertexShader();
	private static native String getYuvFragmentShader();
	private static native String getSceneEnhanceFragmentShader();
	private static native String getRgbFragmentShader();
	private static native String getVertexShader2();
	private static native String getHighLightFragmentShader();
	private static native String getKirschVertexShader();
	private static native String getKirschFragmentShader();
	private static native String getLevelsCompressionShader();
	private static native String getSoftlightFragmentShader();
	private static native String getSobelFragmentShader();
	private static native String getRgb2grayFragmentShader();
	private static native String getPosterizeFragmentShader();
	private static native String getGenerateBlueFragmentShader();
	private static native String getPencilOverLayFragmentShader();
	private static native String getHopeEffectFragmentShader();
	private static native String getKirsch1FragmentShader();
	
	//public static native String testInitShader();
	
	public static native int getShaderProgram(String vertexShaderTag, String fragmentShaderTag, String attrib);
	
}
