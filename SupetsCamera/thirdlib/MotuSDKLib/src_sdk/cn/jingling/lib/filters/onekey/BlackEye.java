package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;
import cn.jingling.lib.filters.detection.FaceDetectorResults.Human;

public class BlackEye extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		FaceDetectorResults rets = FaceDetection.detect(bm);
		if (rets.humans.length > 0) {
			int[] pixels = new int[w * h];
			bm.getPixels(pixels, 0, w, 0, 0, w, h);
			Human human = rets.humans[0];
			SmoothSkinProcessor.InitializeCircle(human.leftEye.y, human.leftEye.x, (human.rightEye.x - human.leftEye.x)/3 ,human.rightEye.y, human.rightEye.x, (human.rightEye.x - human.leftEye.x)/3, pixels, w, h, 100);
			bm.setPixels(pixels, 0, w, 0, 0, w, h);
		}
		
		return bm;
	}

}
