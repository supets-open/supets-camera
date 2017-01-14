package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.GlobalFilter;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.SmoothSkinProcessor;

/** 美肤编辑类，继承自GlobalFilter。需要输入一个Degree：美肤程度。
 *  该类提供的编辑图片方法，不是是基于原图编辑的。会生成修改后的新图，并返回，原图不会被修改。
 *
 */
public class BetterSkin extends GlobalFilter {

	private final static int WHITE_DEGREE = 55;

	private Bitmap mPerformedBitmap;
	
	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.buffingTemplate(pixels, w, h, 10, 1);
		Curve c = new Curve(cx, "curves/skin_smooth.dat");
		SmoothSkinProcessor.faceBuffing(pixels, w, h, c.getCurveRed(),
				c.getCurveGreen(), c.getCurveBlue());
		SmoothSkinProcessor.releaseSource();
		int[] pixelsOri = new int[w * h];
		bm.getPixels(pixelsOri, 0, w, 0, 0, w, h);
//		CMTProcessor.mergeWeight(pixelsOri, pixels, w, h, 2);
		CMTProcessor.brightEffect(pixels, w, h, WHITE_DEGREE);
//		CMTProcessor.fastAverageBlurWithThreshold(pixels, w, h, 5, 10);
		ImageProcessUtils.saturationPs(pixels, w, h, -5);
		mPerformedBitmap = Bitmap.createBitmap(pixels, w, h, bm.getConfig());
	}

	@Override
	public Bitmap apply(Context cx, int degree) {
		// TODO Auto-generated method stub
//		return ImageProcessUtils.mergeBitmap(mOriginalBitmap, mPerformedBitmap, (100 - degree) / 100.0);
		return ImageProcessUtils.nativeMerge(mOriginalBitmap, mPerformedBitmap, degree / 100.0);
	}

	@Override
	public void release() {
		super.release();
		if (mPerformedBitmap != null) {
			mPerformedBitmap.recycle();
			mPerformedBitmap = null;
		}
	}
}
