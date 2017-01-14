package cn.jingling.lib.livefilter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;

/**
 * 在使用CameraMtkSurfaceview辅助类
 * @Copyright(C)2013,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: GlDrawHelper.java Created On 2013-11-28
 * @Author:zhuchen
 * @Description:TODO
 * 
 * @Version:1.0
 * @Update:
 */
public class GLDrawHelper {

	/**
	 * 绘制原图预览 无特别原因 勿修改此函数
	 */
	public void glDrawPreview(float[] matrix, ShaderInfo shader,
			FrameBufferInfo fbi, int textureHandle, int vboHandler,
			int mPreviewImageWidth, int mPreviewImageHeight,
			ViewportRect mViewportRect) {
		GLES20.glUseProgram(shader.program);
		GLHelper.glCheckError();
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLHelper.glCheckError();

		if (fbi == null) {
			GLES20.glViewport(mViewportRect.x, mViewportRect.y, mViewportRect.width, mViewportRect.height);
		} else {
			GLES20.glViewport(0, 0, mPreviewImageWidth, mPreviewImageHeight);
		}

		GLHelper.glCheckError();
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLHelper.glCheckError();
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle);
		GLHelper.glCheckError();
		GLES20.glUniform1i(shader.uniforms.get("uTexture"), 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandler);
		GLHelper.glCheckError();
		GLES20.glVertexAttribPointer(shader.attribute,
				CameraRenderInteface.POS_DATA_SIZE, GLES20.GL_FLOAT, false, 0,
				0);
		GLHelper.glCheckError();
		GLES20.glEnableVertexAttribArray(shader.attribute);
		GLHelper.glCheckError();
		GLES20.glUniformMatrix4fv(shader.uniforms.get("uMVPMatrix"), 1, false,
				matrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
	/**
	 * 初始化正交投影矩阵
	 * @param projectionMatrix
	 * @param direction
	 * @param flip
	 * @param textureWidth
	 * @param textureHeight
	 * @param viewWidth
	 * @param viewHeight
	 */
	public void initProjectionMatrix(float[] projectionMatrix, int direction,
			boolean flip, int textureWidth, int textureHeight, int viewWidth, int viewHeight) {

		// offset是为了解决边缘绿框问题。
		float offset = 0.005f;
		float xr = offset;
		float yr = offset;
		// 不用2N次方优化，是因为MTKRender使用了OES—Texture。只能是Preview多大，Texture就多大。
		float wr = 1.0f - offset;
		float hr = 1.0f - offset;
	
		switch (direction) {
		case 0:
			if (flip) {
				Matrix.orthoM(projectionMatrix, 0, yr, hr, -wr, -xr, -10, 20);
			} else {
				Matrix.orthoM(projectionMatrix, 0, -hr, -yr, -wr, -xr, -10, 20);
			}
			break;
		case 1:
			if (flip) {
				Matrix.orthoM(projectionMatrix, 0, yr, hr, xr, wr, -10, 20);
			} else {
				Matrix.orthoM(projectionMatrix, 0, -hr, -yr, xr, wr, -10, 20);
			}
			break;
		case 2:
			if (flip) {
				Matrix.orthoM(projectionMatrix, 0, -hr, -yr, xr, wr, -1, 1);
			} else {
				Matrix.orthoM(projectionMatrix, 0, yr, hr, xr, wr, -10, 20);
			}
			break;
		case 3:
			if (flip) {
				Matrix.orthoM(projectionMatrix, 0, -hr, -yr, -wr, -xr, -10, 20);
			} else {
				Matrix.orthoM(projectionMatrix, 0, yr, hr, -wr, -xr, -10, 20);
			}
			break;
		}
	
	}

	/**
	 * 初始化MODELVIEW矩阵
	 * @param modelViewMatrix
	 * @param direction
	 * @param flip
	 */
	public void initModelViewMatrix(float[] modelViewMatrix, int direction,
			boolean flip) {
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		float eyeZ = -1.0f;
		if (flip) {
			eyeZ = 1.0f;
		} else {
			eyeZ = -1.0f;
		}

		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = 0.0f;

		float upX = -1.0f;
		float upY = 0.0f;
		final float upZ = 0.0f;

		switch (direction) {
		case 0:
			upX = -1.0f;
			upY = 0.0f;
			break;
		case 1:
			upX = 0.0f;
			upY = 1.0f;
			break;
		case 2:
			upX = 1.0f;
			upY = 0.0f;
			break;
		case 3:
			upX = 0.0f;
			upY = -1.0f;
			break;
		}

		Matrix.setLookAtM(modelViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY,
				lookZ, upX, upY, upZ);
	}
	
	/**
	 * 初始化一块纹理
	 * @param textureHandle
	 */
	public void glInitTextures(int[] textureHandle) {
		GLES20.glGenTextures(1, textureHandle, 0);

		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	}
	
}
