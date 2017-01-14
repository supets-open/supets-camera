package cn.jingling.lib.livefilter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.utils.LogUtils;
import cn.jingling.lib.utils.MathUtils;

public class GLStaticFBORender {
	
	private final int POS_DATA_SIZE = 2;
	
	private int GLHandle_VBO_FrameTexPos = 0;
	private int GLHandle_Texture_OriginalBm = 0;
	
	// 对应OriginalBmTexture的数据。会作为Texture提供给渲染管线。
	//private ByteBuffer mOriginalPixelsBuffer;
	// 对应PerformedBmTexture的数据。会绑定到最终输出FBO。
	private IntBuffer mPerformedPixelsBuffer;
	private Bitmap mPerformedBitmap;
	
	// 顶点数据。在我们的渲染管线里，这个数据既作为vertex的顶点坐标，同时也作为Texture贴图坐标。
	private FloatBuffer mFrameTexPos;
	
	// 在一次渲染过程中，会涉及到多次管线渲染叠加。FBO A、B依次作为输入、输出，完成渲染管线的多次叠加。
	private FrameBufferInfo mFrameBufferA, mFrameBufferB;
	
	// 所有的Filter信息。
	private Map<String, LiveFilterInfo> mLiveFilters;
	// 当前使用的Filter。
	private LiveFilterInfo mCurrentLiveFilter;
	
	private Context mAppContext;
	
	private float[] mMVPMatrix = new float[16];//总投影变换矩阵
	
	// 原图宽、高
	private int mImageWidth, mImageHeight;

	
	/** 初始化Render。
	 * @param cx
	 */
	public void glInitRender(Context cx) {
		mAppContext =cx;
		mLiveFilters = LiveFilterInfo.generateLiveFilters(cx, true);
	}
	
	
	/** 切换图片时调用。根据图片重新设置openGL相关参数。
	 * @param originalBm
	 */
	public void glSetBitmap(Bitmap originalBm) throws OutOfMemoryError {
		
		mImageWidth = originalBm.getWidth();
		mImageHeight = originalBm.getHeight();

		mPerformedPixelsBuffer = IntBuffer.allocate(mImageWidth*mImageHeight);
		
		mPerformedBitmap = null;

		mPerformedBitmap = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
		
		if (mPerformedBitmap == null) {
			throw new OutOfMemoryError();
		}
		
		initMatrix();
		
		// 初始化FBO
		BufferHelper.glReleaseFrameBuffer(mFrameBufferA);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferB);
		mFrameBufferA = BufferHelper.glGenerateFrameBuffer(
				mImageWidth, mImageHeight);
		mFrameBufferB = BufferHelper.glGenerateFrameBuffer(
				mImageWidth, mImageHeight);
		
		// 原始图片数据存入Texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		if (GLHandle_Texture_OriginalBm != 0) {
			GLES20.glDeleteTextures(1, new int[] {GLHandle_Texture_OriginalBm}, 0);
		}
		Bitmap textureBm = originalBm.copy(originalBm.getConfig(), true);
		if (textureBm == null) {
			throw new OutOfMemoryError();
		}
		
		GLHandle_Texture_OriginalBm = TextureHelper.loadSubTexture(textureBm);
		
		// 生成图片顶点坐标、Texture贴图坐标并存入VBO
		initVBO();

	}
	
	public Bitmap glDrawFrame(String filterLabel) {
		if (!mLiveFilters.containsKey(filterLabel)) {
			throw new RuntimeException("Filter label " + filterLabel
					+ " does not exsit in LiveFilterInfo!");
		}
		long time = System.currentTimeMillis();
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
				| GLES20.GL_DEPTH_BUFFER_BIT);
		
		if (mCurrentLiveFilter != null) {
			mCurrentLiveFilter.glRelease();
			mCurrentLiveFilter = null;
		}
		
		LogUtils.d("GLStatic", "gl time consume1: " + (System.currentTimeMillis() - time));
		
		mCurrentLiveFilter = mLiveFilters.get(filterLabel);
		// 第一次调用某滤镜的glUpdate时，会setup，会对其所有的Ops实例进行setup，会比较卡。
		// 以后调用就只是加载相应的texture。
		// 调用release时，将其用到的texture删掉。
		mCurrentLiveFilter.glUpdate(mAppContext, new Point(mImageWidth,
				mImageHeight));
		
		LogUtils.d("GLStatic", "gl time consume2: " + (System.currentTimeMillis() - time));
		
		FrameBufferInfo[] frameBufferSwap = new FrameBufferInfo[] { mFrameBufferA, mFrameBufferB };
		FrameBufferInfo resultFbo = mCurrentLiveFilter.glDraw(mMVPMatrix, GLHandle_VBO_FrameTexPos,
				GLHandle_Texture_OriginalBm,
				frameBufferSwap);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, resultFbo.frameBufferHandle);
		
		LogUtils.d("GLStatic", "gl time consume3: " + (System.currentTimeMillis() - time));
		
		GLES20.glReadPixels(0, 0, mImageWidth, mImageHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mPerformedPixelsBuffer.position(0));
		
		LogUtils.d("GLStatic", "gl time consume4: " + (System.currentTimeMillis() - time));
		mPerformedBitmap.copyPixelsFromBuffer(mPerformedPixelsBuffer);
		
		//mPerformedBitmap.setPixels(mPerformedPixelsBuffer.array(), 0, mImageWidth, 0, 0, mImageWidth, mImageHeight);
		return mPerformedBitmap;
	}
	
	/** 释放资源。调用release()后，下次使用时需再次init()。
	 * 
	 */
	public void glRenderRelease() {
		mAppContext = null;
		if (mCurrentLiveFilter != null) {
			mCurrentLiveFilter.glRelease();
			mCurrentLiveFilter = null;
		}
		
		if (GLHandle_Texture_OriginalBm != 0) {
			GLES20.glDeleteTextures(1, new int[] {GLHandle_Texture_OriginalBm}, 0);
		}
		
		BufferHelper.glReleaseFrameBuffer(mFrameBufferA);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferB);
		
	}
	
	private void initMatrix() {
		// 初始化Matrix 尤其是近视面、远视面，要根据图片大小而变。
		float[] mProjMatrix = new float[16];// 视井投影矩阵
		float[] mVMatrix = new float[16];// 摄像机位置矩阵
		float[] mMMatrix = new float[16];// 物体世界坐标矩阵
		
		float wr = (float)mImageWidth/ (float)MathUtils.nextPowerOfTwo(mImageWidth);
		float hr = (float)mImageHeight/ (float)MathUtils.nextPowerOfTwo(mImageHeight);
		
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0);
		// 近平面、顶点坐标，都是归一化的。
		Matrix.orthoM(mProjMatrix, 0, 0, wr, 0, hr, 1, 3);
		Matrix.setIdentityM(mMMatrix, 0);

		Matrix.setIdentityM(mMVPMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
	}
	
	private void initVBO() {
		int[] vbos = new int[1];
		GLES20.glGenBuffers(1, vbos, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbos[0]);
		float wr = (float)mImageWidth/ (float)MathUtils.nextPowerOfTwo(mImageWidth);
		float hr = (float)mImageHeight/ (float)MathUtils.nextPowerOfTwo(mImageHeight);
		
		float[] frameTexCoords = new float[] { 0, 0, wr, 0, 0, hr, wr, 0, wr,
				hr, 0, hr };
		mFrameTexPos = ByteBuffer
				.allocateDirect(frameTexCoords.length * GLConstants.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFrameTexPos.put(frameTexCoords).position(0);
		
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, frameTexCoords.length
				* GLConstants.BYTES_PER_FLOAT, mFrameTexPos.position(0),
				GLES20.GL_STATIC_DRAW);
		
		if (GLHandle_VBO_FrameTexPos != 0) {
			GLES20.glDeleteBuffers(1, new int[] {GLHandle_VBO_FrameTexPos}, 0);
		}
		GLHandle_VBO_FrameTexPos = vbos[0];
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

}
