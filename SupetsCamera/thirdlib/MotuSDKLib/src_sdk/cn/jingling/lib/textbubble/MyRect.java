package cn.jingling.lib.textbubble;

import android.graphics.Matrix;

public class MyRect {
	public MyPoint p1;
	public MyPoint p2;
	public MyPoint p3;
	public MyPoint p4;

	public MyRect giveRectAfterTransform(Matrix m) {
		MyRect rect = new MyRect();
		rect.p1 = new MyPoint(p1.givePointAfterTransform(m));
		rect.p2 = new MyPoint(p2.givePointAfterTransform(m));
		rect.p3 = new MyPoint(p3.givePointAfterTransform(m));
		rect.p4 = new MyPoint(p4.givePointAfterTransform(m));
		return rect;
	}
	
}
