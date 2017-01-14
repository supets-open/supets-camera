package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.ExpVar;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;
import cn.jingling.lib.filters.detection.FaceDetectorResults.Human;
import cn.jingling.lib.filters.GlobalFilter;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.utils.LogUtils;

public class SmoothSkin extends GlobalFilter {

	private final static String TAG = "SmoothSkin";
	private Bitmap mPerformedBitmap;
	private Point mLeft, mRight, mMouth;

	public static void setup(Bitmap bm, Bitmap perform, boolean debug) {
		int hmin, hmax, smin, smax, vmin, vmax;
		int w = bm.getWidth();
		int h = bm.getHeight();
		hmin = 0;
		hmax = 40;
		smin = 0;
		smax = 255;
		vmin = 20;
		if (debug) {
			vmax = 254;
		} else {
			vmax = 255;
		}
		FaceDetectorResults results = FaceDetection.detect(bm);
		if (results.humans.length > 0) {
			Human human = results.humans[0];
			int size = Math.abs(human.leftEye.x - human.rightEye.x) / 3;
			int x = human.leftEye.x - size * 3 / 4;
			int y = human.leftEye.y + size / 2;
			if (x >= 0 && x + size <= w && y >= 0 && y + size <= h) {
				int[] skin = new int[size * size];
				bm.getPixels(skin, 0, size, x, y, size, size);
				ExpVar ev = CMTProcessor.computeHueExpectationAndVariance(skin,
						size, size);
				if (debug) {
					bm.setPixels(skin, 0, size, x, y, size, size);
				}
				LogUtils.d(
						TAG,
						"expectation and veriance: "
								+ String.valueOf(ev.expect) + "   "
								+ String.valueOf(ev.var));
				if (ev.var <= 100) {
					hmin = Math.max(ev.expect - 15, 0);
					hmax = Math.min(ev.expect + 15, 180);
				}
			}
		}
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.fastAverageBlurWithThresholdWeightSkinDetection(pixels, w,
				h, 5, 10, 256, hmin, hmax, smin, smax, vmin, vmax);
//		CMTProcessor.brightEffect(pixels, w, h, 70);
		perform.setPixels(pixels, 0, w, 0, 0, w, h);
	}

	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		mPerformedBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				bm.getConfig());
		setup(bm, mPerformedBitmap, false);
	}

	@Override
	public Bitmap apply(Context cx, int degree) {
		// TODO Auto-generated method stub
		Bitmap bm = ImageProcessUtils.mergeBitmap(mPerformedBitmap,
				mOriginalBitmap, degree / 100.0);
		return bm;
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
