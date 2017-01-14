package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.IntelligentBeautify;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;
import cn.jingling.lib.filters.detection.FaceDetectorResults.Human;

public class EyeBrighten extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		FaceDetectorResults result = FaceDetection.detect(bm);
		if (result.humans.length == 0) {
			return bm;
		}
		Human human = result.humans[0];
		int w = bm.getWidth();
		int h = bm.getHeight();
		int d = Math.abs(human.leftEye.x - human.rightEye.x) / 3;
		int y = human.leftEye.y - Math.abs(human.leftEye.x - human.rightEye.x) / 12;
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		int[] eye = IntelligentBeautify.getFullPixels(pixels, w, h, human.leftEye.x - d / 2, y - d / 2, d);
		CMTProcessor.eyeBrighten(eye, d, 65);
		IntelligentBeautify.setFullPixels(pixels, w, h, eye, human.leftEye.x - d / 2, y - d / 2, d);
		eye = IntelligentBeautify.getFullPixels(pixels, w, h, human.rightEye.x - d / 2, y - d / 2, d);
		CMTProcessor.eyeBrighten(eye, d, 65);
		IntelligentBeautify.setFullPixels(pixels, w, h, eye, human.rightEye.x - d / 2, y - d / 2, d);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
