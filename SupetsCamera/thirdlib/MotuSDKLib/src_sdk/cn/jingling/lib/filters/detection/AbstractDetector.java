package cn.jingling.lib.filters.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

abstract public class AbstractDetector {
	abstract public void init(Context cx);

	abstract public Point[] detect(Bitmap bm);

	abstract public void release();

	public void drawBitmap(Bitmap bm, Point[] points) {
		Canvas c = new Canvas(bm);
		Paint p = new Paint();
		p.setColor(Color.GREEN);
		int size = points.length;
		for (int i = 0; i < size; i++) {
			c.drawCircle(points[i].x, points[i].y, 2, p);
		}
	}

}
