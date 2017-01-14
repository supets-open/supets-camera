package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectionStatus;
import cn.jingling.lib.filters.detection.FaceDetectorResults;

/** 先进行人脸检测。若检测成功，则使用智能美容3档进行美化。若检测失败，则使用USM处理。不修改原图，返回处理后的新图。
 *
 */
public class IntelligentUSM extends OneKeyFilter {

	private static final int[] BEAUTIFY_DEGREE = { 35, 35, 90,30 };
	@Override
	public Bitmap apply(Context cx, Bitmap originBm) {
		this.statisticEvent();
		Bitmap performBm = originBm.copy(originBm.getConfig(), true);
		
		FaceDetectorResults results = FaceDetection.detect(performBm);
	
		if (results.humans.length > 0) {
			CameraSelf2.applyBeautify(cx,
					originBm, performBm, BEAUTIFY_DEGREE,
					FaceDetectionStatus.POSITIVE, results);
			CameraSelf2.release();
		} else {
			performBm = applyScenePeocess(performBm);
		}

		return performBm;
	}
	
	private Bitmap applyScenePeocess(Bitmap bm) {
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.CLAHERGB3(pixels, w, h, 4,20);
		//ImageProcessUtils.saturationPs(pixels, w, h, 20);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}
	
}
