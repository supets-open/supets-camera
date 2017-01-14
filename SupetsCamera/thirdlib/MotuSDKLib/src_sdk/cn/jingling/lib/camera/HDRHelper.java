package cn.jingling.lib.camera;

import java.util.Formatter.BigDecimalLayoutForm;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import cn.jingling.lib.filters.FilterFactory;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import cn.jingling.lib.utils.LogUtils;

public class HDRHelper {
	
	private static final float MAX_EXPOSURE = 1.5f;
	private static final float MIN_EXPOSURE = 1.5f;
	
	public static void updateExposure(Camera camera, boolean bright) {
		final Parameters params = camera.getParameters();
		if (!isSupportExposure(params)) {
			return;
		}
		
		//cola 2014-01-07

		int ev = getOptimizedExposureCompensationIndex(
				params, bright);
		ev = -2;
		LogUtils.d("Exposure", "set ev " + ev);
		params.setExposureCompensation(ev);
		try {
			camera.setParameters(params);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap mergeHDRImage(Bitmap bitmapBright, Bitmap bitmapDark) {
		int w = bitmapBright.getWidth();
		int h = bitmapBright.getHeight();
		int[] imgDark = new int[w * h];
		bitmapDark.getPixels(imgDark, 0, w, 0, 0, w, h);
		int[] imgBright = new int[w * h];
		bitmapBright.getPixels(imgBright, 0, w, 0, 0, w, h);
		int[] dst = new int[w * h];
		SmoothSkinProcessor.HDR(imgDark, imgBright, dst, w, h);
		imgDark = null;
		imgBright = null;
		Bitmap bitmapMerged = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bitmapMerged.setPixels(dst, 0, w, 0, 0, w, h);
		
		
		ImageProcessUtils.saturationPs(bitmapMerged, 58);//HSI
 
		return bitmapMerged;
	}
	
	/**
	 * 
	 * @param srcBm 
	 * @param isRecycle 是否需要Recycle传入的srcBm
	 * @return 处理后的仿真HDR效果 是bitmap
	 */
	public static Bitmap HDRSimulationBitmap(Bitmap srcBm, boolean isRecycle) {
		int[] pixels = HDRSimulation(srcBm, false);
		int w = srcBm.getWidth();
		int h = srcBm.getHeight();
		Bitmap bmMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bmMask.setPixels(pixels, 0, w, 0, 0, w, h);
		if(isRecycle) {
			srcBm.recycle();
		}
		return bmMask;
	}
	
	/**
	 * 
	 * @param srcBm 
	 * @param isRecycle 是否需要Recycle传入的srcBm
	 * @return 处理后的仿真HDR效果 返回一个图像的数组数据
	 */
	public static int[] HDRSimulation(Bitmap srcBm, boolean isRecycle) {
		if(srcBm == null || srcBm.isRecycled()) {
			LogUtils.e("HDRSimulation", "error!! srcBm == null || srcBm.isRecycled() == true");
			return null;
		}
		int w = srcBm.getWidth();
		int h = srcBm.getHeight();
		int[] imgPixels = new int[w * h];
		srcBm.getPixels(imgPixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.HDRsimple(imgPixels, w, h, 5,8);
		if(isRecycle) {
			srcBm.recycle();
		}
		return imgPixels;
	}
	
	public static void clearExposure(Camera camera) {
		if(camera!=null){
			final Parameters params = camera.getParameters();
			if (!isSupportExposure(params)) {
				return;
			}
			
			try {
				params.setExposureCompensation(0);
				camera.setParameters(params);
			} catch (RuntimeException e) {
				// LogUtils.e("HDRHelper", "Invalid exposure value");
			}
		}
	}
	
	private static boolean isSupportExposure(Parameters params) {
		if (null == params) {
			return false;
		}
		int min = params.getMinExposureCompensation();
		int max = params.getMaxExposureCompensation();
		if (min == 0 && max == 0) {
			return false;
		}
		
		return true;
	}

	private static int getOptimizedExposureCompensationIndex(Parameters params,
			boolean positive) {
		int mic = params.getMinExposureCompensation();
		int mac = params.getMaxExposureCompensation();
		float step = params.getExposureCompensationStep();
		int limit = (int) ((positive ? MAX_EXPOSURE : MIN_EXPOSURE) / step);
		if (limit == 0) {
			return positive ? (int)(1.0f*mac*2.0f/3.0f) : mic;
		}
		LogUtils.d("Exposure", "mic " + mic + "mac " + mac + "step " + step + "limit " + limit);
		return positive ? (int)(1.0f*Math.min(mac, limit)*2.0f/3.0f) : Math.max(mic, -limit);
	}

}
