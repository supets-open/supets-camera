package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;
import cn.jingling.lib.filters.detection.FaceDetectorResults.Human;

public class Lighten extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		Curve c;
		FaceDetectorResults rets = FaceDetection.detect(bm);
		if (rets.humans.length > 0) {
			int[] pixels = new int[w * h];
			bm.getPixels(pixels, 0, w, 0, 0, w, h);
			//�����ݣ������ʱ����Ҫ�ڶ�Ӧ���㷨������Ӧ���ж�
//			SmoothSkinProcessor.InitializeCircle(left.y, left.x, (right.x - left.x)/3 ,right.y, right.x, (right.x - left.x)/3, pixels, w, h);
			Human human = rets.humans[0];
			int tmpJudge = SmoothSkinProcessor.LightenDemo(pixels, w, h, human.rightEye.x - human.leftEye.x, 2 * (human.rightEye.x - human.leftEye.x), human.rightEye.y, (human.rightEye.x + human.leftEye.x) /2);
			if(tmpJudge == 1)
			{
				c = new Curve(cx, "curves/test4_dark.amp");
			}
			else
			{
				c = new Curve(cx, "curves/test4.amp");
			}
			
			SmoothSkinProcessor.buffingTemplate(pixels, w, h, 10, 0);	
			SmoothSkinProcessor.faceBuffing(pixels, w, h, c.getCurveRed(),
					c.getCurveGreen(), c.getCurveBlue());
			SmoothSkinProcessor.releaseSource();
			
			bm.setPixels(pixels, 0, w, 0, 0, w, h);
		}
		else
		{
			int[] pixels = new int[w * h];
			bm.getPixels(pixels, 0, w, 0, 0, w, h);
			c = new Curve(cx, "curves/test4.amp");
			SmoothSkinProcessor.buffingTemplate(pixels, w, h, 10, 0);	
			SmoothSkinProcessor.faceBuffing(pixels, w, h, c.getCurveRed(),
					c.getCurveGreen(), c.getCurveBlue());
			SmoothSkinProcessor.releaseSource();
			
			bm.setPixels(pixels, 0, w, 0, 0, w, h);
		}
		
		return bm;
	}

}
