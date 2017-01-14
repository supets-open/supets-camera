package cn.jingling.lib.filters.realsize;

import java.lang.reflect.Array;
import android.content.Context;
import cn.jingling.lib.filters.RealsizeFilter;
import cn.jingling.lib.jpegsupport.JpegSupport;

public class RSCounterRotate extends RealsizeFilter {

	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		// TODO Auto-generated method stub
		JpegSupport.initJpegReader(inPath);
		int wSrc = JpegSupport.getReaderSrcImageWidth();
		int hSrc = JpegSupport.getReaderSrcImageHeight();

		int wTarget = hSrc;
		int hTarget = wSrc;

		JpegSupport.initJpegWriter(outPath, wTarget, hTarget, 90);

		// stepSpan的意思是：我们依次从ImageSrc里读取h行*step列数据，
		int stepSpan = 500;
		int[] pixelRect;
		int currentColumn = wSrc;
		// JpegReader每次读取必须init 读取全部行数 release
		boolean needInit = false;

		for (currentColumn = wSrc; currentColumn-stepSpan >= 0; currentColumn-=stepSpan) {
			
			if (needInit) {
				JpegSupport.initJpegReader(inPath);
				needInit = false;
			}

			pixelRect = getRotatedPixelRect(0, hSrc, currentColumn-stepSpan, currentColumn);

			JpegSupport.writeJpegLines(pixelRect, stepSpan);
			JpegSupport.finishReadingAndRelease();

			needInit = true;
		}

		// 处理最后剩下的N列数据，N<stepSpan
		if (currentColumn >0) {

			if (needInit) {
				JpegSupport.initJpegReader(inPath);
				needInit = false;
			}

			pixelRect = getRotatedPixelRect(0, hSrc, 0, currentColumn);
			JpegSupport.writeJpegLines(pixelRect, currentColumn);
			JpegSupport.finishReadingAndRelease();
			needInit = true;
		}
		
		
		JpegSupport.finishWritingAndRelease();
		
		return true;

	}

	// [top, bottom) [left, right)
	// top必须为0，bottom必须为图片高度
	private int[] getRotatedPixelRect(int top, int bottom, int left, int right) {
		// 这个“像素方块”，有(bottom-top)列*(right-left)行
		int[] pixelRect = new int[(bottom - top) * (right - left)];
		int index;
		int[] pixelsOneLine;

		for (int i = top; i < bottom; i++) {
			pixelsOneLine = JpegSupport.readJpegLines(1);
			for (int j = left; j < right; j++) {
				//顺时针 第i行j列数据所对应的旋转后的index
				//index = (j - left) * (bottom - top) + (bottom-i-1);
				//逆时针				
				index = (right - j -1) * (bottom - top) + (i - top);
				// Log.e("zhijiankun","index = "+ index);
				Array.setInt(pixelRect, index, pixelsOneLine[j]);
			}
		}

		return pixelRect;
	}

}
