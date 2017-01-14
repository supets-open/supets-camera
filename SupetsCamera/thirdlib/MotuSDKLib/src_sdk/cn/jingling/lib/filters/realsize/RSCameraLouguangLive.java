package cn.jingling.lib.filters.realsize;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import cn.jingling.lib.file.ImageFile;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Layer;

public class RSCameraLouguangLive extends RSLineFilter {

	private Bitmap mLayer_o;
	private Bitmap mLayer_s;
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		int orientation = ImageFile.getImageOrientation(cx, Uri.fromFile(new File(inPath)));
		Log.e("zhijiankun", "Exif orientation = " + orientation);
		
		mLayer_o = Layer.getLayerImage(cx, "layers/live_louguang_1", Layer.Type.NORMAL);
		mLayer_s = Layer.getLayerImage(cx, "layers/live_louguang_2", Layer.Type.NORMAL);
		
		if (orientation != 0) {
			boolean needFlip = false;
			if (args != null && args.length >= 1 && (args[0] != 0)) {
				needFlip = true;
			}
			
			if (needFlip) {
				orientation = 360 - orientation;
			}
			
			Matrix m1 = new Matrix();
			m1.setRotate(-(float)orientation);
			mLayer_o = Bitmap.createBitmap(mLayer_o, 0, 0, mLayer_o.getWidth(), mLayer_o.getHeight(), m1, true);
			mLayer_s = Bitmap.createBitmap(mLayer_s, 0, 0, mLayer_s.getWidth(), mLayer_s.getHeight(), m1, true);
		}
		
		if (args != null && args.length >= 3) {
			int needJpegRotate = args[1];
			int jpegOrientation = args[2];
			Log.e("zhijiankun", "needJpegRotate = " + needJpegRotate);
			Log.e("zhijiankun", "Jpeg Orientation = " + jpegOrientation);
			Matrix m2 = new Matrix();
			m2.reset();
			m2.postRotate(jpegOrientation);
			mLayer_o = Bitmap.createBitmap(mLayer_o, 0, 0, mLayer_o.getWidth(),
					mLayer_o.getHeight(), m2, true);
			mLayer_s = Bitmap.createBitmap(mLayer_s, 0, 0, mLayer_s.getWidth(),
					mLayer_s.getHeight(), m2, true);
		}
		
		return super.apply(cx, inPath, outPath, args);
	}
	
	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
		int w = pixels.length;
		int[] layerPixels_o;
		int[] layerPixels_s;
		
		//
		layerPixels_o = getLayerPixels(mLayer_o, line, height);
		CMTProcessor.rsOverlayAlphaEffect(pixels, layerPixels_o, w, 1, mLayer_o.getWidth(), 1, 50);
		
		layerPixels_s = getLayerPixels(mLayer_s, line, height);
		CMTProcessor.rsScreenEffect(pixels, layerPixels_s, w, 1, mLayer_s.getWidth(), 1);
	}

	@Override
	protected void releaseLayers() {
		// TODO Auto-generated method stub
		mLayer_o.recycle();	
		mLayer_s.recycle();	
	}

}
