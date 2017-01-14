package cn.jingling.lib.livefilter;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;
import cn.jingling.lib.utils.MathUtils;

public class LiveKirsch extends LiveOp {

	private ShaderInfo mShaderKrisch;

	@Override
	public void glSetup(Context cx) {
		// TODO Auto-generated method stub
		mShaderKrisch = ShaderHelper.glGenerateShader(cx,
				"kirsch_vertex_shader", "kirsch_fragment_shader", "aPosition",
				"uMVPMatrix", "texelWidth", "texelHeight", "uTexture");
	}

	@Override
	public void glUpdate(Context cx, Point fboImageSize) {
		super.glUpdate(cx, fboImageSize);
	}

	/*
	 * public void glUpdate(Context cx, Point fboImageSize, ViewportRect
	 * viewportRect) { // TODO Auto-generated method stub super.glUpdate(cx,
	 * fboImageSize, viewportRect); Bitmap bm = Layer.getLayerImage(cx, path,
	 * fboImageSize.x, fboImageSize.y, Layer.Type.NORMAL); mTextureHandle =
	 * TextureHelper.loadSubTexture(bm); }
	 */

	@Override
	public void glDraw(float[] mvpMatrix, int vboHandle, FrameBufferInfo fbi,
			int posDataSize, int textureHandle) {

		drawKirsch(mShaderKrisch, mvpMatrix, vboHandle, fbi, posDataSize,
				textureHandle);

	}

	@Override
	public void glRelease() {

	}

	private void drawKirsch(ShaderInfo shader, float[] mvpMatrix,
			int vboHandle, FrameBufferInfo fbi, int posDataSize,
			int textureHandle) {
		GLES20.glUseProgram(shader.program);
		GLES20.glViewport(0, 0, mImageSize.x, mImageSize.y);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(shader.uniforms.get("uTexture"), 0);
		GLES20.glUniform1f(shader.uniforms.get("texelWidth"),
				1f / MathUtils.nextPowerOfTwo(mImageSize.x));
		GLES20.glUniform1f(shader.uniforms.get("texelHeight"),
				1f / MathUtils.nextPowerOfTwo(mImageSize.y));

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandle);
		GLES20.glVertexAttribPointer(shader.attribute, posDataSize,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shader.attribute);

		GLES20.glUniformMatrix4fv(shader.uniforms.get("uMVPMatrix"), 1, false,
				mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}

}
