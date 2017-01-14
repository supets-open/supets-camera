package cn.jingling.lib.livefilter;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.opengl.GLUtils;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.file.ImageFile;
import cn.jingling.lib.filters.FilterFactory;
import cn.jingling.lib.filters.RealsizeFilter;
import cn.jingling.lib.jpegsupport.JpegSupport;
import cn.jingling.lib.utils.ErrorHandleHelper;
import cn.jingling.lib.utils.LogUtils;

public class GLStaticFBOBlockRenderControll {
	private static final int GL_MSG_INIT = 1;
	private static final int GL_MSG_DRAW = 2;
	private static final int GL_RELEASE = 3;
	
	private static final int BLOCK_SIZE = 1500;
	private static final int BLOCK_OFFSET = 50;
	
	private static final String TAG = "GL_FBO_BLOCK_HANDLE";
	
	private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
	private static final int EGL_OPENGL_ES2_BIT = 4;

	private EGL10 mEGL;
	private EGLDisplay mEGLDisplay;
	private EGLContext mEGLContext;
	private EGLSurface mEGLSurface;
	
	private SurfaceTexture mSurfaceTexture;
	
	private Context mCx;
	
	private HandlerThread mStaticGLThread;
	private Handler mGLHandler;
	
	private GLStaticFBOBlockRender mStaticRender;
	
	private Bitmap mCurrentOriginalBmBlock;
	private Bitmap mCurrentPerformedBmBlock;
	private NormalizedRect mCurrentBlockPosition;
	// 当前使用滤镜的label
	private String mCurrentFilterLabel;
	
	private boolean initialized = false;
	
	// 同步锁，用于OrderThread与WorkThread之间的同步
	private Object mMutex = new Object();
	
	private final static String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/motusdk";
	
	/** 初始化GL空间。非耗时操作。不支持并发运行。
	 * 
	 */
	public void init(Context cx) {
		PackageSecurity.check(cx);
		if (initialized) {
			return;
		}
		initialized = true;
		mCx = cx;
		mStaticGLThread = new HandlerThread("StaticGLThread");
		mStaticGLThread.start();
		mStaticRender = new GLStaticFBOBlockRender();
		mGLHandler = new Handler(mStaticGLThread.getLooper(), new StaticGLThreadHandlerCallback());
		mGLHandler.sendEmptyMessage(GL_MSG_INIT);
	}
	
//	/** 设置绘制原图。非耗时操作。不支持并发运行。
//	 * 
//	 */
//	public void setBitmap(Bitmap bm) {
//		mOriginalBm = bm;
//		mGLHandler.sendEmptyMessage(GL_MSG_SET_BITMAP);
//	}
	
	/** 释放资源。调用release()后，下次使用GLStaticRenderControll时需再次init()。非耗时操作。不支持并发运行。
	 * 
	 */
	public void release() {
		
		mGLHandler.sendEmptyMessage(GL_RELEASE);
		mGLHandler = null;
		mCurrentOriginalBmBlock = null;
		mCx = null;
		initialized = false;
	}
	
	private int calStep(int totalSize, int blockSize) {
		int step = 1;
		int stepSize = totalSize/step;
		while (stepSize > blockSize) {
			step++;
			stepSize = totalSize/step;
		};
		return step;
	}
	
	
	public String drawFrame(String label, Uri inPathUri) {
		String rsLabel = "rs" + label;
		RealsizeFilter mFilter = FilterFactory.createRealsizeFilter(mCx, rsLabel);
		
		//怎么能引用sdkdemo里面的类呢？！
		/*String outPath = Directories.getRootDir() + "/"
				+ String.valueOf(System.currentTimeMillis()) + ".jpg";*/
		String outPath = ROOT + "/"
				+ String.valueOf(System.currentTimeMillis()) + ".jpg";
		String inPath = ImageFile.getRealPathFromUri(mCx, inPathUri);

		
		if (inPath.equals(outPath)) {
			ErrorHandleHelper.handleErrorMsg("Realsize error : inPath and outPath can't be the same !", TAG);
			return null;
			
		}
//		if (!checkJpg(inPath)) {
//			ErrorHandleHelper.handleErrorMsg("Realsize error : inPath file  !", TAG);
//			return null;
//		}

		int initReader = JpegSupport.initJpegReader(inPath);

		if (initReader != 0) {
			ErrorHandleHelper.handleErrorMsg("Realsize error : initJpegReader failed !", TAG);
			return null;
		}

		int initWriter = JpegSupport.initJpegWriter(outPath, -1, -1, 90);

		if (initWriter != 0) {
			ErrorHandleHelper.handleErrorMsg("Realsize error : initJpegWriter failed !", TAG);
			return null;
		}
		
		int width = JpegSupport.getReaderSrcImageWidth();

		int height = JpegSupport.getReaderSrcImageHeight();
		
		int stepHorizontal = calStep(width, BLOCK_SIZE);
		int stepSizeHorizontal = width/stepHorizontal;
		
		int stepVertical = calStep(height, BLOCK_SIZE);
		int stepSizeVertical = height/stepVertical;
		
		Log.e("zhijiankun","width : " + width);
		Log.e("zhijiankun","height : " + height);
		Log.e("zhijiankun","stepHorizontal : " + stepHorizontal);
		Log.e("zhijiankun","stepSizeHorizontal : " + stepSizeHorizontal);
		Log.e("zhijiankun","stepVertical : " + stepVertical);
		Log.e("zhijiankun","stepSizeVertical : " + stepSizeVertical);
		
		int[] pixelsFromReader = new int[width * (stepSizeVertical + 2*BLOCK_OFFSET)];
		int[] pixelsForWriter = new int[width * stepSizeVertical];
		int[] pixelsBuffer = new int[2*BLOCK_OFFSET*width];
		
		int currentJpegReadLine = 0;
		int currentJpegWriteLine = 0;
		
		int currentBlockColumn;
		int currentBlockRow;
		
		// 切割参数1：用于从pixelsFromReader中切割出RawBlock（算上offset）
		int y1;
		int h1;
		int x1;
		int w1;
		
		// 切割参数2：用于从RawBlock中切割出Block（不算offset）
		int y2;
		int h2;
		int x2;
		int w2;
		
		long time = System.currentTimeMillis();
		// 第一个行Block
		{
			y1 = BLOCK_OFFSET; 
			h1 = stepSizeVertical + BLOCK_OFFSET;
			y2 = 0;
			h2 = stepSizeVertical;
			
			
			int[] pixelsTmp = JpegSupport.readJpegLines(stepSizeVertical + BLOCK_OFFSET);
			currentJpegReadLine += stepSizeVertical + BLOCK_OFFSET;
			
			
			fillPixels2(pixelsFromReader, pixelsTmp, 0, BLOCK_OFFSET, width, stepSizeVertical + BLOCK_OFFSET, width);
			
			fillPixels(pixelsFromReader, pixelsBuffer, 0, stepSizeVertical, width, 2*BLOCK_OFFSET, width);
			
			
			pixelsTmp = null;
			
			currentJpegReadLine = currentJpegReadLine + h1;
			currentBlockColumn =0;
			currentBlockRow = 0;

			for (int j = 0; j < stepHorizontal; j++) {
				
				if (j==0) {   
				   x1 = 0;
				   w1 = 0 + stepSizeHorizontal + BLOCK_OFFSET;
				   x2 = 0;
				   w2 = stepSizeHorizontal;
				   
				   currentBlockColumn += stepSizeHorizontal;
				   
				} else if(j == (stepHorizontal -1)) {
					x1 = currentBlockColumn - BLOCK_OFFSET;
					w1 = width - x1; //最后一步，要保证加起来就是width
					x2 = BLOCK_OFFSET;
					w2 = w1 - BLOCK_OFFSET;
					currentBlockColumn = width;
					
				} else {
					x1 = currentBlockColumn - BLOCK_OFFSET;
					w1 = stepSizeHorizontal + 2*BLOCK_OFFSET;
					x2 = BLOCK_OFFSET;
					w2 = stepSizeHorizontal;
					currentBlockColumn += stepSizeHorizontal;
				}
				
				int[] blockPixels = new int[w1*h1];
				fillPixels(pixelsFromReader, blockPixels, x1, y1, w1, h1, width);

				Bitmap bm = Bitmap.createBitmap(blockPixels, w1, h1, Bitmap.Config.ARGB_8888);
				mCurrentOriginalBmBlock = bm.copy(Bitmap.Config.ARGB_8888, true);
				
				float left = (float)x1/(float)width;
				float right = left + (float)w1/(float)width;
				float top = 0.0f;
				float bottom = (float)(h1)/(float)height ;
				
				Log.e("zhijiankun", "left = " + (int)(left*width));
				Log.e("zhijiankun", "right = " + (int)(right*width));

				
				mCurrentBlockPosition = new NormalizedRect(left, right, top, bottom);

				mCurrentPerformedBmBlock = drawSingleBlock(label);

				mCurrentPerformedBmBlock.getPixels(blockPixels, 0, w1, 0, 0, w1, h1);
				
				
				int[] performPixels = new int[w2*h2];
				
				fillPixels(blockPixels, performPixels, x2, y2, w2, h2, w1);
				
				
				fillPixels2(pixelsForWriter, performPixels, stepSizeHorizontal*j, 0, w2, h2, width);
				blockPixels = null;
				performPixels = null;
				
				Log.e("zhijiankun", "end of row 1 block "+ (j+1) + ", time : " + (System.currentTimeMillis() - time));
			}
			
			
			JpegSupport.writeJpegLines(pixelsForWriter, stepSizeVertical);
			
		}
		
		if (stepVertical > 2) {
			
			y1 = 0; 
			h1 = stepSizeVertical + 2*BLOCK_OFFSET;
			y2 = BLOCK_OFFSET;
			h2 = stepSizeVertical;
			
			for (int i = 1; i < stepVertical -1 ; i++) {
				
				int[] pixelsTmp = JpegSupport.readJpegLines(stepSizeVertical);
				currentJpegReadLine += stepSizeVertical;
				
				fillPixels2(pixelsFromReader, pixelsTmp, 0, 2*BLOCK_OFFSET, width, stepSizeVertical, width);
				fillPixels2(pixelsFromReader, pixelsBuffer, 0, 0, width, 2*BLOCK_OFFSET, width);
				
				fillPixels(pixelsFromReader, pixelsBuffer, 0, stepSizeVertical, width, 2*BLOCK_OFFSET, width);
				
				pixelsTmp = null;
				
				//currentJpegReadLine = currentJpegReadLine + h1;
				currentBlockColumn =0;
				currentBlockRow = 0;

				for (int j = 0; j < stepHorizontal; j++) {
					
					if (j==0) {
					   x1 = 0;
					   w1 = 0 + stepSizeHorizontal + BLOCK_OFFSET;
					   x2 = 0;
					   w2 = stepSizeHorizontal;
					   
					   currentBlockColumn += stepSizeHorizontal;
					   
					} else if(j == (stepHorizontal -1)) {
						x1 = currentBlockColumn - BLOCK_OFFSET;;   
						w1 = width - x1; //最后一步，要保证加起来就是width
						x2 = BLOCK_OFFSET;
						w2 = w1 - BLOCK_OFFSET;
						currentBlockColumn = width;
						
					} else {
						x1 = currentBlockColumn - BLOCK_OFFSET;
						w1 = stepSizeHorizontal + 2*BLOCK_OFFSET;
						x2 = BLOCK_OFFSET;
						w2 = stepSizeHorizontal;
						currentBlockColumn += stepSizeHorizontal;
					}
					
					int[] blockPixels = new int[w1*h1];
					fillPixels(pixelsFromReader, blockPixels, x1, y1, w1, h1, width);
					
					Bitmap bm = Bitmap.createBitmap(blockPixels, w1, h1, Bitmap.Config.ARGB_8888);
					mCurrentOriginalBmBlock = bm.copy(Bitmap.Config.ARGB_8888, true);
					
					float left = (float)x1/(float)width;
					float right = left + (float)w1/(float)width;
					float top = (float)(i*stepSizeVertical - BLOCK_OFFSET)/(float)height;
					float bottom = top + (float)(h1)/(float)height ;
					
					mCurrentBlockPosition = new NormalizedRect(left, right, top, bottom);
					
					mCurrentPerformedBmBlock = drawSingleBlock(label);

					mCurrentPerformedBmBlock.getPixels(blockPixels, 0, w1, 0, 0, w1, h1);
					
					int[] performPixels = new int[w2*h2];
					
					fillPixels(blockPixels, performPixels, x2, y2, w2, h2, w1);
					
					fillPixels2(pixelsForWriter, performPixels, stepSizeHorizontal*j, 0, w2, h2, width);
					blockPixels = null;
					performPixels = null;
					
					Log.e("zhijiankun", "end of row "+(i+1)+ " block "+ (j+1) + ", time : " + (System.currentTimeMillis() - time));
				}
				

				JpegSupport.writeJpegLines(pixelsForWriter, stepSizeVertical);
				
			}
		}
			
			
			if (stepVertical > 1) {
				
				int stepSizeVerticalLast = height - (stepSizeVertical) * (stepVertical -1);
				pixelsFromReader = new int[width * (stepSizeVerticalLast + 2*BLOCK_OFFSET)];
				pixelsForWriter = new int[width * stepSizeVerticalLast];

				y1 = 0; 
				h1 = stepSizeVerticalLast + BLOCK_OFFSET;
				y2 = BLOCK_OFFSET;
				h2 = stepSizeVerticalLast;
				
                int[] pixelsTmp = JpegSupport.readJpegLines(stepSizeVerticalLast - BLOCK_OFFSET);
                currentJpegReadLine += stepSizeVerticalLast - BLOCK_OFFSET;
				
				fillPixels2(pixelsFromReader, pixelsTmp, 0, 2*BLOCK_OFFSET, width, stepSizeVerticalLast - BLOCK_OFFSET, width);
				fillPixels2(pixelsFromReader, pixelsBuffer, 0, 0, width,  2*BLOCK_OFFSET, width);
				
				//fillPixels(pixelsFromReader, pixelsBuffer, 0, stepSizeVerticalLast, width, 2*BLOCK_OFFSET, width);
				
				pixelsTmp = null;
				
				pixelsTmp = null;
				
				currentJpegReadLine = currentJpegReadLine + h1;
				currentBlockColumn = 0;
				currentBlockRow = 0;

				for (int j = 0; j < stepHorizontal; j++) {
					
					if (j==0) {   
					   x1 = 0;
					   w1 = 0 + stepSizeHorizontal + BLOCK_OFFSET;
					   x2 = 0;
					   w2 = stepSizeHorizontal;
					   
					   currentBlockColumn += stepSizeHorizontal;
					   
					} else if(j == (stepHorizontal -1)) {
						x1 = currentBlockColumn - BLOCK_OFFSET;;   
						w1 = width - x1; //最后一步，要保证加起来就是width
						x2 = BLOCK_OFFSET;
						w2 = w1 - BLOCK_OFFSET;
						currentBlockColumn = width;
						
					} else {
						x1 = currentBlockColumn - BLOCK_OFFSET;
						w1 = stepSizeHorizontal + 2*BLOCK_OFFSET;
						x2 = BLOCK_OFFSET;
						w2 = stepSizeHorizontal;
						currentBlockColumn += stepSizeHorizontal;
					}
					
					int[] blockPixels = new int[w1*h1];
					fillPixels(pixelsFromReader, blockPixels, x1, y1, w1, h1, width);
					
					Bitmap bm = Bitmap.createBitmap(blockPixels, w1, h1, Bitmap.Config.ARGB_8888);
					mCurrentOriginalBmBlock = bm.copy(Bitmap.Config.ARGB_8888, true);
					
					float left = (float)x1/(float)width;
					float right = left + (float)w1/(float)width;
					float top = (float)(height - stepSizeVerticalLast - BLOCK_OFFSET)/ (float)height;
					float bottom = top + (float)(h1)/(float)height ;
					
					mCurrentBlockPosition = new NormalizedRect(left, right, top, bottom);
					
					mCurrentPerformedBmBlock = drawSingleBlock(label);

					mCurrentPerformedBmBlock.getPixels(blockPixels, 0, w1, 0, 0, w1, h1);
					
					
					int[] performPixels = new int[w2*h2];
					
					fillPixels(blockPixels, performPixels, x2, y2, w2, h2, w1);
					
					fillPixels2(pixelsForWriter, performPixels, stepSizeHorizontal*j, 0, w2, h2, width);
					blockPixels = null;
					performPixels = null;
					
					Log.e("zhijiankun", "end of row "+(stepVertical)+ " block "+ (j+1) + ", time : " + (System.currentTimeMillis() - time));
				}
				
				JpegSupport.writeJpegLines(pixelsForWriter, stepSizeVerticalLast);
				
			
			}
		
		JpegSupport.finishReadingAndRelease();
		
		Log.e("zhijiankun", "finishReadingAndRelease");
		
		JpegSupport.finishWritingAndRelease();
		
		Log.e("zhijiankun", "the real end");
		
		
//		ImageFile.setFileOrientation(FilterEditActivity.this, mRSPath, ImageFile.TYPE_JPG, 
//				orientation);
		
		return outPath;
	}
	
	/** src里，抠出一块矩形来，给dst
	 */
	private void fillPixels(int[] src, int[] dst, int x, int y, int width, int height, int srcWidth) {
//		Log.e("zhijiankun", "x = " + x);
//		Log.e("zhijiankun", "y = " + y);
//		Log.e("zhijiankun", "width = " + width);
//		Log.e("zhijiankun", "height = " + height);
//		Log.e("zhijiankun", "srcWidth = " + srcWidth);
//		
		for (int i =0; i< height; i++) {
			
			for (int j =0; j< width; j++) {
				
				//Log.e("zhijiankun", "i = " + i);

				dst[i*width + j] = src[srcWidth*(y+i) + (x+j)];
			}

		}
	}
	
	/** 将dst中的像素，粘贴到src的矩形里。
	 */
	private void fillPixels2(int[] src, int[] dst, int x, int y, int width, int height, int srcWidth) {
		for (int i =0; i< height; i++) {
			for (int j =0; j< width; j++) {
				src[srcWidth*(y+i) + (x+j)] = dst[i*width + j] ;
			}

		}
	}
	
	/** 绘制单个图片块。本方法为耗时操作。不支持并发运行。
	 * @param label 绘制使用的滤镜label。
	 */
	private Bitmap drawSingleBlock(String label) {
		synchronized (mMutex) {
			mCurrentFilterLabel = label;
			mGLHandler.sendEmptyMessage(GL_MSG_DRAW);
			try {
				// Block and wait. Until the working thread finishes the drawing.
				mMutex.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return mCurrentPerformedBmBlock;
			
		}
	}
	
	
	
	/** 一定要在GLThread中调用。
	 * 
	 */
	private void glinitEGLContext() {
		mEGL = (EGL10) EGLContext.getEGL();
		mEGLDisplay = mEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		mSurfaceTexture = new SurfaceTexture(0);
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
	
	private class StaticGLThreadHandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case GL_MSG_INIT:
				glinitEGLContext();
				mStaticRender.glInitRender(mCx);
				initialized = true;
				break;
				
			case GL_MSG_DRAW:
				synchronized (mMutex) {
					long time = System.currentTimeMillis();
					mCurrentPerformedBmBlock = mStaticRender
							.glDrawFrame(mCurrentFilterLabel, mCurrentOriginalBmBlock, mCurrentBlockPosition);
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

			default:
				break;
			}

			return true;
		}
		
	}

}
