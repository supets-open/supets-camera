package cn.jingling.lib.advanceedit.makeup;

import java.io.Serializable;

import android.graphics.Point;

/** 存储眼线(或腮红)贴图的位置参数。包括左右眼线（或腮红）贴图位置（左上角位置）和贴图缩放参数。
*
*/
public class MakeupBitmapPosition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8765179283582135999L;
	
	public MakeupBitmapPosition(Point left, Point right, float scale) {
		mLeftPosition = left;
		mRightPosition = right;
		this.scale = scale;

	}
	
	public Point mLeftPosition = null;
	public Point mRightPosition = null;
	public float scale = 1.0f;
	
}
