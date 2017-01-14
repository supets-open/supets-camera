package cn.jingling.lib.livefilter;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;

public class LiveCurve extends LiveOp {
	private String mCurve;
	private ShaderInfo mShader;
	private int mCurveHandle;

	public LiveCurve(String curve) {
		this.mCurve = curve;
	}

	@Override
	public void glSetup(Context cx) {
		mShader = ShaderHelper.glGenerateShader(cx, "vertex_shader",
				"curve_fragment_shader", "aPosition", "uMVPMatrix", "uTexture",
				"uTextureCurve");

	}

	@Override
	public void glUpdate(Context cx, Point fboImageSize) {
		super.glUpdate(cx, fboImageSize);
		mCurveHandle = TextureHelper.loadCurveTexture(cx, mCurve);
	}

	@Override
	public void glDraw(float[] mvpMatrix, int vboHandle, FrameBufferInfo fbi,
			int posDataSize, int textureHandle) {
		GLES20.glUseProgram(mShader.program);
		GLES20.glViewport(0, 0, mImageSize.x, mImageSize.y);

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(mShader.uniforms.get("uTexture"), 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCurveHandle);
		GLES20.glUniform1i(mShader.uniforms.get("uTextureCurve"), 1);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandle);
		GLES20.glVertexAttribPointer(mShader.attribute, posDataSize,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(mShader.attribute);
		GLES20.glUniformMatrix4fv(mShader.uniforms.get("uMVPMatrix"), 1, false,
				mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

	}

	@Override
	public void glRelease() {
		GLES20.glDeleteTextures(1, new int[] { mCurveHandle }, 0);
		mCurveHandle = -1;

	}
}
