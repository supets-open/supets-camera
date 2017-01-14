package cn.jingling.lib.livefilter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES20;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;

/**
 * @author jiankun.zhi
 * 
 */
public class LiveLayer extends LiveOp {
	public static enum Type {
		NONE, MULTIPLY, LINEAR_BURN, OVERLAY, SCREEN, DARKEN, COVERAGE, SOFTLIGHT
	}

	private static final Map<Type, String> TYPE_FRAGMENTS = new HashMap<Type, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3216412589715917810L;

		{
			put(Type.LINEAR_BURN, "linearburn_fragment_shader");
			put(Type.MULTIPLY, "multiply_fragment_shader");
			put(Type.OVERLAY, "overlay_fragment_shader");
			put(Type.SCREEN, "screen_fragment_shader");
			put(Type.DARKEN, "darken_fragment_shader");
			put(Type.COVERAGE, "coverage_fragment_shader");
			put(Type.SOFTLIGHT, "softlight_fragment_shader");
		}
	};

	public String path;
	public Type type;
	public float weight; // default is 1.0f
	private int mTextureHandle;
	private ShaderInfo mShader;
	private Bitmap mUpdateBm;

	public LiveLayer(String path, Type type, float weight) {
		this.path = path;
		this.type = type;
		this.weight = weight;
	}

	public LiveLayer(String path, Type type) {
		this(path, type, 1.0f);
	}

	@Override
	public void glSetup(Context cx) {
		// TODO Auto-generated method stub
		mShader = ShaderHelper.glGenerateShader(cx, "vertex_shader",
				TYPE_FRAGMENTS.get(type), "aPosition", "uMVPMatrix",
				"uTexture", "uTextureLayer", "uLayerWeight");
	}

	/*
	 * public void glUpdate(Context cx, Point fboImageSize, ViewportRect
	 * viewportRect) { // TODO Auto-generated method stub super.glUpdate(cx,
	 * fboImageSize, viewportRect); Bitmap bm = Layer.getLayerImage(cx, path,
	 * fboImageSize.x, fboImageSize.y, Layer.Type.NORMAL); mTextureHandle =
	 * TextureHelper.loadSubTexture(bm); }
	 */

	@Override
	public void glUpdate(Context cx, Point fboImageSize) {
		super.glUpdate(cx, fboImageSize);
		
		boolean needUpdateBm = false;
		if (mUpdateBm == null || mUpdateBm.isRecycled()
				|| mUpdateBm.getWidth() != fboImageSize.x
				|| mUpdateBm.getHeight() != fboImageSize.y) {
			needUpdateBm = true;
		}
		
		if (needUpdateBm) {
			prepareBmForTexture(cx, fboImageSize);
		}

		mTextureHandle = TextureHelper.loadSubTexture(mUpdateBm);
		mUpdateBm = null;
	}

	@Override
	public void prepareBmForTexture(Context cx, Point fboImageSize) {
		Bitmap bm = Layer.getLayerImage(cx, path, fboImageSize.x,
				fboImageSize.y, Layer.Type.NORMAL);
		bm = ImageProcessUtils.flip(bm, false);
		if (mUpdateBm != null && !mUpdateBm.isRecycled()) {
			mUpdateBm.recycle();
			mUpdateBm = null;
		}

		mUpdateBm = bm;
	}

	@Override
	public void glDraw(float[] mvpMatrix, int vboHandle, FrameBufferInfo fbi,
			int posDataSize, int textureHandle) {
		// TODO Auto-generated method stub
		GLES20.glUseProgram(mShader.program);
		GLES20.glViewport(0, 0, mImageSize.x, mImageSize.y);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(mShader.uniforms.get("uTexture"), 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
		GLES20.glUniform1i(mShader.uniforms.get("uTextureLayer"), 1);
		GLES20.glUniform1f(mShader.uniforms.get("uLayerWeight"), weight);
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
		// TODO Auto-generated method stub
		GLES20.glDeleteTextures(1, new int[] { mTextureHandle }, 0);
		mTextureHandle = -1;
		if(mUpdateBm != null && !mUpdateBm.isRecycled()) {
			mUpdateBm.recycle();
			mUpdateBm = null;
		}
	}
}
