package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.ExpVar;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.IntelligentBeautify;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectionStatus;
import cn.jingling.lib.filters.detection.FaceDetectorResults;
import cn.jingling.lib.filters.detection.FaceDetectorResults.Human;

public class CameraSelf extends OneKeyFilter {

	private final static int[] VALUES = { 25, 25, 40, 40 };

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		applyBeautify(bm, bm, VALUES, FaceDetectionStatus.UNKNOWN, null);
		return bm;
	}
	
	/**
	 * 
	 * @param original
	 * @param performed
	 * @param beautify -1 to apply random shape
	 * @param left could be null, then system will detect face, need more time
	 * @param right could be null, then system will detect face, need more time
	 * @param mouth could be null, then system will detect face, need more time
	 * @param hasFace meaningless if left is null
	 * @return true if find a face
	 */
	public static FaceDetectionStatus applyBeautify(Bitmap original, Bitmap performed,
			int[] beautify, FaceDetectionStatus status, FaceDetectorResults result) {
		if (beautify[0] < 0) {
			int ranf = 1, rane= -1;
			switch (beautify[0]) {
			case -1:
				ranf = -1;
				rane = -1;
				break;
			case -2:
				ranf = -1;
				rane = 1;
				break;
			case -3:
				ranf = 1;
				rane = -1;
				break;
			case -4:
				ranf = 1;
				rane = 1;
				break;
			}
			int[] temp = { ranf * (int) (Math.random() * 50 + 120),
					rane * (int) (Math.random() * 50 + 120), 40, 30 };
			beautify = temp;
		}
		if (status == FaceDetectionStatus.UNKNOWN) {
			result = FaceDetection.detect(original);
		}
		int w = original.getWidth();
		int h = original.getHeight();
		int hmin, hmax, smin, smax, vmin, vmax;
		hmin = 0;
		hmax = 40;
		smin = 0;
		smax = 255;
		vmin = 20;
		vmax = 255;
		if (status == FaceDetectionStatus.POSITIVE) {
			Human human = result.humans[0];
			int size = Math.abs(human.leftEye.x - human.rightEye.x) / 3;
			int x = human.leftEye.x - size * 3 / 4;
			int y = human.leftEye.y + size / 2;
			if (x >= 0 && x + size <= w && y >= 0 && y + size <= h) {
				int[] skin = new int[size * size];
				original.getPixels(skin, 0, size, x, y, size, size);
				ExpVar ev = CMTProcessor.computeHueExpectationAndVariance(skin,
						size, size);
				if (ev.var <= 100) {
					hmin = Math.max(ev.expect - 15, 0);
					hmax = Math.min(ev.expect + 15, 180);
				}
			}
		}
		int[] pixels = new int[w * h];
		original.getPixels(pixels, 0, w, 0, 0, w, h);
		if (beautify[2] != 0) {
			CMTProcessor.fastAverageBlurWithThresholdWeightSkinDetection(pixels, w,
					h, 5, 10, beautify[2] * 256 / 100, hmin, hmax, smin, smax, vmin, vmax);
		}
		if (beautify[3] != 0) {
			CMTProcessor.brightEffect(pixels, w, h, beautify[3] * 20 / 100 + 50);
			ImageProcessUtils.saturationPs(pixels, w, h, -10);
		}
		if (status == FaceDetectionStatus.POSITIVE) {
			Human human = result.humans[0];
			IntelligentBeautify.partialFaceProcess(pixels, w, h, human.leftEye, human.rightEye,
					human.mouth, beautify[1], beautify[0]);
		}
		performed.setPixels(pixels, 0, w, 0, 0, w, h);
		return status;
	}

}
