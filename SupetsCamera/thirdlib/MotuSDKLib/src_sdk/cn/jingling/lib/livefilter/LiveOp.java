package cn.jingling.lib.livefilter;

import android.content.Context;
import android.graphics.Point;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;

abstract public class LiveOp {
	
	// 原图宽高。FBO的Texture宽高是根据mImageSize做nextPowerof2而得来的。
	protected Point mImageSize;

	/**
	 * Only called once, in onSurfaceCreated. This will create gl shader and program.
	 * 
	 * @param cx
	 */
	abstract public void glSetup(Context cx);

	/**
	 * Called when switch filters. This will load the curve or layer textures.
	 * 
	 * @param cx
	 * @param fboImageSize 原图宽高。FBO的Texture宽高是根据fboImageSize做nextPowerof2而得来的。
	 * @param viewportRect 显示在屏幕时，使用的ViewPort宽高
	 */
	public void glUpdate(Context cx, Point fboImageSize) {
		mImageSize = fboImageSize;
	}
	
	/**
	 * called in onDrawFrame
	 * 
	 * @param mvpMatrix
	 * @param vboHandle
	 * @param fbi output frame buffer, to store the output data.
	 * @param posDataSize
	 * @param textureHandle input texture to be processed
	 */
	abstract public void glDraw(float[] mvpMatrix,
			int vboHandle, FrameBufferInfo fbi, int posDataSize, int textureHandle);

	/**
	 * Called when switch filters. This will release the curve or layer textures you loaded when glUpdate.
	 */
	abstract public void glRelease();
	
	/**
	 * 
	 * @param cx
	 * @param fboImageSize
	 * @param needFlipLayer
	 */
	public void prepareBmForTexture(Context cx, Point fboImageSize) {
		
	}
}
