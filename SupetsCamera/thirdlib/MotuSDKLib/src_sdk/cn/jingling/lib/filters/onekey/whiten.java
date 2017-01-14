package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;

public class whiten extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		Point left = new Point();
		Point right = new Point();
		Point mouth = new Point();
		int w = bm.getWidth();
		int h = bm.getHeight();
//		Curve c;
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		FaceDetectorResults hn = FaceDetection.detect(bm);

		if(hn == null || hn.humans.length <= 0)
		{
			bm.setPixels(pixels, 0, w, 0, 0, w, h);
		}
		else
		{
			int leftx = hn.humans[0].leftEye.x;
			int lefty = hn.humans[0].leftEye.y;
			int rightx = hn.humans[0].rightEye.x;
			int righty = hn.humans[0].rightEye.y;
			SmoothSkinProcessor.whiten(pixels, w, h, 1, lefty, leftx, righty, rightx);			
			bm.setPixels(pixels, 0, w, 0, 0, w, h);
		}
//		if (findEyesAndMouth(bm, left, right, mouth)) 
//		SmoothSkinProcessor.whiten(pixels, w, h, 1, left.y, left.x, right.y, right.x);
//		SmoothSkinProcessor.whiten(pixels, w, h, 50, 0, 0, 0, 0);
//		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
