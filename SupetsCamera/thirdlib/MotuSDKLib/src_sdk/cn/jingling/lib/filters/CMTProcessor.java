package cn.jingling.lib.filters;

public class CMTProcessor {
	static {
		System.loadLibrary("mtprocessor-jni");
	}
 
	public native static int lipstick(int [] Pixels, int [] color, int w, int h, int [] p);//cheng	
	public native static void Posterize(int [] img, int w, int h, int levels);//cheng
	public native static void WaterColor(int [] src, int [] model, int w, int h);//cheng
	public native static void Pencil(int []src, int [] model1, int [] model2, int w, int h);//cheng
	public native static void ColorPencil(int [] src, int [] model1, int [] model2, int w, int h);//cheng
	public native static void BlueEdg(int [] src, int [] model1, int [] model2, int w, int h);//cheng
	public native static void OilPainting(int [] src, int [] model1, int [] model2, int w, int h);//cheng
	public native static void setHopeEffectTexturePixels(int[] texPixels, int w, int h);//cheng
	public native static void HopeEffect(int[] texPixels, int w, int h);//cheng
	public native static void HalfTone(int []pixels, int w, int h, int []tPixels, int tw, int th, int flag);//Yu
	
	
	public native static void alphaCompositeEffect(int[] pixels,
			int[] layerPixels, int w, int h, int alpha);

	public native static void autoColor(int[] srcPixArray, int w, int h, int clipShadow, int clipHighlight);

	public native static void autoContrast(int[] srcPixArray, int w, int h, int clipShadow, int clipHighlight);
	
	public native static void autoLevels(int[] pixels, int w, int h);

	public native static void blueEffect(int[] pixels, int[] r, int[] g,
			int[] b, int w, int h);

	public native static void blur(int[] pixels, int w, int h, int x, int y,
			int r);

	public native static void blurBackgroundEffectByCircle(int[] pixels, int w,
			int h, int x, int y, int r0, int r1);

	public native static void blurBackgroundEffectByLine(int[] pixels, int w,
			int h, int x, int y, int r0, int r1, float theta);

	public native static void brightEffect(int[] pixels, int w, int h,
			int degree);

	public native static void colorBurn(int[] srcPixArray, int[] layerPixArray,
			int w, int h);

	public native static void colorLevel(int[] srcPixArray, int w, int h,
			int min, float gray, int max, int outMin, int outMax);

	public native static ExpVar computeHueExpectationAndVariance(int[] pixels, int w, int h);

	public native static void contrastEffect(int[] pixels, int w, int h,
			int degree);

	public native static void coverEffect(int[] pixels, int[] layerPixels,
			int w, int h);

	public native static void curveEffect(int[] pixels, int[] r, int[] g,
			int[] b, int w, int h);

	public native static void darken(int[] srcPixArray, int[] layerPixArray,
			int w, int h);

	public native static void dlomo(int[] pixels, int w, int h, int x, int y,
			int bound);

	public native static void dreamy(int[] pixels, int w, int h, int i);
	public native static void emissionEffect(int[] srcPixArray, int w, int h);
	public native static void equlizeHist(int[] srcPixArray, int w, int h);
	public native static void etocEffect(int[] srcPixArray, int w, int h);

	public native static void eyeBrighten(int[] srcPixArray, int w, int bb);

	public native static void eyeEnlarge(int[] pixels, int w, int h, int x,
			int y, int r, float scale);

	public native static void eyeEnlargeWithTags(int[] in, int[] out, int w, int h, int[] x,
			int[] y, int[] r, float[] scale, int num);

	public native static void fastAverageBlur(int[] srcPixArray, int width,
			int height, int radius);

	public native static void fastAverageBlurWithThreshold(int[] srcPixArray,
			int w, int h, int radius, int threshold);

	/**
	 * 
	 * @param srcPixArray
	 * @param w
	 * @param h
	 * @param radius
	 * @param threshold
	 * @param weight 0 ~ 256
	 */
	public native static void fastAverageBlurWithThresholdAndWeight(int[] srcPixArray,
			int w, int h, int radius, int threshold, int weight);

	public native static void fastAverageBlurWithThresholdWeightSkinDetection(int[] srcPixArray,
			int w, int h, int radius, int threshold, int weight, 
			int hmin, int hmax, int smin, int smax, int vmin, int vmax);

	public native static void gaussBlur(int[] srcPixArray, int width,
			int height, int radius, float sigma);
	
	public native static void getDynamicFrame(int[] frame, int[] oriFrame,
			int w, int h, int oriW, int oriH);

	public native static void gray(int[] pixels, int w, int h);

	public native static void lightenEffect(int[] pixels,
			int[] layerPixels, int w, int h);

	/**
	 * 
	 * @param srcPixArray
	 * @param layerPixArray
	 * @param w
	 * @param h
	 * @param layeralpha from 0 to 100
	 */
	public native static void linearBurn(int[] srcPixArray,
			int[] layerPixArray, int w, int h, int layeralpha);
	/**
	 * 
	 * @param pixels 原图待处理像素。并非原图全部像素。
	 * @param layerPixels layer待处理像素。并非layer全部像素。
	 * @param w 原图宽度
	 * @param h pixels对应的原图高度。如果是严格逐行处理，h=1.
	 * @param pw layer的宽度
	 * @param ph layerPixels对应的layer高度。如果是严格逐行处理，ph=1.
	 * @param layeralpha from 0 to 100
	 */
	public native static void rsLinearBurn(int[] pixels, int[] layerPixels,
			int w, int h, int pw, int ph, int layeralpha);

	public native static void linearDodgeEffect(int[] pixels,
			int[] layerPixels, int w, int h);

	public native static void llomo(int[] pixels, int w, int h, int x, int y,
			int bound);

	public native static void lomo(int[] pixels, int w, int h, int color, int brightRatio, int darkRatio, int distScope);

	public native static void mergeSelection(int[] pixels, int[] layerPixels,
			int[] sel, int w, int h);

	/**
	 * 
	 * @param pixels
	 * @param layerPixels
	 * @param w
	 * @param h
	 * @param weight from 0 to 255, means the original pixels weight
	 */
	public native static void mergeWeight(int[] pixels, int[] layerPixels,
			int w, int h, int weight);
	
	/** 实现逐行贴图功能。将一张图贴到原图上。
	 * @param pixels 原图待处理像素。并非原图全部像素。
	 * @param layerPixels layer待处理像素。并非layer全部像素。
	 * @param w 原图宽度
	 * @param h pixels对应的原图高度。如果是严格逐行处理，h=1.
	 * @param pw layer的宽度
	 * @param ph layerPixels对应的layer高度。如果是严格逐行处理，ph=1.
	 */
	public native static void rsCoverageEffect(int[] pixels, int[] layerPixels,
			int w, int h, int pw, int ph);
	
	/**
	 * 
	 * @param pixels 原图待处理像素。并非原图全部像素。
	 * @param layerPixels layer待处理像素。并非layer全部像素。
	 * @param w 原图宽度
	 * @param h pixels对应的原图高度。如果是严格逐行处理，h=1.
	 * @param pw layer的宽度
	 * @param ph layerPixels对应的layer高度。如果是严格逐行处理，ph=1.
	 */
	public native static void rsMultiplyAlphaEffect(int[] pixels, int[] layerPixels,
			int w, int h, int alpha);
	
	public native static void multiplyAlphaEffect(int[] pixels, int[] layerPixels,
			int w, int h, int pw, int ph, int alpha);

	public native static void multiplyEffect(int[] pixels, int[] layerPixels,
			int w, int h);

	public native static void normalization(int[] srcPixArray, int w, int h, int clipShadow, int clipHighlight);

	/**
	 * 
	 * @param pixels
	 * @param layerPixels
	 * @param w
	 * @param h
	 * @param alpha
	 *            from 0 to 100, 100 will be original pixels
	 */
	public native static void overlayAlphaEffect(int[] pixels,
			int[] layerPixels, int w, int h, int alpha);

	public native static void overlayEffect(int[] pixels, int[] layerPixels,
			int w, int h);
	
	/**
	 * 
	 * @param pixels 原图待处理像素。并非原图全部像素。
	 * @param layerPixels layer待处理像素。并非layer全部像素。
	 * @param w 原图宽度
	 * @param h pixels对应的原图高度。如果是严格逐行处理，h=1.
	 * @param pw layer的宽度
	 * @param ph layerPixels对应的layer高度。如果是严格逐行处理，ph=1.
	 */
	public native static void rsOverlayEffect(int[] pixels, int[] layerPixels,
			int w, int h, int pw, int ph);
	
	/**
	 * @param pixels
	 * @param layerPixels
	 * @param w
	 * @param h
	 * @param pw
	 * @param ph
	 * @param alpha from 0 ~ 100
	 */
	public native static void rsOverlayAlphaEffect(int[] pixels, int[] layerPixels,
			int w, int h, int pw, int ph, int alpha);

	public native static void popstyle(int[] srcPixArray, int w, int h, int type);

	public native static void postivefilterEffect(int[] srcPixArray, int w,
			int h);

	public native static void redeyeEffect(int[] pixels, int w, int h, int x,
			int y, int r);

	public native static void relief(int[] srcPixArray, int width, int height,
			int increment);

	/**
	 * 
	 * @param pixels 原图待处理像素。并非原图全部像素。
	 * @param layerPixels layer待处理像素。并非layer全部像素。
	 * @param w 原图宽度
	 * @param h pixels对应的原图高度。如果是严格逐行处理，h=1.
	 * @param pw layer的宽度
	 * @param ph layerPixels对应的layer高度。如果是严格逐行处理，ph=1.
	 */
	public native static void rsMultiplyEffect(int[] pixels, int[] layerPixels,
			int w, int h, int pw, int ph);

	public native static void screenEffect(int[] pixels, int[] layerPixels,
			int w, int h);
	
	public native static void rsScreenEffect(int[] pixels, int[] layerPixels,
			int w, int h, int lw, int lh);

	public native static void screenWithLimitedLayer(int[] srcPixArray,
			int[] layerPixArray, int w, int h, int lw, int lh);

	public native static void setVisibleArea(int[] pixels, int w, int h,
			int[] vertexs, int cnt);

	/**
	 * 
	 * @param pixels
	 * @param w
	 * @param h
	 * @param r 1 without change
	 */
	public native static void sharpenEffect(int[] pixels, int w, int h, int r);

	public native static void singleColorEffect(int[] pixels, int w, int h,
			float[] matrix, float targetRed, float targetGreen,
			float targetBlue, float threshold, float maxup);

	public native static void sketchEffect(int[] pixels, int w, int h);

	public native static void skinSmoothPointEffect(int[] pixels, int w, int h,
			int x, int y, int r);

	public native static void skinWhitePointEffect(int[] oriPixels,
			int[] pixels, int w, int h, int x, int y, int r);

	public native static void skinWhiteTeethPointEffect(int[] oriPixels,
			int[] pixels, int w, int h, int x, int y, int r);

	public native static void smileWholeMouth(int[] pixels, int w, int h, int x,
			int y, int x2, int y2, float scale);

	public native static void smoothEffect(int[] pixels, int w, int h);

	public native static void softlightEffect(int[] pixels, int[] layerPixels,
			int w, int h);

	public native static void thinEffect(int[] pixels, int w, int h, int x,
			int y, int x2, int y2, int r, float scale, int flag);
	
	public native static void thinEffectAuto(int[] pixels, int w, int h, int x,
			int y, int x2, int y2, int degree);
	
	public native static void thinEffectWholeFace(int[] pixels, int w, int h, int x,
			int y, int x2, int y2, float scale);

	public native static void transToGray(int[] in, byte[] out, int width, int height);
	
	public native static void transToReversedBGR(int[] in, byte[] out, int width, int height);

	public native static void unsharpEffect(int[] pixels, int[] smoothImg,
			int w, int h, int rad, int thresh, float f);

	public native static void yuv420sp2rgb(byte[] rgb, byte[] yuv, int w, int h);
	
	public native static void yuv2rgbResize(byte[] in, int inWidth, int inHeight,
			byte[] out, int outWidth, int outHeight, int direction);

	public native static void yuv2rgbBitmap(byte[] in, int w, int h, int[] pixels);
	/**
	 * @param r 滤波半径
	 * @param w 原图宽度
	 * @param times 滤波次数
	 */
	public native static void progressiveLineInitialize(int r, int w, int h, int times, int channel);
	
	/** 调用h+r*times次。返回int[]有可能是null或一行数据。输入的int[]最后r*times次为null。
	 * @param pixels 输入的下一行pixels
	 */
	public native static int[] progressiveLineProcess(int[] scrPixels, int w);
	
	public native static void progressiveRelease();
	
	public native static int[] usmProcessProgressive(int []pixels, int w, int radius, int thres, int amount); 

	
}