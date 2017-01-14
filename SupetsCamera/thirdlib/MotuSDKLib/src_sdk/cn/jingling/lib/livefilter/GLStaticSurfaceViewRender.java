package cn.jingling.lib.livefilter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.GLImageViewportHelper.ImageType;
import cn.jingling.lib.utils.MathUtils;

public class GLStaticSurfaceViewRender implements GLSurfaceView.Renderer {

	private Context mAppContext;

	private float[] mMVPMatrixStandard = new float[16];// 总投影变换矩阵：标准投影
	private float[] mMVPMatrixFlipUpDown = new float[16];// 总投影变换矩阵:实现上下Flip
	private int mImageWidth = 0;
	private int mImageHeight = 0;
	private int mViewWidth = 0;
	private int mViewHeight = 0;
	private Bitmap mOriginalBitmap;

	private ImageType mImageType = ImageType.FIT_CENTER;

	// 在一次渲染过程中，会涉及到多次管线渲染叠加。FBO A、B依次作为输入、输出，完成渲染管线的多次叠加。
	private FrameBufferInfo mFrameBufferA, mFrameBufferB;

	// 顶点数据。在我们的渲染管线里，这个数据既作为vertex的顶点坐标，同时也作为Texture贴图坐标。
	private FloatBuffer mFrameTexPos;

	// 所有的Filter信息。
	private Map<String, LiveFilterInfo> mLiveFilters;
	// 当前使用的Filter。
	private LiveFilterInfo mCurrentLiveFilter;
	private LiveOriginal mLiveOriginal;

	private String mCurrentLabel = "original";

	private int GLHandle_VBO_FrameTexPos = 0;
	private int GLHandle_Texture_OriginalBm = 0;

	private boolean needInit = false;
	private boolean inited = false;
	private boolean needResetBm = false;
	private boolean bmSet = false;
	private boolean needRelease = false;

	/** 
	 * 初始化Render。会在下一次onDrawFrame时生效。
	 * @param cx
	 */
	public void initRender(Context cx) {
		mAppContext = cx;
		needInit = true;
		mLiveOriginal = new LiveOriginal();
	}

	/**
	 * 释放资源。调用release()后，下次使用时需再次init()。会在下一次onDrawFrame时生效。
	 * 
	 */
	public void releaseRender() {
		needRelease = true;

	}

	/**
	 * 切换图片时调用。根据图片重新设置openGL相关参数。会在下一次onDrawFrame时生效。
	 * 
	 * @param originalBm
	 */
	public void setBitmap(Bitmap originalBm) {
		mOriginalBitmap = originalBm;
		mImageWidth = originalBm.getWidth();
		mImageHeight = originalBm.getHeight();
		needResetBm = true;
	}

	/**
	 * 设置所使用的FilterLabel。会在下一次onDrawFrame时生效。
	 * 
	 * @param filterLabel
	 */
	public void setFilter(String filterLabel) {
		
		if (mLiveFilters==null) {
			mLiveFilters=LiveFilterInfo.generateLiveFilters(mAppContext, true);
		}
		
		if (!mLiveFilters.containsKey(filterLabel)) {
			throw new RuntimeException("Filter label " + filterLabel
					+ " does not exsit in LiveFilterInfo!");
		}
		mCurrentLabel = filterLabel;
	}

	/**
	 * 设置显示方式。会在下一次onDrawFrame时生效。
	 * 
	 * @param type
	 */
	public void setImageType(ImageType type) {
		mImageType = type;
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		if (needInit) {
			glInitRender();
			needInit = false;
			inited = true;
		}

		if (needResetBm && inited && mOriginalBitmap!=null) {
			glResetBitmap(mOriginalBitmap);
			needResetBm = false;
			bmSet = true;
		}

		if (needRelease) {
			glReleaseRender();
			needRelease = false;
		} else {
			if (inited && bmSet) {
				glDrawFrame();
			}
		}

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		mViewWidth = width;
		mViewHeight = height;

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		// 清除深度缓冲与颜色缓冲
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		mLiveOriginal.glSetup(mAppContext);
	}

	private void glInitRender() {
		mLiveFilters = LiveFilterInfo.generateLiveFilters(mAppContext, true);
		needInit = false;
	}

	private void glReleaseRender() {
		mAppContext = null;
		if (mCurrentLiveFilter != null) {
			mCurrentLiveFilter.glRelease();
			mCurrentLiveFilter = null;
		}

		if (GLHandle_Texture_OriginalBm != 0) {
			GLES20.glDeleteTextures(1,
					new int[] { GLHandle_Texture_OriginalBm }, 0);
		}

		BufferHelper.glReleaseFrameBuffer(mFrameBufferA);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferB);
	}

	private void glResetBitmap(Bitmap originalBm) {
		
		mImageWidth = originalBm.getWidth();
		mImageHeight = originalBm.getHeight();
		
		initMatrix();

		// 初始化FBO
		BufferHelper.glReleaseFrameBuffer(mFrameBufferA);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferB);
		mFrameBufferA = BufferHelper.glGenerateFrameBuffer(mImageWidth,
				mImageHeight);
		mFrameBufferB = BufferHelper.glGenerateFrameBuffer(mImageWidth,
				mImageHeight);

		// 原始图片数据存入Texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		if (GLHandle_Texture_OriginalBm != 0) {
			GLES20.glDeleteTextures(1,
					new int[] { GLHandle_Texture_OriginalBm }, 0);
		}
		Bitmap textureBm = originalBm.copy(originalBm.getConfig(), true);
		GLHandle_Texture_OriginalBm = TextureHelper.loadSubTexture(textureBm);

		// 生成图片顶点坐标、Texture贴图坐标并存入VBO
		initVBO();

	}

	/**
	 * 绘制前调用。会根据当图片宽高、视窗宽高和ImageType，计算当前帧绘制到窗口时，使用的ViewPort。
	 * 
	 */
//	private void glResetViewPort() {	
//		mCurrentViewport = GLImageViewportHelper.getGLViewPort(
//				mImageWidth, mImageHeight, mViewWidth, mViewHeight, mImageType);
//	}

	private void glDrawFrame() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		if (mCurrentLiveFilter != null) {
			mCurrentLiveFilter.glRelease();
			mCurrentLiveFilter = null;
		}

		mCurrentLiveFilter = mLiveFilters.get(mCurrentLabel);
		// 第一次调用某滤镜的glUpdate时，会setup，会对其所有的Ops实例进行setup，会比较卡。
		// 以后调用就只是加载相应的texture。
		// 调用release时，将其用到的texture删掉。
		
		ViewportRect mCurrentViewport = GLImageViewportHelper.getGLViewPort(
				mImageWidth, mImageHeight, mViewWidth, mViewHeight, mImageType);
		
		mCurrentLiveFilter.glUpdate(mAppContext, new Point(mImageWidth,
				mImageHeight));

		FrameBufferInfo[] frameBufferSwap = new FrameBufferInfo[] {
				mFrameBufferA, mFrameBufferB };

		FrameBufferInfo fbi = mCurrentLiveFilter.glDraw(mMVPMatrixStandard, GLHandle_VBO_FrameTexPos,
				GLHandle_Texture_OriginalBm, frameBufferSwap);
		mLiveOriginal.glDraw(mMVPMatrixFlipUpDown, GLHandle_VBO_FrameTexPos, null, mCurrentViewport, fbi.textureHandle);
	}

	private void initMatrix() {

		float[] mProjMatrix = new float[16];// 视井投影矩阵projection
		float[] mVMatrix = new float[16];// 摄像机位置矩阵vision
		float[] mMMatrix = new float[16];// 物体世界坐标矩阵model

		float wr = (float)mImageWidth/ (float)MathUtils.nextPowerOfTwo(mImageWidth);
		float hr = (float)mImageHeight/ (float)MathUtils.nextPowerOfTwo(mImageHeight);
		
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0);
		Matrix.orthoM(mProjMatrix, 0, 0, wr, 0, hr, 1, 3);

		Matrix.setIdentityM(mMMatrix, 0);

		Matrix.setIdentityM(mMVPMatrixStandard, 0);
		Matrix.multiplyMM(mMVPMatrixStandard, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrixStandard, 0, mProjMatrix, 0, mMVPMatrixStandard, 0);
		
		
        // 视线沿着z轴负方向，即可实现flip; up方向转动，即可实现rotate。
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -2, 0, 0, 0, 0, -1, 0);
		// up为y轴正向，右侧为x轴正向。（left<right bottom<top） (up*右=视线方向)
		Matrix.orthoM(mProjMatrix,  0, 0, wr, -hr, 0, 1, 3);
		
		Matrix.setIdentityM(mMMatrix, 0);
		
		Matrix.setIdentityM(mMVPMatrixFlipUpDown, 0);
		Matrix.multiplyMM(mMVPMatrixFlipUpDown, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrixFlipUpDown, 0, mProjMatrix, 0, mMVPMatrixFlipUpDown, 0);

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
				.allocateDirect(
						frameTexCoords.length * GLConstants.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFrameTexPos.put(frameTexCoords).position(0);

		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, frameTexCoords.length
				* GLConstants.BYTES_PER_FLOAT, mFrameTexPos.position(0),
				GLES20.GL_STATIC_DRAW);

		if (GLHandle_VBO_FrameTexPos != 0) {
			GLES20.glDeleteBuffers(1, new int[] { GLHandle_VBO_FrameTexPos }, 0);
		}
		GLHandle_VBO_FrameTexPos = vbos[0];
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

}
