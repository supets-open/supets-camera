package cn.jingling.lib.livefilter;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.utils.LogUtils;

public class GLStaticFBORenderControll {
	private static final int GL_MSG_INIT = 1;
	private static final int GL_MSG_SET_BITMAP = 2;
	private static final int GL_MSG_DRAW = 3;
	private static final int GL_RELEASE = 4;
	private static final int GL_MSG_DRAW_BM = 5;
	
	private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
	private static final int EGL_OPENGL_ES2_BIT = 4;
	
	private static final String TAG = "GLStaticFBORenderControll";

	private EGL10 mEGL;
	private EGLDisplay mEGLDisplay;
	private EGLContext mEGLContext;
	private EGLSurface mEGLSurface;
	
	private Context mCx;
	
	private HandlerThread mStaticGLThread;
	private Handler mGLHandler;
	
	private GLStaticFBORender mStaticRender;
	
	private Bitmap mOriginalBm;
	private Bitmap mPerformedBm;
	
	// 当前使用滤镜的label
	private String mCurrentFilterLabel;
	
	private boolean initialized = false;
	
	private int mMaxTextureSize;
	private int mStatus = BitmapAndStatus.STATUS_NORMAL;
		
	public class BitmapAndStatus {
		public Bitmap bitmap;
		public int status;
		public static final int STATUS_NORMAL = 0;
		public static final int STATUS_OOM = 1;
		
		public BitmapAndStatus(Bitmap bitmap, int status) {
			this.bitmap = bitmap;
			this.status = status;
		}
		
	}
	
	// 同步锁，用于OrderThread与WorkThread之间的同步
	private Object mMutex = new Object();
	
	private Object mMutex2 = new Object();
	
	/** 初始化GL空间。非耗时操作。不支持并发运行。
	 * 
	 */
	public int init(Context cx) {
		PackageSecurity.check(cx);
		if (initialized) {
			return mMaxTextureSize;
		}
		synchronized (mMutex2) {
			initialized = true;
			mCx = cx;
			mStaticGLThread = new HandlerThread("StaticGLThread");
			mStaticGLThread.start();
			mStaticRender = new GLStaticFBORender();
			mGLHandler = new Handler(mStaticGLThread.getLooper(), new StaticGLThreadHandlerCallback());
			mGLHandler.sendEmptyMessage(GL_MSG_INIT);
			
			try {
				// Block and wait. Until the working thread finishes init.
				mMutex2.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return mMaxTextureSize;
		}
		
	}
	
	
	/** 设置绘制原图。非耗时操作。不支持并发运行。
	 * 
	 */
	public void setBitmap(Bitmap bm) {
		if (bm == null) {
			return;
		}
		
		int maxTextureSize = GLHelper.glGetMaxTextureSize();
		int bmSize = Math.max(bm.getWidth(), bm.getHeight());
		
		if (maxTextureSize >0 && bmSize > maxTextureSize) {
			double ratio = (double) maxTextureSize/ (double)bmSize;
			int dstWidth = (int)(ratio *(double) bm.getWidth());
			int dstHeight = (int)(ratio *(double) bm.getHeight());
			bm = Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, true);
		}
		
		mOriginalBm = bm;
		mGLHandler.sendEmptyMessage(GL_MSG_SET_BITMAP);
	}
	
	/** 释放资源。调用release()后，下次使用GLStaticRenderControll时需再次init()。非耗时操作。不支持并发运行。
	 * 
	 */
	public void release() {
		
		mGLHandler.sendEmptyMessage(GL_RELEASE);
		mGLHandler = null;
		mOriginalBm = null;
		mCx = null;
		initialized = false;
	}
	
	
	public Bitmap drawFrame(String label) {
		
		synchronized (mMutex) {
			mCurrentFilterLabel = label;
			mGLHandler.sendEmptyMessage(GL_MSG_DRAW);
			try {
				// Block and wait. Until the working thread finishes the drawing.
				mMutex.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return mPerformedBm;
			
		}
	}
	
	/** 绘制图片。本方法为耗时操作。不支持并发运行。
	 * @param label 绘制使用的滤镜label。
	 */
	public BitmapAndStatus drawFrameBitmap(String label, Bitmap bm) {
		synchronized (mMutex) {
			mOriginalBm = bm;
			mCurrentFilterLabel = label;
			mStatus = BitmapAndStatus.STATUS_NORMAL;
			mGLHandler.sendEmptyMessage(GL_MSG_DRAW_BM);
			try {
				// Block and wait. Until the working thread finishes the drawing.
				mMutex.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return new BitmapAndStatus(mPerformedBm, mStatus);
			
		}
	}
	
	
	
	/** 一定要在GLThread中调用。
	 * 
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void glinitEGLContext() {
		mEGL = (EGL10) EGLContext.getEGL();
		mEGLDisplay = mEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

		if (mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
			throw new RuntimeException("eglGetDisplay failed : "
					+ GLUtils.getEGLErrorString(mEGL.eglGetError()));
		}
		int []configAttribs = {
				EGL10.EGL_BUFFER_SIZE, 32,
				EGL10.EGL_ALPHA_SIZE, 8,
				EGL10.EGL_BLUE_SIZE, 8,
				EGL10.EGL_GREEN_SIZE, 8,
				EGL10.EGL_RED_SIZE, 8,
				EGL10.EGL_DEPTH_SIZE, 8,
				EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
				EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
				EGL10.EGL_NONE
		};
		
		int []numConfigs = new int[1];
		EGLConfig[] configs = new EGLConfig[1]; 
		if (!mEGL.eglChooseConfig(mEGLDisplay, configAttribs, configs, 1, numConfigs)) {
			throw new RuntimeException("eglChooseConfig failed : " + 
					GLUtils.getEGLErrorString(mEGL.eglGetError()));
		}
		
		int []contextAttribs = {
				EGL_CONTEXT_CLIENT_VERSION, 2,
				EGL10.EGL_NONE
		};
		mEGLContext = mEGL.eglCreateContext(mEGLDisplay, configs[0], EGL10.EGL_NO_CONTEXT, contextAttribs);
		
//		final int[] textureHandle = new int[1];
//		GLES20.glGenTextures(1, textureHandle, 0);
//		mSurfaceTexture = new SurfaceTexture(textureHandle[0]);
//		mEGLSurface = mEGL.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurfaceTexture, null);
		
		
		int attribList[] = { EGL10.EGL_WIDTH, 8, EGL10.EGL_HEIGHT, 8,
				EGL10.EGL_NONE };
		mEGLSurface = mEGL.eglCreatePbufferSurface(mEGLDisplay, configs[0], attribList);
		
		if (mEGLSurface == EGL10.EGL_NO_SURFACE || mEGLContext == EGL10.EGL_NO_CONTEXT) {
			int error = mEGL.eglGetError();
			if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
				throw new RuntimeException("eglCreateWindowSurface returned  EGL_BAD_NATIVE_WINDOW. " );
			}
			throw new RuntimeException("eglCreateWindowSurface failed : " + 
					GLUtils.getEGLErrorString(mEGL.eglGetError()));
		}

		if (!mEGL.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
			throw new RuntimeException("eglMakeCurrent failed : " + 
					GLUtils.getEGLErrorString(mEGL.eglGetError()));
		}
	}
	
	private void glReleaseEGLContext() {
		mEGL.eglDestroyContext(mEGLDisplay, mEGLContext);
		mEGL.eglDestroySurface(mEGLDisplay, mEGLSurface);
		mEGLContext = EGL10.EGL_NO_CONTEXT;
		mEGLSurface = EGL10.EGL_NO_SURFACE;
	}
	
	private class StaticGLThreadHandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case GL_MSG_INIT:
				synchronized (mMutex2) {
					glinitEGLContext();
					mStaticRender.glInitRender(mCx);
					initialized = true;
					
					mMaxTextureSize = GLHelper.glGetMaxTextureSize();
					
					mMutex2.notify();
				}

				break;
			case GL_MSG_SET_BITMAP:
				   mStaticRender.glSetBitmap(mOriginalBm);

				break;
			case GL_MSG_DRAW:
				synchronized (mMutex) {
					long time = System.currentTimeMillis();
					mPerformedBm = mStaticRender
							.glDrawFrame(mCurrentFilterLabel);
					LogUtils.d("GLStatic",
							"gl time consume: "
									+ (System.currentTimeMillis() - time));
					mMutex.notify();
				}
				break;
			case GL_RELEASE:
				mStaticRender.glRenderRelease();
				glReleaseEGLContext();
				mStaticGLThread.quit();
				break;
			
			case GL_MSG_DRAW_BM:
				
				synchronized (mMutex) {
					long time = System.currentTimeMillis();
					
					try{
						mStatus = BitmapAndStatus.STATUS_NORMAL;
						mStaticRender.glSetBitmap(mOriginalBm);
						mPerformedBm = mStaticRender
									.glDrawFrame(mCurrentFilterLabel);
					} catch (OutOfMemoryError e){
						mStatus = BitmapAndStatus.STATUS_OOM;
						mPerformedBm = null;
					}
					
					LogUtils.d("GLStatic",
							"gl time consume: "
									+ (System.currentTimeMillis() - time));
					mMutex.notify();
				}
				break;

			default:
				break;
			}

			return true;
		}
		
	}

}
