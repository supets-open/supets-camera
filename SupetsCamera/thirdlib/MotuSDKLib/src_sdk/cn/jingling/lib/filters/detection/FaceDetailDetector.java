package cn.jingling.lib.filters.detection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.utils.LogUtils;

import com.lmd.handler.LMDHandler;

public class FaceDetailDetector extends AbstractDetector {

	private static final String TAG = "FaceDetailDetector";
	
	private LMDHandler mProcessor;

	@Override
	public void init(Context cx) {
		mProcessor = new LMDHandler();
		boolean init = mProcessor.LMDInitHandler(generateFilename(cx, "LMD.mdl"));
		LogUtils.d(TAG, String.valueOf(init));
	}

	@Override
	public Point[] detect(Bitmap bm) {
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		byte[] bgr = new byte[w * h * 3];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.transToReversedBGR(pixels, bgr, w, h);
		pixels = null;
		try {
			OutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory() + "/imagestream.dat");
			os.write(bgr);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int[] ret = mProcessor.LMDWorker(bgr, w, h, getZoomVal(w, h));
		bgr = null;
		Point[] points = new Point[ret.length];
		for (int i = 0; i < ret.length / 2; i ++) {
			points[i] = new Point(ret[i * 2], ret[i * 2 + 1]);
		}
		return points;
	}
	
	@Override
	public void release() {
		mProcessor.LMDDestoryHandler();
	}

	private static String generateFilename(Context context, String assetFile) {
		InputStream is;
		try {
			is = context.getResources().getAssets().open(assetFile);
			File cascadeDir = context.getDir("face_detail",
					Context.MODE_PRIVATE);
			File cascadeFile = new File(cascadeDir, "face_detail" + assetFile);
			FileOutputStream os = new FileOutputStream(cascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();
			return cascadeFile.getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static float getZoomVal(int width, int height) {
		int minsize = width < height ? width : height;
		float fScale = (1.3f);
		if (minsize >= 1920)
			fScale = (6.0f);
		else if (minsize >= 1024)
			fScale = (4.5f);
		else if (minsize >= 640)
			fScale = (3.0f);
		else if (minsize >= 320)
			fScale = (2.0f);
		return fScale;
	}
	
}
