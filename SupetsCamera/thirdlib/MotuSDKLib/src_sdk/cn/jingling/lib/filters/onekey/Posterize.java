package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class Posterize extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		// TODO Auto-generated method stub
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
//		int[] model = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
	
//		Bitmap tmpBm = Layer.getLayerImage(cx, "layers/canvas_brush_stroke", w, h, Layer.Type.NORMAL, 100, true);
//		tmpBm.getPixels(model, 0, w, 0, 0, w, h);
		
//		model = Layer.getLayerPixels(cx, "layers/canvas_canvas3", w, h, Layer.Type.NORMAL, 100);
		//加一个图层上去
//		SmoothSkinProcessor.multiColorDone(pixels, model, w, h, 3);
		CMTProcessor.Posterize(pixels, w, h, 5);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
