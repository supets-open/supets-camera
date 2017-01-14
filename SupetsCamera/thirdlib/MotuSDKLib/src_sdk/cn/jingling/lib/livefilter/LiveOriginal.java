package cn.jingling.lib.livefilter;

import android.content.Context;
import android.opengl.GLES20;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;

public class LiveOriginal {

	private ShaderInfo mShader;

	/**
	 * Setup related shader.
	 * @param cx context
	 */
	public void glSetup(Context cx) {
		mShader = ShaderHelper.glGenerateShader(cx, "vertex_shader",
				"empty_fragment_shader", "aPosition", "uMVPMatrix",
				"uTexture");
	}
	
	/**
	 * Draw the texture with the specified matrix.
	 * 
	 * @param mvpMatrix mvp matrix
	 * @param vboHandle Vertex Buffer Object for rendering
	 * @param fbi Frame Buffer Object to draw or null for drawing on screen surface
	 * @param viewportRect View port for displaying the surface.
	 * @param textureHandle texture to draw
	 */
	public void glDraw(float[] mvpMatrix, int vboHandle, FrameBufferInfo fbi,
			ViewportRect viewportRect, int textureHandle) {
		GLES20.glUseProgram(mShader.program);
		GLES20.glViewport(viewportRect.x, viewportRect.y, viewportRect.width, viewportRect.height);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(mShader.uniforms.get("uTexture"), 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandle);
		GLES20.glVertexAttribPointer(mShader.attribute, CameraRenderInteface.POS_DATA_SIZE,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(mShader.attribute);
		GLES20.glUniformMatrix4fv(mShader.uniforms.get("uMVPMatrix"), 1, false,
				mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}

}
