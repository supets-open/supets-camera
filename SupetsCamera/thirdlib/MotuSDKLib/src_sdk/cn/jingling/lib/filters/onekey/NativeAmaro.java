package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class NativeAmaro extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		pixels = ImageProcessUtils.saturationPs(pixels, w, h, -15);

		Curve c = new Curve(cx, "curves/amaro.dat");
		CMTProcessor.curveEffect(pixels, c.getCurveRed(), c.getCurveGreen(),
				c.getCurveBlue(), w, h);
		CMTProcessor.colorBurn(pixels, Layer.getLayerPixels(cx,
				"layers/amaro1", w, h, Layer.Type.ROTATABLE, 60 * 255 / 100), w, h);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		
		Canvas canvas = new Canvas(bm);
		Paint p = new Paint();
//		p.setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
		canvas.drawBitmap(Layer.getLayerImage(cx, "layers/amaro2", w, h, Layer.Type.ROTATABLE, 20 * 255 / 100), new Matrix(), p);
		
		pixels = null;
		return bm;
	}

}
