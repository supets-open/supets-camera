package cn.jingling.lib.livefilter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.GLImageViewportHelper.ImageType;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;
import cn.jingling.lib.utils.MathUtils;

@SuppressLint("NewApi")
public class CameraRenderer extends AbsCameraRender {

	private FloatBuffer mFrameTexPos, mDisplayedTexPos;
	private ByteBuffer mPixelsYBuffer, mPixelsUVBuffer;
	private int mTextureDataYHandle, mTextureDataUVHandle, mVBOFrameHandle,
			mVBODisplayHandle;
	private FrameBufferInfo mFrameBufferYUV, mFrameBufferA, mFrameBufferB;
	private ShaderInfo mYUVShader;
	private LiveOriginal mLiveOriginal;

	private int mVertexCount;
	// 第一次绘制，DrawYUV时使用的Matrix。它负责矫正Flip、Orientation信息。
	private float[] mMVPMatrix = new float[16];
	// 之后绘制滤镜时，使用的Matrix。已经不用考虑Flip、Orientation信息。
	private float[] mFixMVPMatrix = new float[16];
	
	private boolean mPixelsReady = false;
	private int mImageFormat;

	public CameraRenderer(Context cx, boolean smooth) {
		mAppContext = cx.getApplicationContext();
		mLiveFilters = LiveFilterInfo.generateLiveFilters(cx, smooth);
		mLiveOriginal = new LiveOriginal();
		setFilter("original");
	}

	/**
	 * 调用此函数，默认预览是全屏方式显示
	 * @param viewWidth
	 * @param viewHeight
	 * @param previewWidth
	 * @param previewHeight
	 * @param direction
	 * @param format
	 * @param flip
	 */
	public void init(int viewWidth, int viewHeight, int previewWidth,
			int previewHeight, int direction, int format, boolean flip) {
		init(viewWidth, viewHeight, previewWidth, previewHeight, direction, format, flip, ImageType.FIT_CENTER);
	}
	
	/**
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 * @param previewWidth
	 * @param previewHeight
	 * @param direction
	 * @param format
	 * @param flip
	 * @param isFullScreen 相机预览图是否全屏显示
	 */
	public void init(int viewWidth, int viewHeight, int previewWidth,
			int previewHeight, int direction, int format, boolean flip, ImageType type) {
		mViewHeight = viewHeight;
		mViewWidth = viewWidth;
			
		final int w = previewWidth;
		final int h = previewHeight;
		synchronized (mMutex) {
			mTextureWidth = w;
			mTextureHeight = h;
			if (w > h) {
				mPreviewImageHeight = w;
				mPreviewImageWidth = h;
			} else {
				mPreviewImageHeight = h;
				mPreviewImageWidth = w;
			}
			mImageFormat = format;
			mPixelsYBuffer = ByteBuffer.allocateDirect(w * h);
			mPixelsUVBuffer = ByteBuffer.allocateDirect(w * h / 2);
			mFrameTexPos = getTextureCoordinate(mTextureWidth, mTextureHeight);
			mDisplayedTexPos = getTextureCoordinate(mPreviewImageWidth,
					mPreviewImageHeight);
			initMatrix(direction, flip);
			mImageType = type;
			mNeedUpdateViewPort = true;
	
		}
		Iterator<Entry<String, LiveFilterInfo>> iter = mLiveFilters.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, LiveFilterInfo> entry = iter.next();
			entry.getValue().setup();
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		GLHelper.glCheckError();
		synchronized (mMutex) {
			if (!mPixelsReady) {
				return;
			}
			GLES20.glClearColor(0, 0, 0, 0);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
					| GLES20.GL_DEPTH_BUFFER_BIT);
	
			if (mFilterUpdate) {
//				Debug.startMethodTracing("cameraRenderTrace",7*1024*1024);
//				long start = System.currentTimeMillis();
				glUpdateFilter();
				mFilterUpdate = false;
//				LogUtils.d("xxxx", "time:" + (System.currentTimeMillis() - start));
//				Debug.stopMethodTracing();
			}
			
			if (mNeedUpdateViewPort) {
				mViewportRect = GLImageViewportHelper.getGLViewPort(
						mPreviewImageWidth, mPreviewImageHeight, mViewWidth,
						mViewHeight, mImageType);
				mNeedUpdateViewPort = false;
			}
			
			if ("original".equals(mCurrentLiveFilter.getLabel())) {
				BufferHelper.glBindFrameBuffer(mFrameBufferYUV);
				
				glDrawYUV(mYUVShader, null, mTextureDataYHandle,
						mTextureDataUVHandle);
			} else {
				BufferHelper.glBindFrameBuffer(mFrameBufferYUV);
				BufferHelper.glBindFrameBuffer(mFrameBufferA);
				BufferHelper.glBindFrameBuffer(mFrameBufferB);
				glDrawYUV(mYUVShader, mFrameBufferYUV, mTextureDataYHandle,
						mTextureDataUVHandle);
				FrameBufferInfo fbi = mCurrentLiveFilter.glDraw(mFixMVPMatrix, mVBODisplayHandle,
						mFrameBufferYUV.textureHandle,
						new FrameBufferInfo[] { mFrameBufferA, mFrameBufferB });
				mLiveOriginal.glDraw(mFixMVPMatrix, mVBODisplayHandle, null, mViewportRect, fbi.textureHandle);

			}
			GLES20.glFinish();
			mPixelsReady = false;
		}
		GLHelper.glCheckError();
		mFpsCount++;
		if (mFpsCount >= FPS_INTERVAL) {
			markTime();
			mFpsCount = 0;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		// GLES20.glViewport(0, 0, width, height);
		if(DEBUG) {
			Log.d("xxxx", "onSurfaceChanged width = " + width + ", height = " + height);
		}
		mViewWidth = width;
		mViewHeight = height;
		mNeedUpdateViewPort = true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		mYUVShader = ShaderHelper.glGenerateShader(mAppContext, "vertex_shader",
				"yuv_fragment_shader", "aPosition", "uTextureY", "uTextureUV",
				"uMVPMatrix");
		glInitYUVTextures();
		BufferHelper.glReleaseFrameBuffer(mFrameBufferYUV);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferA);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferB);
		mFrameBufferYUV = BufferHelper.glGenerateFrameBufferWithNoBind(
				mPreviewImageWidth, mPreviewImageHeight);
		mFrameBufferA = BufferHelper.glGenerateFrameBufferWithNoBind(
				mPreviewImageWidth, mPreviewImageHeight);
		mFrameBufferB = BufferHelper.glGenerateFrameBufferWithNoBind(
				mPreviewImageWidth, mPreviewImageHeight);
		glInitVBOs();
		mLiveOriginal.glSetup(mAppContext);
		GLHelper.glCheckError();
	}

	public void setFrame(byte[] frame) {
		if (mPixelsYBuffer == null) {
			return;
		}
		synchronized (mMutex) {
			if (mPixelsReady == true) {
				return;
			}
		}
		mPixelsYBuffer.position(0);
		mPixelsUVBuffer.position(0);

		try {
			int size = mTextureWidth * mTextureHeight;
			mPixelsYBuffer.put(frame, 0, size);
			mPixelsUVBuffer.put(frame, size,
					size * (ImageFormat.getBitsPerPixel(mImageFormat) - 8) / 8);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		mPixelsYBuffer.position(0);
		mPixelsUVBuffer.position(0);
		synchronized (mMutex) {
			mPixelsReady = true;
		}
	}

	private FloatBuffer getTextureCoordinate(int w, int h) {
		float wr = 1.0f * w / MathUtils.nextPowerOfTwo(w);
		float hr = 1.0f * h / MathUtils.nextPowerOfTwo(h);
		float[] frameTexCoords = new float[] { 0, 0, wr, 0, 0, hr, wr, 0, wr,
				hr, 0, hr };
		mVertexCount = frameTexCoords.length;
		FloatBuffer frameTexPos = ByteBuffer
				.allocateDirect(frameTexCoords.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		frameTexPos.put(frameTexCoords).position(0);
		return frameTexPos;
	}

	private void initMatrix(int direction, boolean flip) {
		float[] projectionMatrix = new float[16];
		float[] modelViewMatrix = new float[16];
		initModelViewMatrix(modelViewMatrix, direction, flip);
		initProjectionMatrix(projectionMatrix, direction, flip);
		logMatrix(projectionMatrix, "projectionMatrix");
		logMatrix(modelViewMatrix, "modelViewMatrix");
		Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix,
				0);
		logMatrix(mMVPMatrix, "mMVPMatrix");
		
		float hr = (float) mPreviewImageHeight
				/ MathUtils.nextPowerOfTwo(mPreviewImageHeight);
		float wr = (float) mPreviewImageWidth
				/ MathUtils.nextPowerOfTwo(mPreviewImageWidth);
		float[] fixModelViewMatrix = new float[16];
		Matrix.setLookAtM(fixModelViewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
		float[] fixProjectionMatrix = new float[16];
		Matrix.orthoM(fixProjectionMatrix, 0, 0, wr, 0, hr, 1, -1);
		
		Matrix.multiplyMM(mFixMVPMatrix, 0, fixProjectionMatrix, 0,
				fixModelViewMatrix, 0);
	}

	private void initProjectionMatrix(float[] projectionMatrix, int direction,
			boolean flip) {
		// offset是为了解决边缘绿框问题。
		float offset = 0.005f;
		float xr = offset;
		float yr = offset;
		float wr = (float)(mTextureWidth)/ (float)MathUtils.nextPowerOfTwo(mTextureWidth) - offset;
		float hr = (float)(mTextureHeight)/ (float)MathUtils.nextPowerOfTwo(mTextureHeight)- offset;
		if(DEBUG) {
			Log.d("xxxx", "hr = " + hr + ", wr = " + wr + ", direction = " + direction + ", flip = " + flip);
		}
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
		// Matrix.setIdentityM(mProjectMatrix, 0);
	}

	private void initModelViewMatrix(float[] modelViewMatrix, int direction,
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

	private void glInitYUVTextures() {
		final int[] textureHandle = new int[2];
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glGenTextures(2, textureHandle, 0);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
				MathUtils.nextPowerOfTwo(mTextureWidth),
				MathUtils.nextPowerOfTwo(mTextureHeight), 0,
				GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, null);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[1]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA,
				MathUtils.nextPowerOfTwo(mTextureWidth),
				MathUtils.nextPowerOfTwo(mTextureHeight), 0,
				GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, null);

		mTextureDataYHandle = textureHandle[0];
		mTextureDataUVHandle = textureHandle[1];
	}

	private void glInitVBOs() {
		int[] vbos = new int[2];
		GLES20.glGenBuffers(2, vbos, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbos[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexCount
				* BYTES_PER_FLOAT, mFrameTexPos.position(0),
				GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbos[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexCount
				* BYTES_PER_FLOAT, mDisplayedTexPos.position(0),
				GLES20.GL_STATIC_DRAW);
		mVBOFrameHandle = vbos[0];
		mVBODisplayHandle = vbos[1];
	}

	private void glDrawYUV(ShaderInfo shader, FrameBufferInfo fbi,
			int textureYHandle, int textureUVHandle) {
		GLES20.glUseProgram(shader.program);
		if (fbi == null) {
			GLES20.glViewport(mViewportRect.x, mViewportRect.y, mViewportRect.width, mViewportRect.height);
		} else {
			GLES20.glViewport(0, 0, mPreviewImageWidth, mPreviewImageHeight);
		}
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureYHandle);
		GLES20.glUniform1i(shader.uniforms.get("uTextureY"), 0);
		GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, mTextureWidth,
				mTextureHeight, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
				mPixelsYBuffer.position(0));
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureUVHandle);
		GLES20.glUniform1i(shader.uniforms.get("uTextureUV"), 1);
		GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0,
				mTextureWidth / 2, mTextureHeight / 2,
				GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE,
				mPixelsUVBuffer.position(0));
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOFrameHandle);
		GLES20.glVertexAttribPointer(shader.attribute, POS_DATA_SIZE,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shader.attribute);
		GLES20.glUniformMatrix4fv(shader.uniforms.get("uMVPMatrix"), 1, false,
				mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
	private void logMatrix(float[] matrix, String logName) {
		if(!DEBUG) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<16;i++){
			sb.append(matrix[i]+",");
		}
		Log.d("xxxx", logName + " = " + sb.toString());
	}

}
