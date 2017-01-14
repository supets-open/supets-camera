package cn.jingling.lib.livefilter;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.livefilter.GLImageViewportHelper.ImageType;

public class GLStaticTextureViewRenderControll {
	private static final int GL_MSG_INIT = 1;
	private static final int GL_MSG_SET_BITMAP = 2;
	private static final int GL_MSG_DRAW = 3;
	private static final int GL_RELEASE = 4;
	
	private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
	private static final int EGL_OPENGL_ES2_BIT = 4;

	private EGL10 mEGL;
	private EGLDisplay mEGLDisplay;
	private EGLContext mEGLContext;
	private EGLSurface mEGLSurface;
	
	private SurfaceTexture mSurfaceTexture;
	
	private Context mCx;
	
	private HandlerThread mStaticGLTextureThread;
	private Handler mGLTextureHandler;
	
	private GLStaticTextureViewRender mStaticTextureRender;
	
	private Bitmap mOriginalBm;

	// 当前使用滤镜的label
	private String mCurrentFilterLabel;
	
	private boolean initialized = false;
	
	public GLStaticTextureViewRenderControll(Context cx) {
		PackageSecurity.check(cx);
		mCx = cx;
		mStaticGLTextureThread = new HandlerThread("StaticGLThread");
		mStaticGLTextureThread.start();
		mStaticTextureRender = new GLStaticTextureViewRender();
		mGLTextureHandler = new Handler(mStaticGLTextureThread.getLooper(), new GLStaticTextureThreadHandlerCallback());
	}
	
	/** 初始化GL空间。非耗时操作。不支持并发运行。
	 * 
	 */
	public void init(SurfaceTexture surfaceTexture) {
		if (initialized) {
			return;
		}
		initialized = true;
		mSurfaceTexture = surfaceTexture;
		mGLTextureHandler.sendEmptyMessage(GL_MSG_INIT);
	}
	
	/** 设置绘制原图。非耗时操作。不支持并发运行。
	 * 
	 */
	public void setBitmap(Bitmap bm) {
		mOriginalBm = bm;
		mGLTextureHandler.sendEmptyMessage(GL_MSG_SET_BITMAP);
	}
	
	/** 释放资源。调用release()后，下次使用GLStaticRenderControll时需再次init()。非耗时操作。不支持并发运行。
	 * 
	 */
	public void release() {
		
		mGLTextureHandler.sendEmptyMessage(GL_RELEASE);
		mGLTextureHandler = null;
		mOriginalBm = null;
		mCx = null;
		initialized = false;
	}
	
	/** 绘制图片。非耗时操作。不支持并发运行。
	 * @param label 绘制使用的滤镜label。
	 */
	public void drawFrame(String label) {
		mCurrentFilterLabel = label;
		mGLTextureHandler.sendEmptyMessage(GL_MSG_DRAW);
	}
	
	/**
	 * 设置显示方式。会在下一次DrawFrame时生效。
	 * @param type
	 */
	public void setImageType(ImageType type) {
		mStaticTextureRender.setImageType(type);
	}
	
	/** TextureView的Size发生变化时调用。
	 * @param viewWidth
	 * @param viewHeight
	 */
	public void setViewSize(int viewWidth, int viewHeight) {
		mStaticTextureRender.setViewSize(viewWidth, viewHeight);
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
		mEGLSurface = mEGL.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurfaceTexture, null);
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
	
	private class GLStaticTextureThreadHandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case GL_MSG_INIT:
				glinitEGLContext();
				mStaticTextureRender.glInitRender(mCx);
				initialized = true;

				break;
			case GL_MSG_SET_BITMAP:
				mStaticTextureRender.glSetBitmap(mOriginalBm);

				break;
			case GL_MSG_DRAW:
				mStaticTextureRender.glDrawFrame(mCurrentFilterLabel);
				mEGL.eglSwapBuffers(mEGLDisplay, mEGLSurface);
				break;
			case GL_RELEASE:
				mStaticTextureRender.glRenderRelease();
				glReleaseEGLContext();
				mStaticGLTextureThread.quit();
				break;

			default:
				break;
			}

			return true;
		}
		
	}

}
