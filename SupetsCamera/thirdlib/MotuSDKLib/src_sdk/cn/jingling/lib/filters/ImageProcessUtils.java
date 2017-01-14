package cn.jingling.lib.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class ImageProcessUtils {

	public enum Type {
		STRENCH, RIGHT_BOTTOM, LEFT_BOTTOM, LEFT_TOP, BOTTOM
	};

	/**
	 * 
	 * @param bm1
	 *            Layer 1
	 * @param bm2
	 *            Layer 2
	 * @param ratio
	 *            Ratio of Layer 1 from 0 to 1
	 * @return Result Bitmap
	 */
	public static Bitmap mergeBitmap(Bitmap bm1, Bitmap bm2, double ratio) {
		Bitmap rst = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(),
				bm1.getConfig());
		Canvas c = new Canvas(rst);
		c.drawBitmap(bm1, 0, 0, new Paint());
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setAlpha((int) ((1 - ratio) * 255));
		c.drawBitmap(bm2, 0, 0, paint);
		return rst;
	}

	public static Bitmap mergeBitmap(Bitmap bm1, Bitmap bm2) {
		Bitmap rst = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(),
				bm1.getConfig());
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Canvas c = new Canvas(rst);
		c.drawBitmap(bm1, 0, 0, paint);
		c.drawBitmap(bm2, 0, 0, paint);
		return rst;
	}

	/**
	 * 
	 * @param bm1
	 *            Layer 1
	 * @param bm2
	 *            Layer 2
	 * @param ratio
	 *            Ratio of Layer 1 from 0 to 1
	 * @return Result Bitmap
	 */
	public static void mergeBitmapInPlace(Bitmap bm1, Bitmap bm2, double ratio) {
		mergeBitmapInPlace(bm1, bm2, ratio, Type.STRENCH, 0, 0);
	}

	/**
	 * 
	 * @param bm1
	 *            Layer 1
	 * @param bm2
	 *            Layer 2
	 * @param ratio
	 *            Ratio of Layer 1 from 0 to 1
	 * @return Result Bitmap
	 */
	public static void mergeBitmapInPlace(Bitmap bm1, Bitmap bm2, double ratio,
			Type type) {
		mergeBitmapInPlace(bm1, bm2, ratio, type, 0, 0);
	}
		/**
	 * 
	 * @param bm1
	 *            Layer 1
	 * @param bm2
	 *            Layer 2
	 * @param ratio
	 *            Ratio of Layer 1 from 0 to 1
	 * @return Result Bitmap
	 */
	public static void mergeBitmapInPlace(Bitmap bm1, Bitmap bm2, double ratio,
			Type type, double marginFractionH, double marginFractionV) {
		Canvas c = new Canvas(bm1);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		paint.setSubpixelText(true);
		paint.setAlpha((int) ((1 - ratio) * 255));
		int w1 = bm1.getWidth();
		int h1 = bm1.getHeight();
		int w2 = bm2.getWidth();
		int h2 = bm2.getHeight();
		int marginH = (int)(marginFractionH * w1);
		int marginV = (int)(marginFractionV * h1);
		Rect rect = new Rect(0, 0, w1, h1);
		if (type == Type.LEFT_BOTTOM) {
			rect = new Rect(0 + marginH, h1 - h2 - marginV, w2 + marginH, h1 - marginV);
		} else if (type == Type.LEFT_TOP) {
			rect = new Rect(0 + marginH, 0 + marginV, w2 + marginH, h2 + marginV);
		} else if (type == Type.RIGHT_BOTTOM) {
			rect = new Rect(w1 - w2 - marginH, h1 - h2 - marginV, w1 - marginH, h1 - marginV);
		} else if (type == Type.BOTTOM) {
			rect = new Rect((w1 - w2) / 2, h1 - h2 - marginV, (w1 + w2) / 2, h1 - marginV);
		}
		c.drawBitmap(bm2, null, rect, paint);
	}

	/**
	 * draw the layer in selection on bm,
	 * 
	 * @param bm
	 * @param layer
	 * @param sel
	 */
	public static void mergeBitmap(Bitmap bm, Bitmap layer, ImageSelection sel) {
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		int[] layerPixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		layer.getPixels(layerPixels, 0, w, 0, 0, w, h);
		CMTProcessor.mergeSelection(pixels, layerPixels, sel.getPixels(), w, h);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
	}

	public static Bitmap nativeMerge(Bitmap bm, Bitmap layer, double ratio) {
		Bitmap rst = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				bm.getConfig());
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		int[] layerPixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		layer.getPixels(layerPixels, 0, w, 0, 0, w, h);
		CMTProcessor.mergeWeight(pixels, layerPixels, w, h, (int)(ratio * 255));
		rst.setPixels(pixels, 0, w, 0, 0, w, h);
		return rst;
	}

	/**
	 * 
	 * @param degree
	 *            From -100 to 100, it is the same effect as the saturation of
	 *            photoshop
	 */
	public static int[] saturationPs(int[] pixels, int w, int h, int degree) {

		Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		saturationPs(bm, degree);
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		bm.recycle();
		bm = null;
		return pixels;
	}

	/**
	 * 
	 * @param bm
	 * @param degree
	 *            From -100 to 100, it is the same effect as the saturation of
	 *            photoshop
	 */
	public static void saturationPs(Bitmap bm, int degree) {
		int d = (degree + 100) / 2;
		saturation(bm, d);
	}

	/**
	 * 
	 * @param bm
	 * @param degree
	 *            From 0 to 100, degree 50 is the original bitmap
	 */
	public static void saturation(Bitmap bm, int degree) { // 0 to 100
		saturation(bm, (float) (degree / 50.0), (float) (degree / 50.0),
				(float) (degree / 50.0));
	}

	public static void saturation(Bitmap bm, float red, float green, float blue) {
		applyColorMatrix(bm, getSaturationMatrix(red, green, blue));
	}

	public static void saturationAndBrightnessPs(Bitmap bm, int sat, int bright) {
		int d = (sat + 100) / 2;
		ColorMatrix mat = getSaturationMatrix(d / 50f, d / 50f, d / 50f);
		float[] m = mat.getArray();
		m[4] = bright;
		m[9] = bright;
		m[14] = bright;
		applyColorMatrix(bm, mat);
	}

	public static void redFace(Bitmap bm) {
		applyColorMatrix(bm, getRedFaceMatrix());
	}

	public static int[] colorZoom(int[] pixels, int w, int h, float ratio) {
		Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		colorZoom(bm, ratio);
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		return pixels;
	}

	public static void colorZoom(Bitmap bm, float ratio) {
		ColorMatrix matrix = new ColorMatrix();
		matrix.reset();
		matrix.setScale(ratio, ratio, ratio, 1);
		applyColorMatrix(bm, matrix);
	}

	private static void applyColorMatrix(Bitmap bm, ColorMatrix matrix) {
		Canvas canvas = new Canvas();
		canvas.setBitmap(bm);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		ColorMatrixColorFilter cFilter = new ColorMatrixColorFilter(matrix);
		paint.setColorFilter(cFilter);
		canvas.drawBitmap(bm, 0, 0, paint);
		canvas.save();
	}

	private static ColorMatrix getSaturationMatrix(float red, float green,
			float blue) {
		ColorMatrix matrix = new ColorMatrix();
		matrix.reset();
		float[] m = matrix.getArray();

		final float R = 0.213f * (1 - red);
		final float G = 0.715f * (1 - green);
		final float B = 0.072f * (1 - blue);

		m[0] = R + red;
		m[1] = G;
		m[2] = B;
		m[5] = R;
		m[6] = G + green;
		m[7] = B;
		m[10] = R;
		m[11] = G;
		m[12] = B + blue;
		return matrix;
	}

	private static ColorMatrix getRedFaceMatrix() {
		ColorMatrix matrix = new ColorMatrix();
		matrix.reset();
		float[] m = matrix.getArray();
		m[2] = (float) 0.5;
		return matrix;
	}

	public static void hue(Bitmap bm, int degreeHue) {

		float degree = (float) ((degreeHue - 180.0) / 180.0 * Math.PI);

		Canvas myCanvas = new Canvas();
		Paint myPaint = new Paint();
		myPaint.setAntiAlias(true);
		ColorMatrix myColorMatrix = new ColorMatrix();

		setHueMatrix(myColorMatrix, degree);
		ColorMatrixColorFilter myColorMatrixColorFilter = new ColorMatrixColorFilter(
				myColorMatrix);
		myPaint.setColorFilter(myColorMatrixColorFilter);
		myCanvas.setBitmap(bm);

		myCanvas.drawBitmap(bm, 0, 0, myPaint);
		myCanvas.save();

	}

	// Set the Hue Matrix
	private static void setHueMatrix(ColorMatrix cm, float degree) {
		float hueNum = (float) Math.max(-Math.PI, Math.min(degree, Math.PI));
		float cosNum = (float) Math.cos(hueNum);
		float sinNum = (float) Math.sin(hueNum);
		float lumR = 0.213f;
		float lumG = 0.715f;
		float lumB = 0.072f;

		float[] hueMatr = { lumR + cosNum * (1 - lumR) + sinNum * (-lumR),
				lumG + cosNum * (-lumG) + sinNum * (-lumG),
				lumB + cosNum * (-lumB) + sinNum * (1 - lumB), 0, 0,
				lumR + cosNum * (-lumR) + sinNum * (0.143f),
				lumG + cosNum * (1 - lumG) + sinNum * (0.140f),
				lumB + cosNum * (-lumB) + sinNum * (-0.283f), 0, 0,
				lumR + cosNum * (-lumR) + sinNum * (-(1 - lumR)),
				lumG + cosNum * (-lumG) + sinNum * (lumG),
				lumB + cosNum * (1 - lumB) + sinNum * (lumB), 0, 0, 0, 0, 0, 1,
				0, 0, 0, 0, 0, 1 };
		cm.set(hueMatr);
	}

	/**
	 * 
	 * @param bm
	 * @param angle
	 *            rotation angle in clockwise
	 * @return
	 */
	public static Bitmap rotate(Bitmap bm, int angle) {
		Matrix m = new Matrix();
		m.setRotate(angle);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m,
				true);
	}

	public static Bitmap flip(Bitmap bm, boolean horizontal) {
		Matrix m = new Matrix();
		if (horizontal) {
			m.setScale(-1, 1);
		} else {
			m.setScale(1, -1);
		}
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m,
				true);
	}

	public static Bitmap createAlphaBitmap(Bitmap bm, int alpha) {
		Paint p = new Paint();
		p.setAlpha(alpha);
		p.setDither(true);
		Bitmap b = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		c.drawBitmap(bm, 0, 0, p);
		bm.recycle();
		return b;
	}

	public static byte[] getGrayImage(Bitmap bm) {
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		byte[] out = new byte[w * h];
		CMTProcessor.transToGray(pixels, out, w, h);
		return out;
	}
	
	public static void effectSmoothSkin(Context cx, Bitmap bm, int degree) {
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.buffingTemplate(pixels, w, h, 10, 1);
		Curve curve = new Curve(cx, "curves/skin_smooth.dat");
		SmoothSkinProcessor.faceBuffingWeight(pixels, w, h, curve.getCurveRed(), curve.getCurveGreen(), curve.getCurveBlue(), degree);
		SmoothSkinProcessor.releaseSource();
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
	}
	
	public static void skinSmooth(Context context, int[] pixels, int w, int h,
			int level) {
		Bitmap bmp = null;
		try {
			int[][] degree = { { 0, 0 }, { 50, 13 }, { 60, 25 }, { 80, 33 },
					{ 90, 55 }, { 100, 70 } };
			SmoothSkinProcessor.buffingTemplate(pixels, w, h, 10, 1);
			Curve curve = new Curve(context, "curves/skin_smooth.dat");
			SmoothSkinProcessor.faceBuffingWeight(pixels, w, h,
					curve.getCurveRed(), curve.getCurveGreen(),
					curve.getCurveBlue(), degree[level][0]);

			SmoothSkinProcessor.releaseSource();
			CMTProcessor.brightEffect(pixels, w, h,
					degree[level][1] * 30 / 100 + 40); // 40 ~ 70
			ImageProcessUtils.saturationPs(pixels, w, h, -10);

			// bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			// bmp.setPixels(pixels, 0, w, 0, 0, w, h);
			// bmp.recycle();
			// bmp = null;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// if (bmp != null) {
			// bmp.recycle();
			// bmp = null;
			// }
		}
	}
	
}
