package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.GlobalFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import cn.jingling.lib.filters.detection.FaceDetection;

public class ColorTemperature extends GlobalFilter{
	
	int mDefaultSeekBarValue = -1;
 
	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		
		Bitmap originBm = mOriginalBitmap;		
		Bitmap performedBm = mOriginalBitmap.copy(mOriginalBitmap.getConfig(), true);
		
		int w = originBm.getWidth();
		int h = originBm.getHeight();
		int[] pixels_src = new int[w * h];
  
		int size = w*h;
		
 	
		originBm.getPixels(pixels_src, 0, w, 0, 0, w, h);
		mDefaultSeekBarValue = SmoothSkinProcessor.ColorTemperature(pixels_src, pixels_src, size, -1);
		if(mDefaultSeekBarValue<=57)      mDefaultSeekBarValue = (mDefaultSeekBarValue-23)*2;
		else                         mDefaultSeekBarValue = ((mDefaultSeekBarValue-57)/10+34)*2;
 
 	}
	
	public int getDefaultSeekBarValue() {
		return mDefaultSeekBarValue;
	}
	
	
	@Override
	public Bitmap apply(Context cx, int degree) {
		
		Bitmap originBm = mOriginalBitmap;		
		Bitmap performedBm = mOriginalBitmap.copy(mOriginalBitmap.getConfig(), true);
		
		int w = originBm.getWidth();
		int h = originBm.getHeight();
		int[] pixels_src = new int[w * h];
 
		
		int temperature;
		int size = w*h;
	//	LOGW("ColorTemperature 11.18\n");
		if((degree/2)<34) temperature = (degree/2)+23;
		else        
		{
			temperature = ((degree/2)-34)*10+57;
	 
		}
		
 
 
		
		originBm.getPixels(pixels_src, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.ColorTemperature(pixels_src, pixels_src, size, temperature);
		performedBm.setPixels(pixels_src, 0, w, 0, 0, w, h);
 		
		
		return performedBm;
	}

}
