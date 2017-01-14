package cn.jingling.lib.filters.realsize;

import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.RealsizeFilter;
import cn.jingling.lib.jpegsupport.JpegSupport;
import cn.jingling.lib.utils.LogUtils;
import android.content.Context;

public class RSTestYBW extends RealsizeFilter{
	private static final String TAG = null;

	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		// TODO Auto-generated method stub
		try {
			super.apply(cx, inPath, outPath, args);

			if (inPath.equals(outPath)) {
				return false;
			}
			if (!checkJpg(inPath)) {
				return false;
			}

			int initReader = JpegSupport.initJpegReader(inPath);

			if (initReader != 0) {
				return false;
			}

			int initWriter = JpegSupport.initJpegWriter(outPath, -1, -1, 90);

			if (initWriter != 0) {
				return false;
			}

			int h = JpegSupport.getReaderSrcImageHeight();
			int w = JpegSupport.getReaderSrcImageWidth();

			int[] srcPixels;
			int[] outPixels;
			int r = 5;
			int times = 3;
			int channel = 3;
			
			CMTProcessor.progressiveLineInitialize(r, w, h, times, channel);
			
			long time = System.currentTimeMillis();
			
			for (int i = 0; i <  h; i++) {
				srcPixels = JpegSupport.readJpegLines(1);

				outPixels = CMTProcessor.progressiveLineProcess(srcPixels, w);
				if (outPixels != null) {
					JpegSupport.writeJpegLines(outPixels, 1);
				}
			}
			
			for (int j = 0; j < r * times; j++) {
				outPixels = CMTProcessor.progressiveLineProcess(null, w);
				if (outPixels != null) {
					JpegSupport.writeJpegLines(outPixels, 1);
				}
			}
			
			JpegSupport.finishWritingAndRelease();  //必须写完
			JpegSupport.finishReadingAndRelease();  //必须读完
			
			LogUtils.d(TAG, "time consume: " + (System.currentTimeMillis() - time));

//			setExif(inPath, outPath);

			return true;

		} catch (Exception e) {
			// 不论发生什么Exception，我们只需要返回false告诉上层，图片存储未完成
			e.printStackTrace();
			return false;
		}

	}


}
