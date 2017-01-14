package cn.jingling.lib.filters;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.jingling.lib.livefilter.NormalizedRect;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Layer {
	// private final static String TAG = "Layer";

	public enum Type {
		NORMAL, ROTATABLE, CROP, ROTATE_90
	};
	
	
	/** 从layer图片中，截取出一块矩形像素。逐块算法时，会使用。
	 * @return
	 */
	public static Bitmap getLayerBlock(Context cx, String file, int w, int h,
			Type type, NormalizedRect blockPositionRect) {
		Bitmap originalLayer = getLayerImage(cx, file,type);
		int widthBm = originalLayer.getWidth();
		int heightBm = originalLayer.getHeight();
		int x = (int)(blockPositionRect.left * widthBm);
		int y = (int)(blockPositionRect.top * heightBm);
		int width = (int)(blockPositionRect.right * widthBm) - x;
		int height = (int)(blockPositionRect.bottom * heightBm) - y;
		
		Bitmap cropedLayer = Bitmap.createBitmap(originalLayer, x, y, width, height);
		
		Bitmap scaledLayer = Bitmap.createScaledBitmap(cropedLayer, w, h, true);
		originalLayer.recycle();
		cropedLayer.recycle();
		return scaledLayer;
	}

	public static int[] getLayerPixels(Context cx, String file, int w, int h,
			Type type) {
		return getLayerPixels(cx, file, w, h, type, -1);
	}
	
	/**
	 * 
	 * @param cx
	 * @param file
	 * @param w
	 * @param h
	 * @param type
	 * @param alpha 0 ~ 255
	 * @return
	 */
	public static int[] getLayerPixels(Context cx, String file, int w, int h,
			Type type, int alpha) {
		Bitmap bm = getLayerImage(cx, file, w, h, type, alpha);
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		bm.recycle();
		return pixels;
	}
	
	/**
	 * @param alpha 0 ~ 255
	 */
	public static int[] getLayerPixels(Context cx, String file,
			Type type, int alpha) {
		Bitmap bm = getLayerImage(cx, file, -1, -1, type, alpha);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		bm.recycle();
		return pixels;
	}
	
	public static int[] getSDCardLayerPixels(Context cx, String file, int w, int h,
			Type type) {
		return getSDCardLayerPixels(cx, file, w, h, type, -1);
	}

	/**
	 * @param alpha 0 ~ 255
	 */
	public static int[] getSDCardLayerPixels(Context cx, String file, int w, int h,
			Type type, int alpha) {
		Bitmap bm = getSDCardLayerImage(cx, file, w, h, type, alpha);
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		bm.recycle();
		return pixels;
	}
	
	public static Bitmap getSDCardLayerImage(Context cx, String file, int w, int h,
			Type type) {
		try {
			FileInputStream is = new FileInputStream(file);
			return getLayerImage(is, w, h, type, -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param alpha 0 ~ 255
	 */
	public static Bitmap getSDCardLayerImage(Context cx, String file, int w, int h,
			Type type, int alpha) {
		try {
			FileInputStream is = new FileInputStream(file);
			return getLayerImage(is, w, h, type, alpha);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param alpha 0 ~ 255
	 */
	public static Bitmap getSDCardLayerImage(Context cx, String file, int w, int h,
			Type type, int alpha, boolean scale) {
		try {
			FileInputStream is = new FileInputStream(file);
			return getLayerImage(is, w, h, type, alpha, scale);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getLayerImage(Context cx, String file, Type type) {
		return getLayerImage(cx, file, -1, -1, type, -1, false);
	}

	/**
	 * 
	 * @param cx
	 * @param file
	 * @param size the long side
	 * @param type
	 * @return
	 */
	public static Bitmap getLayerImage(Context cx, String file, int size, Type type) {
		return getLayerImage(cx, file, -1, size, type, -1, true);
	}

	public static Bitmap getLayerImage(Context cx, String file, int w, int h,
			Type type) {
		try {
			InputStream is = cx.getAssets().open(file);
			return getLayerImage(is, w, h, type, -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param alpha 0 ~ 255
	 */
	public static Bitmap getLayerImage(Context cx, String file, int w, int h,
			Type type, int alpha) {
		try {
			InputStream is = cx.getAssets().open(file);
			return getLayerImage(is, w, h, type, alpha);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param alpha 0 ~ 255
	 */
	public static Bitmap getLayerImage(Context cx, String file, Type type, int alpha) {
		try {
			InputStream is = cx.getAssets().open(file);
			return getLayerImage(is, -1, -1, type, alpha);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param alpha 0 ~ 255
	 */
	public static Bitmap getLayerImage(Context cx, String file, int w, int h,
			Type type, int alpha, boolean scale) {
		try {
			InputStream is = cx.getAssets().open(file);
			return getLayerImage(is, w, h, type, alpha, scale);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param alpha 0 ~ 255
	 */
	private static Bitmap getLayerImage(InputStream is, int w, int h,
			Type type, int alpha) {
		return getLayerImage(is, w, h, type, alpha, true);
	}

	/**
	 * @param alpha 0 ~ 255
	 */
	private static Bitmap getLayerImage(InputStream is, int w, int h,
			Type type, int alpha, boolean scale) {
		Bitmap bm = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = true;
		options.inPreferredConfig = Config.ARGB_8888;
		options.inScaled = false; // No pre-scaling
//		bm = BitmapFactory.decodeStream(is, null, options);
		Bitmap original = BitmapFactory.decodeStream(is, null, options);
		if (w == -1) {
			if (h == -1) {
				w = original.getWidth();
				h = original.getHeight();
			} else {
				int size = h;
				w = original.getWidth();
				h = original.getHeight();
				int l = Math.max(w, h);
				w = w * size / l;
				h = h * size / l;
			}
		}
		if (type == Type.CROP
				&& h * original.getWidth() / w < original.getHeight()) {
			bm = Bitmap.createBitmap(original, 0, 0, original.getWidth(), h
					* original.getWidth() / w);
			original.recycle();
		} else {
			bm = original;
		}

		Bitmap bitmap = null;
		Matrix m = new Matrix();

		if (type == Type.ROTATE_90) {
			Bitmap tmpBitmap = null;
			m.postRotate(270);

			tmpBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);

			if (bm != tmpBitmap) {
				bm.recycle();
			}
			bm = tmpBitmap;
			
		}
		
		if (w > h && type == Type.ROTATABLE) {
			Bitmap tmpBitmap = null;
			m.postRotate(90);
			m.postScale(-1, 1);

			tmpBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);

			if (bm != tmpBitmap) {
				bm.recycle();
			}
			bm = tmpBitmap;
		}
		
		if (scale) {
			float sx = (float) (w) / bm.getWidth();
			float sy = (float) (h) / bm.getHeight();
			//use canvas instead of Bitmap.createBitmap to avoid OpenGL black screen Error on some phones.
			m = new Matrix();
			m.reset();
			m.preScale(sx, sy);
			bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);
			c.drawBitmap(bm, m, new Paint());
		} else {
			bitmap = bm;
		}
		// Log.d("Layer", "1: "+bitmap1.getWidth()+":"+bitmap1.getHeight());

		if (bitmap != bm) {
			bm.recycle();
		}
		if (alpha != -1) {
			Paint p = new Paint();
			p.setAlpha(alpha);
			p.setDither(true);
			Bitmap b = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);
			c.drawBitmap(bitmap, 0, 0, p);
			bitmap.recycle();
			return b;
		}
		return bitmap;
	}

}
