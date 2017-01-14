package cn.jingling.lib.advanceedit.brush;

import android.graphics.Paint;

public class MyPaint extends Paint{
	public MyPaint()
	{
		super();
		this.setAntiAlias(true);
		this.setFilterBitmap(true);
		this.setDither(true);
		this.setSubpixelText(true);
	}
	
	public MyPaint(MyPaint myPaint) {
		super(myPaint);
	}

}
