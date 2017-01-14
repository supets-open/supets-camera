package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;
import cn.jingling.lib.filters.detection.FaceDetectorResults.Human;

public class LightenEye extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		FaceDetectorResults ret = FaceDetection.detect(bm);
		if (ret.humans.length > 0) {
			int[] pixels = new int[w * h];
			bm.getPixels(pixels, 0, w, 0, 0, w, h);
//			SmoothSkinProcessor.InitializeCircle(left.y, left.x, (right.x - left.x)/3 ,right.y, right.x, (right.x - left.x)/3, pixels, w, h, 100);
			Human human = ret.humans[0];
			SmoothSkinProcessor.BrightEyes(pixels, w, h, 100, human.leftEye.y, human.leftEye.x, human.rightEye.y, human.rightEye.x);
			bm.setPixels(pixels, 0, w, 0, 0, w, h);
		}
		return bm;
	}

}
