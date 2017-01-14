package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class LipStick extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		// TODO Auto-generated method stub
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		int[] color  = new int[w * h];
		int[] points = new int[12];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		
		Bitmap tmpBm = Layer.getLayerImage(cx, "layers/lipcolor1", w, h, Layer.Type.NORMAL, 100, true);
		tmpBm.getPixels(color, 0, w, 0, 0, w, h);
		
		points[0] = 141; points[1] = 135;
		points[2] = 385; points[3] = 110;
		points[4] = 229; points[5] = 124;
		points[6] = 246; points[7] = 230;
		points[8] = 234; points[9] = 145;
		points[10]= 241; points[11]= 183;
		
		 
		CMTProcessor.lipstick(pixels, color, w, h,points);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
