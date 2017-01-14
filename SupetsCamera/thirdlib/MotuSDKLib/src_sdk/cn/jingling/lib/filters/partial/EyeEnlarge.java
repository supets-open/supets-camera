package cn.jingling.lib.filters.partial;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.detection.EyeCorrector;
import cn.jingling.lib.filters.detection.EyeCorrector.Params;
import cn.jingling.lib.filters.PartialFilter;


/** 眼睛放大编辑类，继承自PartialFilter。需要输入一个辅助点：眼睛位置。支持设置degree。
 *  该类提供的编辑图片方法，是基于原图编辑的，会直接修改原图的pixels，返回的bitmap就是原图bitmap。
 *
 */
public class EyeEnlarge extends PartialFilter {

//	private final static String TAG = "EyeEnlarge";

	private boolean mEnableEyeFinder = true;

	private int mWidth;
	private int mHeight;
	
	private int[] mOriginalPixels, mPerformedPixels;
	private int[] mX, mY, mR;
	private float[] mScale;
	private final static int MAX_OP = 30;
	private int mNum;
	
	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		mRadius = Math.min(bm.getWidth(), bm.getHeight()) / 5;
		mWidth = bm.getWidth();
		mHeight = bm.getHeight();
		mOriginalPixels = new int[mWidth * mHeight];
		bm.getPixels(mOriginalPixels, 0, mWidth, 0, 0, mWidth, mHeight);
		mPerformedPixels = Arrays.copyOf(mOriginalPixels, mWidth * mHeight);
		for (int i = 0; i < mWidth * mHeight; i ++) {
			mPerformedPixels[i] = mOriginalPixels[i];
		}
		mX = new int[MAX_OP];
		mY = new int[MAX_OP];
		mR = new int[MAX_OP];
		mScale = new float[MAX_OP];
		mNum = 0;
	}

	/** 编辑图片方法。是基于原图编辑的，直接修改原图的pixels，返回的bitmap就是原图bitmap。
	 * @param bm 输入的原图。
	 * @param point 辅助点：眼睛位置。
	 * @return 修改后的图片。
	 * @see cn.jingling.lib.filters.PartialFilter#apply(android.graphics.Bitmap, android.graphics.Point)
	 */
	@Override
	public Bitmap apply(Bitmap bm, Point point) {
		int x = point.x;
		int y = point.y;
		enlarge(bm, x, y);
		return bm;
	}

	public void setEyeFinderEnabled(boolean enabled) {
		mEnableEyeFinder = enabled;
	}

	public void enlarge(Bitmap bm, Point p) {
		enlarge(bm, p.x, p.y);
	}

	private void enlarge(Bitmap bm, int x, int y) {
		Params p = new Params();
		if (mEnableEyeFinder) {
			Point center = new EyeCorrector().getRealEyeCenter(bm, x, y);
			x = center.x;
			y = center.y;
		}
		EyeCorrector.fillBorders(bm, x, y, mRadius, p);
		updateBitmap(bm, p, x, y);
	}

	private void updateBitmap(Bitmap bm, Params p, int x, int y) {
		if (mNum >= MAX_OP) {
			return;
		}
		mX[mNum] = x;
		mY[mNum] = y;
		mR[mNum] = mRadius;
		mScale[mNum] = 0.1f;
		mNum ++;
		
		CMTProcessor.eyeEnlargeWithTags(mOriginalPixels, mPerformedPixels, mWidth, mHeight, mX, mY,
				mR, mScale, mNum);
		bm.setPixels(mPerformedPixels, 0, mWidth, 0, 0, mWidth, mHeight);
	}

	@Override
	public void release() {
		super.release();
	}
	
}