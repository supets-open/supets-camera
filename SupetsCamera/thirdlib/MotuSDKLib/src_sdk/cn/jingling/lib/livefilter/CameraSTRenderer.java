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
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.GLImageViewportHelper.ImageType;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;
import cn.jingling.lib.utils.ErrorHandleHelper;
import cn.jingling.lib.utils.LogUtils;
import cn.jingling.lib.utils.MathUtils;

@SuppressLint("NewApi")
public class CameraSTRenderer extends AbsCameraRender implements
		SurfaceTexture.OnFrameAvailableListener, CameraRenderInteface {

	private FloatBuffer mFrameTexPos, mDisplayedTexPos;
	private int mVBOFrameHandle, mVBODisplayHandle;
	private FrameBufferInfo mFrameBufferA, mFrameBufferB;
	private ShaderInfo mRGBShader;

	private int mVertexCount;
	private float[] mMVPMatrix = new float[16];
	private float[] mFixMVPMatrix = new float[16];
	private boolean mPixelsReady = false;

	private SurfaceTexture mSurfaceTexture;
	private GLSurfaceView mGLSurfaceView;
	// Renderer observer.
	private Observer mObserver;
	private FrameBufferInfo mFrameBuffer;
	private int mTextureDataHandle;
	// float[] mTextureMatrix = new float[16];
	private int mDirection;
	private boolean mFlip;
	private GLDrawHelper mGlDrawHelper;
	private LiveOriginal mLiveOriginal;

	public CameraSTRenderer(Context cx, GLSurfaceView gLSurfaceView,
			boolean smooth) {
		PackageSecurity.check(cx);
		mAppContext = cx.getApplicationContext();
		mLiveFilters = LiveFilterInfo.generateLiveFilters(cx, smooth);
		setFilter("original");
		this.mGLSurfaceView = gLSurfaceView;
		mGlDrawHelper = new GLDrawHelper();
		mLiveOriginal = new LiveOriginal();
	}

	/**
	 * 初始化函数 必须在初始化时要调用
	 * 
	 * @param previewWidth
	 * @param previewHeight
	 * @param direction
	 * @param flip
	 */
	public void init(int previewWidth, int previewHeight, int direction,
			boolean flip, ImageType imageType) {
		if (previewWidth <= 0 || previewHeight <= 0 || direction < 0) {
			ErrorHandleHelper
					.handleErrorMsg(
							"CameraMtkRenderer->init() parameters contain invalid value!",
							"CameraMtkRenderer");
			return;
		}
		this.mImageType = imageType;
		this.mDirection = direction;
		this.mFlip = flip;
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
			mFrameTexPos = getTextureCoordinate();
			mDisplayedTexPos = getTextureCoordinate();
			mNeedUpdateViewPort = true;
			initMatrix(direction, flip);
		}

		// 滤镜参数初始化
		Iterator<Entry<String, LiveFilterInfo>> iter = mLiveFilters.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, LiveFilterInfo> entry = iter.next();
			entry.getValue().setup();
		}
	}

	/**
	 * 初始化一个SurfaceTexture，用于和camera绑定，获取数据
	 */
	private void initSurfaceTexture() {
		SurfaceTexture oldSurfaceTexture = mSurfaceTexture;
		mSurfaceTexture = new SurfaceTexture(mTextureDataHandle);
		mSurfaceTexture.setOnFrameAvailableListener(this);

		// free
		if (oldSurfaceTexture != null) {
			oldSurfaceTexture.release();
			oldSurfaceTexture = null;
		}
	}

	public LiveFilterInfo getFilterInfo() {
		return mCurrentLiveFilter;
	}

	public void refresh() {
		synchronized (mMutex) {
			mFilterUpdate = true;
		}
	}

	/**
	 * 设置显示方式，会在绘制下一帧数据时生效。
	 * 
	 * @param imageType
	 */
	public void setImageType(ImageType imageType) {
		mImageType = imageType;
		mNeedUpdateViewPort = true;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLHelper.glCheckError();
		synchronized (mMutex) {
			if (!mPixelsReady) {
				return;
			}
			GLES20.glClearColor(0, 0, 0, 0);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
					| GLES20.GL_DEPTH_BUFFER_BIT);

			if (mFilterUpdate) {
				// 开启实时滤镜后，需要做些变量数据更新
				glUpdateFilter();
				mFilterUpdate = false;
			}

			if (mNeedUpdateViewPort) {
				mViewportRect = GLImageViewportHelper.getGLViewPort(
						mPreviewImageWidth, mPreviewImageHeight, mViewWidth,
						mViewHeight, mImageType);
				mNeedUpdateViewPort = false;
			}

			mSurfaceTexture.updateTexImage();
			// mSurfaceTexture.getTransformMatrix(mTextureMatrix);
			// 原图预览
			if ("original".equals(mCurrentLiveFilter.getLabel())) {
				BufferHelper.glBindFrameBuffer(mFrameBuffer);
				mGlDrawHelper.glDrawPreview(mMVPMatrix, mRGBShader, null,
						mTextureDataHandle, mVBOFrameHandle,
						mPreviewImageWidth, mPreviewImageHeight, mViewportRect);
			} else {// 加滤镜的预览
				BufferHelper.glBindFrameBuffer(mFrameBuffer);
				BufferHelper.glBindFrameBuffer(mFrameBufferA);
				BufferHelper.glBindFrameBuffer(mFrameBufferB);
				mGlDrawHelper.glDrawPreview(mMVPMatrix, mRGBShader,
						mFrameBuffer, mTextureDataHandle, mVBOFrameHandle,
						mPreviewImageWidth, mPreviewImageHeight, mViewportRect);
				FrameBufferInfo fbi = mCurrentLiveFilter.glDraw(mFixMVPMatrix,
						mVBODisplayHandle, mFrameBuffer.textureHandle,
						new FrameBufferInfo[] { mFrameBufferA, mFrameBufferB });
				mLiveOriginal.glDraw(mFixMVPMatrix, mVBODisplayHandle, fbi,
						mViewportRect, fbi.textureHandle);
			}
			mPixelsReady = false;
			GLES20.glFinish();
		}
		GLHelper.glCheckError();
		if (DEBUG) {
			mFpsCount++;
			if (mFpsCount >= FPS_INTERVAL) {
				markTime();
				mFpsCount = 0;
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		mViewWidth = width;
		mViewHeight = height;
		mNeedUpdateViewPort = true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		if (DEBUG) {
			LogUtils.d("xxxx", "onSurfaceCreated");
		}
		mRGBShader = ShaderHelper.glGenerateShader(mAppContext,
				"vertex_shader_2", "rgb_fragment_shader", "aPosition",
				"uTexture", "uMVPMatrix");

		BufferHelper.glReleaseFrameBuffer(mFrameBufferA);
		BufferHelper.glReleaseFrameBuffer(mFrameBufferB);

		// 申请FBO 用于实时滤镜
		mFrameBufferA = BufferHelper.glGenerateFrameBufferWithNoBind(
				mPreviewImageWidth, mPreviewImageHeight);
		mFrameBufferB = BufferHelper.glGenerateFrameBufferWithNoBind(
				mPreviewImageWidth, mPreviewImageHeight);

		BufferHelper.glReleaseFrameBuffer(mFrameBuffer);
		mFrameBuffer = BufferHelper.glGenerateFrameBufferWithNoBind(
				mPreviewImageWidth, mPreviewImageHeight);
		GLHelper.glCheckError();

		glInitTextures();
		initSurfaceTexture();
		glInitVBOs();
		if (mObserver != null) {
			mObserver.onSurfaceTextureCreated(mSurfaceTexture);
		}
		mLiveOriginal.glSetup(mAppContext);
	}

	private FloatBuffer getTextureCoordinate() {
		float wr = 1.0f;
		float hr = 1.0f;

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
		Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix,
				0);

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
		mGlDrawHelper.initProjectionMatrix(projectionMatrix, direction, flip,
				mTextureWidth, mTextureHeight, mViewWidth, mViewHeight);
	}

	private void initModelViewMatrix(float[] modelViewMatrix, int direction,
			boolean flip) {
		mGlDrawHelper.initModelViewMatrix(modelViewMatrix, direction, flip);
	}

	/**
	 * 初始化一块纹理 用于和mSurfaceTexture绑定
	 */
	private void glInitTextures() {
		final int[] textureHandle = new int[1];
		mGlDrawHelper.glInitTextures(textureHandle);
		mTextureDataHandle = textureHandle[0];
	}

	/**
	 * 初始化二块VBO 用于定点buffer
	 */
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

	public SurfaceTexture getSurfaceTexture() {
		return mSurfaceTexture;
	}

	@Override
	public void onFrameAvailable(SurfaceTexture surfaceTexture) {
		synchronized (mMutex) {
			mPixelsReady = true;
		}

		if (mGLSurfaceView != null) {
			mGLSurfaceView.requestRender();
		}
	}

	public void setObserver(Observer observer) {
		mObserver = observer;
	}

	public interface Observer {
		/**
		 * 用于在SurfaceTexture调用onSurfaceCreated时，回调给activity
		 */
		public void onSurfaceTextureCreated(SurfaceTexture surfaceTexture);
	}

	private void logMatrix(float[] matrix, String logName) {
		if (!DEBUG) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 16; i++) {
			sb.append(matrix[i] + ",");
		}
		Log.d("xxxx", logName + " = " + sb.toString());
	}

	public void resetViewSize(int viewWidth, int viewHeight) {
		this.mViewWidth = viewWidth;
		this.mViewHeight = viewHeight;
	}
}
