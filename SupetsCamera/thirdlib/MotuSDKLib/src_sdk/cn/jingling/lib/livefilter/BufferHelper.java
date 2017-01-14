package cn.jingling.lib.livefilter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import cn.jingling.lib.utils.MathUtils;

public class BufferHelper {

	/**
	 * This method will generate and bind a frame buffer.
	 * 
	 * @param width
	 *            width of the framebuffer
	 * @param height
	 *            height of the framebuffer
	 * @return A FrameBufferInfo object
	 */
	public static FrameBufferInfo glGenerateFrameBuffer(int width, int height) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int frameBufferTexture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
				MathUtils.nextPowerOfTwo(width),
				MathUtils.nextPowerOfTwo(height), 0, GLES20.GL_RGBA,
				GLES20.GL_UNSIGNED_BYTE, null);

		int[] frameBuffers = new int[1];
		GLES20.glGenFramebuffers(1, frameBuffers, 0);
		int frameBufferHandle = frameBuffers[0];

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				frameBufferTexture, 0);

		return new FrameBufferInfo(frameBufferHandle, frameBufferTexture);
	}

	/**
	 * This method will generate but not bind a framebuffer.
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static FrameBufferInfo glGenerateFrameBufferWithNoBind(int width,
			int height) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int frameBufferTexture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
				MathUtils.nextPowerOfTwo(width),
				MathUtils.nextPowerOfTwo(height), 0, GLES20.GL_RGBA,
				GLES20.GL_UNSIGNED_BYTE, null);

		int[] frameBuffers = new int[1];
		GLES20.glGenFramebuffers(1, frameBuffers, 0);
		int frameBufferHandle = frameBuffers[0];

		return new FrameBufferInfo(frameBufferHandle, frameBufferTexture);
	}

	/**
	 * Just bind a frame buffer with texture.
	 * 
	 * @param fbinfo
	 */
	public static void glBindFrameBuffer(FrameBufferInfo fbinfo) {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,
				fbinfo.frameBufferHandle);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				fbinfo.textureHandle, 0);
	}

	/**
	 * Generate but not bind an OES framebuffer
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static FrameBufferInfo glGenerateFrameBufferOES(int width, int height) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int frameBufferTexture = textures[0];
		int target = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
		GLES20.glBindTexture(target, frameBufferTexture);
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MIN_FILTER,
				GLES20.GL_NEAREST);
		GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_LINEAR);

		int[] frameBuffers = new int[1];
		GLES20.glGenFramebuffers(1, frameBuffers, 0);
		int frameBufferHandle = frameBuffers[0];

		return new FrameBufferInfo(frameBufferHandle, frameBufferTexture);
	}

	/**
	 * Release the framebuffer and delete the texture.
	 * 
	 * @param fbi
	 */
	public static void glReleaseFrameBuffer(FrameBufferInfo fbi) {

		if (fbi != null) {
			GLES20.glDeleteFramebuffers(1, new int[] { fbi.frameBufferHandle },
					0);
			GLES20.glDeleteTextures(1, new int[] { fbi.textureHandle }, 0);
		}
	}

	public static void glDrawFrameBufferOnScreen(FrameBufferInfo fbi,
			Matrix mvpMatrix) {

	}

	/**
	 * This class is a data structure for storing the FBO, including the FBO's
	 * handle and related texture handle.
	 * 
	 * @author jiankun.zhi
	 * 
	 */
	public static class FrameBufferInfo {
		public int frameBufferHandle;
		public int textureHandle;

		public FrameBufferInfo(int frameBuffer, int texture) {
			frameBufferHandle = frameBuffer;
			textureHandle = texture;

		}

	}

}
