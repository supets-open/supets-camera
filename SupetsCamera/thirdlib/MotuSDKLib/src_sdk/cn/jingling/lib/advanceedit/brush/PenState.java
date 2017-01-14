package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @Copyright(C)2012,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: PenState.java Created On 2012-10-16
 * @Author:yangding
 * @Description:最基本的硬笔
 * 
 */
public class PenState extends DrawState{
	/** 线性画笔的一种。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public PenState(Bitmap pathBitmap)
	{
		super(pathBitmap);
	}

}
