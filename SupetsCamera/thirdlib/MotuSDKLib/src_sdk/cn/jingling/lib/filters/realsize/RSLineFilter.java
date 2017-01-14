package cn.jingling.lib.filters.realsize;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import cn.jingling.lib.file.ImageFile;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.RealsizeFilter;
import cn.jingling.lib.jpegsupport.JpegSupport;
import cn.jingling.lib.utils.ErrorHandleHelper;
import cn.jingling.lib.utils.LogUtils;

abstract public class RSLineFilter extends RealsizeFilter {

	public static int NEED_FLIP = 1;
	private static Object mRealSizeSaveMutex = new Object();
	private static final String TAG = "Realsize";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.jingling.lib.filters.RealsizeFilter#apply(android.content.Context,
	 * java.lang.String, java.lang.String, int[])
	 */
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		// TODO Auto-generated method stub
		synchronized (mRealSizeSaveMutex) {

			super.apply(cx, inPath, outPath, args);
			LogUtils.i(TAG, "Realsize save begin : inPath = " + inPath);
			LogUtils.i(TAG, "Realsize save begin : outPath = " + outPath);

			if (inPath.equals(outPath)) {
				ErrorHandleHelper.handleErrorMsg("Realsize error : inPath and outPath can't be the same !", TAG);
				return false;
				
			}
			if (!checkJpg(inPath)) {
				ErrorHandleHelper.handleErrorMsg("Realsize error : inPath file  !", TAG);
				return false;
			}

			boolean needFlip = false;
			if (args != null && args.length>=1 && args[0] == NEED_FLIP) {
				needFlip = true;
			}

			int initReader = JpegSupport.initJpegReader(inPath);

			if (initReader != 0) {
				ErrorHandleHelper.handleErrorMsg("Realsize error : initJpegReader failed !", TAG);
				return false;
			}

			int initWriter = JpegSupport.initJpegWriter(outPath, -1, -1, ImageFile.JPEG_QUALITY);

			if (initWriter != 0) {
				ErrorHandleHelper.handleErrorMsg("Realsize error : initJpegWriter failed !", TAG);
				return false;
			}
			
			// 逐行贴纸功能的实现。旋转是为了应对图片Exif中Orientation不为0的情况。
			RectF range = new RectF(mPosterRangeLeft, mPosterRangeTop, mPosterRangeRight, mPosterRangeBottom);
			if (mPoster != null) {
				int orientation = ImageFile.getImageOrientation(cx, Uri.fromFile(new File(inPath)));
				LogUtils.e("zhijiankun","exif orientation: "+ orientation);
				
				if (needFlip) {
					orientation = 360 - orientation;
				}
				
				Matrix m = new Matrix();
				m.setRotate(-(float)orientation);
				mPoster = Bitmap.createBitmap(mPoster, 0, 0, mPoster.getWidth(), mPoster.getHeight(), m, true);
				RectF rangeOriginal = new RectF(mPosterRangeLeft, mPosterRangeTop, mPosterRangeRight, mPosterRangeBottom);
				
				Matrix m2 = new Matrix();
				m2.setRotate(-(float)orientation, 0.5f, 0.5f);
				m2.mapRect(range, rangeOriginal);
				
				//range = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
				LogUtils.e("zhijiankun","rangeOriginal: "+ rangeOriginal);
				LogUtils.e("zhijiankun","range: "+ range);
				
			}

			int h = JpegSupport.getReaderSrcImageHeight();
			int w = JpegSupport.getReaderSrcImageWidth();
			LogUtils.e("zhijiankun","jpeg h: "+ h);
			LogUtils.e("zhijiankun","jpeg w: "+ w);
			int[] pixels;
			int[] posterPixels;
			for (int i = 0; i < h; i++) {
				pixels = JpegSupport.readJpegLines(1);
				if (needFlip) {
					FlipUtil.flipLine(cx, pixels, i, h);
				}
				applyLine(cx, pixels, i, h);
				if (mPoster != null) {	
					if ((float) i / (float) h >= range.top
							&& (float) i / (float) h <= range.bottom) {
						posterPixels = getLayerPixels(mPoster, i, h);
						CMTProcessor.rsCoverageEffect(pixels, posterPixels, w,
								1, mPoster.getWidth(), 1);
					}
				}

				JpegSupport.writeJpegLines(pixels, 1);
			}
			JpegSupport.finishWritingAndRelease();
			JpegSupport.finishReadingAndRelease();

			releaseLayers();
			setExif(inPath, outPath);
			LogUtils.i("Realsize", "Realsize save finished !");

			return true;
		}

	}

	/**
	 * 根据原图待处理行数，得到对应的layer像素。
	 * 
	 * @param layer
	 *            layer的全部像素。
	 * @param line
	 *            当前正在处理原图第几行。0到height-1。
	 * @param height
	 *            原图高度：即原图共有几行。
	 * @return 对应的layer像素.
	 */
	protected int[] getLayerPixels(Bitmap layer, int line, int height) {
		int pw = layer.getWidth();
		int[] layerPixels = new int[pw];
		int y = line * layer.getHeight() / height;
		layer.getPixels(layerPixels, 0, pw, 0, y, pw, 1);
		return layerPixels;
	}

	/**
	 * 
	 * @param cx
	 * @param pixels
	 *            需要处理的原图像素
	 * @param line
	 *            当前正在处理原图第几行。0到height-1。
	 * @param height
	 *            原图高度：即原图共有几行。
	 */
	abstract protected void applyLine(Context cx, int[] pixels, int line,
			int height);

	/**
	 * 释放滤镜计算过程中，使用到的layer bitmap等变量。如果不需要释放任何变量，就以空函数实现。
	 */
	abstract protected void releaseLayers();
	
}
