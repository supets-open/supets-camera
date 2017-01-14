package cn.jingling.lib.livefilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES20;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;
import cn.jingling.lib.utils.MathUtils;

public class LiveColorPencil extends LiveOp {

	private ShaderInfo mShaderKrisch, mShaderPosterize,
			mShaderLevelsCompression, mOverlayShader, mPencilOverlayShader;

	private FrameBufferInfo mFrameBufferTemplateEdg, mFrameBufferSrcHolderA,
			mFrameBufferSrcHolderB;

	private int mTextureBrushStrokeHandle1, mTextureBrushStrokeHandle2;

	@Override
	public void glSetup(Context cx) {
		// TODO Auto-generated method stub
		mShaderKrisch = ShaderHelper.glGenerateShader(cx,
				"kirsch_vertex_shader", "kirsch_fragment_shader", "aPosition",
				"uMVPMatrix", "texelWidth", "texelHeight", "uTexture");

		mShaderPosterize = ShaderHelper.glGenerateShader(cx, "vertex_shader",
				"posterize_fragment_shader", "aPosition", "uMVPMatrix",
				"uLevels", "uTexture");

		mShaderLevelsCompression = ShaderHelper.glGenerateShader(cx,
				"vertex_shader", "levels_compression_fragment_shader",
				"aPosition", "uMVPMatrix", "uLowEdge", "uHighEdge", "uTexture");

		mOverlayShader = ShaderHelper.glGenerateShader(cx, "vertex_shader",
				"overlay_fragment_shader", "aPosition", "uMVPMatrix",
				"uTexture", "uTextureLayer", "uLayerWeight");

		mPencilOverlayShader = ShaderHelper.glGenerateShader(cx,
				"vertex_shader", "pencil_overlay_fragment_shader", "aPosition",
				"uMVPMatrix", "uTexture", "uTextureLayer", "uLayerWeight");
	}

	@Override
	public void glUpdate(Context cx, Point fboImageSize) {
		// TODO Auto-generated method stub
		super.glUpdate(cx, fboImageSize);
		mFrameBufferTemplateEdg = BufferHelper.glGenerateFrameBuffer(
				fboImageSize.x, fboImageSize.y);
		mFrameBufferSrcHolderA = BufferHelper.glGenerateFrameBuffer(
				fboImageSize.x, fboImageSize.y);
		mFrameBufferSrcHolderB = BufferHelper.glGenerateFrameBuffer(
				fboImageSize.x, fboImageSize.y);

		Bitmap bm1 = Layer.getLayerImage(cx, "layers/newpencilstroke",
				fboImageSize.x, fboImageSize.y, Layer.Type.NORMAL);
		mTextureBrushStrokeHandle1 = TextureHelper.loadSubTexture(bm1);

		Bitmap bm2 = Layer.getLayerImage(cx, "layers/canvas_paper7",
				fboImageSize.x, fboImageSize.y, Layer.Type.NORMAL);
		mTextureBrushStrokeHandle2 = TextureHelper.loadSubTexture(bm2);
	}

	@Override
	public void glDraw(float[] mvpMatrix, int vboHandle, FrameBufferInfo fbi,
			int posDataSize, int textureHandle) {

		drawKirsch(mShaderKrisch, mvpMatrix, vboHandle,
				mFrameBufferTemplateEdg, posDataSize, textureHandle);
		drawPosterize(mShaderPosterize, mvpMatrix, vboHandle,
				mFrameBufferSrcHolderA, posDataSize, textureHandle, 12);
		drawLevelsCompression(mShaderLevelsCompression, mvpMatrix, vboHandle,
				mFrameBufferSrcHolderB, posDataSize,
				mFrameBufferSrcHolderA.textureHandle, 50 / 255f, 180 / 255f);
		drawLayer(mOverlayShader, mvpMatrix, vboHandle, mFrameBufferSrcHolderA,
				posDataSize, mFrameBufferSrcHolderB.textureHandle,
				mFrameBufferTemplateEdg.textureHandle, 0.7f);

		drawLayer(mPencilOverlayShader, mvpMatrix, vboHandle,
				mFrameBufferSrcHolderB, posDataSize,
				mFrameBufferSrcHolderA.textureHandle,
				mTextureBrushStrokeHandle1, 1.0f);
		drawLayer(mOverlayShader, mvpMatrix, vboHandle, fbi, posDataSize,
				mFrameBufferSrcHolderB.textureHandle,
				mTextureBrushStrokeHandle2, 0.5f);
	}

	@Override
	public void glRelease() {
		BufferHelper.glReleaseFrameBuffer(mFrameBufferTemplateEdg);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferSrcHolderA);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferSrcHolderB);
		mFrameBufferTemplateEdg = null;
		mFrameBufferSrcHolderA = null;
		mFrameBufferSrcHolderB = null;

		GLES20.glDeleteTextures(1, new int[] { mTextureBrushStrokeHandle1 }, 0);
		mTextureBrushStrokeHandle1 = -1;

		GLES20.glDeleteTextures(1, new int[] { mTextureBrushStrokeHandle2 }, 0);
		mTextureBrushStrokeHandle2 = -1;
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

	private void drawPosterize(ShaderInfo shader, float[] mvpMatrix,
			int vboHandle, FrameBufferInfo fbi, int posDataSize,
			int textureHandle, float uLevels) {
		GLES20.glUseProgram(shader.program);
		GLES20.glViewport(0, 0, mImageSize.x, mImageSize.y);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(shader.uniforms.get("uTexture"), 0);
		GLES20.glUniform1f(shader.uniforms.get("uLevels"), uLevels);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandle);
		GLES20.glVertexAttribPointer(shader.attribute, posDataSize,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shader.attribute);

		GLES20.glUniformMatrix4fv(shader.uniforms.get("uMVPMatrix"), 1, false,
				mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}

	private void drawLevelsCompression(ShaderInfo shader, float[] mvpMatrix,
			int vboHandle, FrameBufferInfo fbi, int posDataSize,
			int textureHandle, float lowEdge, float highEdge) {
		GLES20.glUseProgram(shader.program);
		GLES20.glViewport(0, 0, mImageSize.x, mImageSize.y);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(shader.uniforms.get("uTexture"), 0);
		GLES20.glUniform1f(shader.uniforms.get("uLowEdge"), lowEdge);
		GLES20.glUniform1f(shader.uniforms.get("uHighEdge"), highEdge);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandle);
		GLES20.glVertexAttribPointer(shader.attribute, posDataSize,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shader.attribute);

		GLES20.glUniformMatrix4fv(shader.uniforms.get("uMVPMatrix"), 1, false,
				mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}

	private void drawLayer(ShaderInfo shader, float[] mvpMatrix, int vboHandle,
			FrameBufferInfo fbi, int posDataSize, int textureHandle,
			int textureLayerHandle, float layerWeight) {
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
		GLES20.glUniform1f(shader.uniforms.get("uLayerWeight"), layerWeight);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandle);
		GLES20.glVertexAttribPointer(shader.attribute, posDataSize,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shader.attribute);
		GLES20.glUniformMatrix4fv(shader.uniforms.get("uMVPMatrix"), 1, false,
				mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}

}
