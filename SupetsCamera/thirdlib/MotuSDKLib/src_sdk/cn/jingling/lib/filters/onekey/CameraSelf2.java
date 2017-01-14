package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.IntelligentBeautify;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectionStatus;
import cn.jingling.lib.filters.detection.FaceDetectorResults;

public class CameraSelf2 extends OneKeyFilter {

	private final static int[] VALUES = { 35, 35, 90,30 };
	private final static String STATISTIC_LABEL = "AutoBeautify";
	
	private static boolean mSmoothInited = false;
	
	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		applyBeautify(cx, bm, bm, VALUES, FaceDetectionStatus.UNKNOWN, null);
		return bm;
	}
	
	public static Bitmap applyBeautify(Context cx, Bitmap bm, int[] beautify) {
		applyBeautify(cx, bm, bm, beautify, FaceDetectionStatus.UNKNOWN, null);
		release();
		return bm;
	}
	

	/** 对图像进行人脸检测，若检测到人脸，则将检测结果保存到results参数中，并根据beautify提供的参数进行智能美容编辑。编辑后的结果保存在performed中。
	 * @param cx
	 * @param original 原图
	 * @param performed 处理后图片
	 * @param beautify 传入四个int参数，分别代表：瘦脸程度、大眼程度、磨皮程度、美白程度
	 * @param status 是否已经进行了人脸检测。若为UNKNOWN，则进行人脸检测，并将检测结果存储到results。若为POSITIVE，则直接使用results中存储的人脸数据。
	 * @param results 用于存储人脸检测的结果，不可以为null
	 * @return 返回人脸检测的结果。若检测成功，则返回FaceDetectionStatus.POSITIVE。否则，返回FaceDetectionStatus.NEGTIVE。
	 */
	public static FaceDetectionStatus applyBeautify(Context cx, Bitmap original, Bitmap performed,
			int[] beautify, FaceDetectionStatus status, FaceDetectorResults results) {
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
					rane * (int) (Math.random() * 50 + 120), 40, 2 };
			beautify = temp;
		}
		if (status == FaceDetectionStatus.UNKNOWN) {

			results.copy(FaceDetection.detect(original));

			if (results.humans.length > 0) {
				status = FaceDetectionStatus.POSITIVE;
			} else {
				status = FaceDetectionStatus.NEGTIVE;
			}
		}
		int w = original.getWidth();
		int h = original.getHeight();
		int[] pixels = new int[w * h];
		original.getPixels(pixels, 0, w, 0, 0, w, h);
		if (!mSmoothInited) {
			SmoothSkinProcessor.buffingTemplate(pixels, w, h, 10, 1);
			mSmoothInited = true;
		}
		Curve curve = new Curve(cx, "curves/skin_smooth.dat");
		if (beautify[2] != 0) {
			SmoothSkinProcessor.faceBuffingWeight(pixels, w, h, curve.getCurveRed(), curve.getCurveGreen(), curve.getCurveBlue(), beautify[2]);
		}
		if (beautify[3] != 0) {
			CMTProcessor.brightEffect(pixels, w, h, beautify[3] * 30 / 100 + 40); //40 ~ 70
			ImageProcessUtils.saturationPs(pixels, w, h, -10);
		}
		if (status == FaceDetectionStatus.POSITIVE) {
			if (!(beautify[1] == 0 && beautify[0] == 0)) {
				IntelligentBeautify.partialFaceProcess(pixels, w, h, results.humans[0].leftEye, results.humans[0].rightEye,
						results.humans[0].mouth, beautify[1], beautify[0]);
			}
		}
		performed.setPixels(pixels, 0, w, 0, 0, w, h);
		return status;
	}
	
	public static void release() {
		if (mSmoothInited) {
			SmoothSkinProcessor.releaseSource();
			mSmoothInited = false;
		}
	}
	
}
