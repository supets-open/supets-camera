package cn.jingling.lib.livefilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.opengl.GLES20;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;

public class LiveHopeEffect extends LiveOp {

	private ShaderInfo mHopeEffectShader;

	private int mTextureLayerHandle;

	@Override
	public void glSetup(Context cx) {
		mHopeEffectShader = ShaderHelper.glGenerateShader(cx, "vertex_shader",
				"hope_effect_fragment_shader", "aPosition", "uMVPMatrix",
				"uTexture", "uTextureLayer");
	}

	@Override
	public void glUpdate(Context cx, Point fboImageSize) {
		// TODO Auto-generated method stub
		super.glUpdate(cx, fboImageSize);

		Bitmap textureBitmap = createTextureBitmap(fboImageSize.x,
				fboImageSize.y);
		mTextureLayerHandle = TextureHelper.loadSubTexture(textureBitmap);
	}

	private Bitmap createTextureBitmap(int w, int h) {
		Bitmap textureBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		int[] texPixels = new int[w * h];
		textureBitmap.getPixels(texPixels, 0, w, 0, 0, w, h);
		CMTProcessor.setHopeEffectTexturePixels(texPixels, w, h);
		textureBitmap.setPixels(texPixels, 0, w, 0, 0, w, h);
		return textureBitmap;
	}

	@Override
	public void glDraw(float[] mvpMatrix, int vboHandle, FrameBufferInfo fbi,
			int posDataSize, int textureHandle) {
		drawHopeEffect(mHopeEffectShader, mvpMatrix, vboHandle, fbi,
				posDataSize, textureHandle, mTextureLayerHandle);

	}

	@Override
	public void glRelease() {
		GLES20.glDeleteTextures(1, new int[] { mTextureLayerHandle }, 0);
		mTextureLayerHandle = -1;

	}

	private void drawHopeEffect(ShaderInfo shader, float[] mvpMatrix,
			int vboHandle, FrameBufferInfo fbi, int posDataSize,
			int textureHandle, int textureLayerHandle) {
		GLES20.glUseProgram(shader.program);
		GLES20.glViewport(0, 0, mImageSize.x, mImageSize.y);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(shader.uniforms.get("uTexture"), 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureLayerHandle);
		GLES20.glUniform1i(shader.uniforms.get("uTextureLayer"), 1);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandle);
		GLES20.glVertexAttribPointer(shader.attribute, posDataSize,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shader.attribute);
		GLES20.glUniformMatrix4fv(shader.uniforms.get("uMVPMatrix"), 1, false,
				mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}

}
