package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.detection.EyeCorrector;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;
import cn.jingling.lib.filters.detection.EyeCorrector.Params;
import cn.jingling.lib.filters.GlobalFilter;

public class EyeEnlargeAuto extends GlobalFilter {

	private boolean mEnableEyeFinder = true;
	private Bitmap mTempBitmap;
	private FaceDetectorResults mFaceDetectorResults = new FaceDetectorResults();

	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		FaceDetection fd = new FaceDetection();
		mFaceDetectorResults = FaceDetection.detect(bm);
		/* for (int i = 0; i < mFaceDetectorResults.numOfHumans; i++) {
			enlarge(bm, (int) mFaceDetectorResults.humans[i].LeftEyePosition.x,
					(int) mFaceDetectorResults.humans[i].LeftEyePosition.y, 50);
			enlarge(bm, (int) mFaceDetectorResults.humans[i].RightEyePosition.x,
					(int) mFaceDetectorResults.humans[i].RightEyePosition.y, 50);		
		 }*/
	}

	public void setEyeFinderEnabled(boolean enabled) {
		mEnableEyeFinder = enabled;
	}

	private void enlarge(Bitmap bm, int x, int y, int degree) {
		Params p = new Params();
		int r;
		int[] pixels;
		if (mEnableEyeFinder) {
			Point center = new EyeCorrector().getRealEyeCenter(bm, x, y);
			x = center.x;
			y = center.y;
		}
		r = Math.min(bm.getWidth(), bm.getHeight()) / 8;
		EyeCorrector.fillBorders(bm, x, y, r, p);
		pixels = new int[p.w * p.h];
		bm.getPixels(pixels, 0, p.w, p.x0, p.y0, p.w, p.h);
		CMTProcessor.eyeEnlarge(pixels, p.w, p.h, x - p.x0, y - p.y0, r,
				degree / 100f);
		bm.setPixels(pixels, 0, p.w, p.x0, p.y0, p.w, p.h);
	}

	@Override
	public Bitmap apply(Context cx, int degree) {
		mTempBitmap = Bitmap.createBitmap(mOriginalBitmap);
		 for (int i = 0; i < mFaceDetectorResults.humans.length; i++) {
			enlarge(mTempBitmap, mFaceDetectorResults.humans[i].leftEye.x,
					mFaceDetectorResults.humans[i].leftEye.y, degree);
			enlarge(mTempBitmap, mFaceDetectorResults.humans[i].rightEye.x,
					mFaceDetectorResults.humans[i].rightEye.y, degree);
		 }
		return mTempBitmap;
	}

	@Override
	public void release() {
		super.release();
	}
}