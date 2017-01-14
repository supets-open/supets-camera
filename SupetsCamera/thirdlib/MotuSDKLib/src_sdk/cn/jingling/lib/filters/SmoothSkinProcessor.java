package cn.jingling.lib.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

public class SmoothSkinProcessor {
	static {
		System.loadLibrary("mtprocessor-jni");
	}

	/**
	 * 
	 * @param pixels
	 * @param w
	 * @param h
	 * @param bb
	 *            the radius of high pass filter
	 * @param flag
	 *            equals 0 or 1, which means adopt FFT method or Gauss method
	 */

	/**
	 * buffingTemplate is used to produce the model, which is the first step in
	 * procedure of faceBuffing
	 */
	public native static void buffingTemplate(int[] pixels, int w, int h,
			int bb, int flag);

	/**
	 * faceBuffing is used to modify the image used the model produced by the
	 * first step, which is the second step in procedure of faceBuffing the
	 * parameter weight is used to control the effect of facebuffing
	 */
	public native static void faceBuffing(int[] pixels, int w, int h,
			int[] R_Table, int[] G_Table, int[] B_Table);

	public native static void faceBuffingBackup(int[] pixels, int w, int h,
			int[] R_Table, int[] G_Table, int[] B_Table);

	public native static void faceBuffingWeight(int[] pixels, int w, int h,
			int[] R_Table, int[] G_Table, int[] B_Table, int weight);

	/**
	 * releaseSource is used to free the model array, which is the third step in
	 * procedure of faceBuffing
	 */
	public native static void releaseSource();

	/**
	 * InitializeCirle is the entrance of darkCircle, which is automatically do
	 * with the dark cirle
	 */
	public native static void InitializeCircle(int eyex1, int eyey1,
			int eyeradius1, int eyex2, int eyey2, int eyeradius2,
			int[] srcPixArray, int w, int h, int ratio);

	/**
	 * LightenDemo is used to modify the image when it seems too dark by
	 * modifying the curves, but now is not in use in this project
	 */
	public native static int LightenDemo(int[] srcPixArray, int w, int h,
			int sAxis, int lAxis, int centerX, int centerY);

	/**
	 * usmProcess is used to do the usm sharpen
	 */
	public native static void usmProcess(int[] srcPixArray, int w, int h,
			int radius, int thres, int amount);

	/**
	 * whiten is the entrance of automatically white effect modification
	 */
	public native static void whiten(int[] srcPixArray, int w, int h,
			int ratio, int sAxis, int lAxis, int centerX, int centerY);

	/**
	 * produceArea is the entrance of darkCircle, which is the artificially one
	 */
	public native static void produceArea(int[] srcPixArray, int[] baseflag,
			int w, int h, int[] modifyColor);

	/**
	 * BrightEyes is the entrance to make the eyes seem bright
	 */
	public native static void BrightEyes(int[] srcPixArray, int w, int h,
			int ratio, int leftX, int leftY, int rightX, int rightY);

	// public native static void backLight(int []srcPixArray,int w, int h, int
	// leftX, int leftY, int rightX, int rightY, int sAmount, int lAmount, int
	// []darkerTable,int []lighterTable);

	// public native static void DenoiseImage(int []srcPixArray, int w, int h,
	// int var, int det);

	public native static void Bilateral(int[] srcPixArray, int w, int h,
			int delta, int radius, int sigma);

	public native static void deHaze(int[] srcPixArray, int w, int h,
			int level, float Rat, float RatE, float RatL);

	public native static void sceneProcess(int[] srcPixArray, int w, int h);

	public native static void sceneEnhance(int[] srcPixArray, int w, int h,
			int para1, int para2, int[] LAB);

	public native static void whiteBalance(int[] srcPixArray, int w, int h);

	public native static void HDRsimple(int[] img, int w, int h, int block_num,
			int edg_thre);

	public native static void CLAHERGB3(int[] img, int w, int h, int block_num,
			int edg_thre);

	public native static void LocaEnhanceRGB(int[] img, int w, int h,
			int block_num, int edg_thre);

	public native static void HDR(int[] imgDark, int[] imgBright, int[] dst,
			int w, int h);

	public native static int ColorTemperature(int[] img, int[] dst, int size,
			int temperature);

	public native static void Kirsch(int[] rgb, int w, int h, int kind);// cheng

	public native static void gifProcess(int[] pixels, int w, int h, int[] p,
			int mouthCondition, int browCondition, int eyeCondition);

	public native static void MSRCR(int[] pixels, int w, int h);

	public native static void ViberationInitial(int[] srcPixArray, int w, int h);

	public native static void ViberationControl(int[] srcPixArray, int w,
			int h, float degree);

	public native static void ViberationRelease();

	public native static void setupDecolorization(int[] srcPixArray, int w,
			int h);

	public native static void decolorization(int[] srcPixArray, int size);

	public native static void beepsSetupAll(int[] pixels, int w, int h);

	public native static void BEEPSVerticalHorizontal(double[] data, int w,
			int h);

	public native static void BEEPSHorizontalVertical(double[] data, int w,
			int h);

	public native static void beepsOverlay(int[] src, int[] dst);

	public native static void beepsDetailRecover(int[] src, int[] dst);

	// night
	// public native static void NightImageInput(int []src, int []dst, int w,
	// int h, int img_total, int img_id);
	public native static void NightGenerate(int[][] srcImages, int[] dst,
			int w, int h, int img_total);

	// public native static void NightGenerateYUV(byte [][]YUV, int []dst, int
	// w, int h, int img_total);
	public native static void NightGenerateYUV(byte[][] YUV, int[] dst,
			int w_src, int h_src, int w_dst, int h_dst, int img_total);

	public native static void autoContrast(int[] img, int w, int h,
			float thre_low, float thre_high);

	public native static void shadowAndHighlight(int[] src, int w, int h,
			int lowV, int highV);

	public native static void ShadowHighLight(int[] img, int w, int h);

	public native static void ShadowHighLight1(int[] img, int w, int h);

	public native static void ShadowHighLight2(int[] img, int w, int h);
	
	public native static void skinOverLay(int [] src, int [] dst, int w, int h);

}
