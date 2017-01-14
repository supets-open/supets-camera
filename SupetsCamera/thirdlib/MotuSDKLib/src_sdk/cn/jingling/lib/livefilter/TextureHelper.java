package cn.jingling.lib.livefilter;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.utils.MathUtils;

public class TextureHelper {
	public static int loadTexture(final Context context, final int resourceId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false; // No pre-scaling
		// Read in the resource
		final Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), resourceId, options);

		return loadTexture(bitmap);
	}

	public static int loadTexture(Bitmap bitmap) {
		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}

	public static int loadSubTexture(Context context, int resourceId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false; // No pre-scaling

		// Read in the resource
		final Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), resourceId, options);
		return loadSubTexture(bitmap);
	}

	public static int loadSubTexture(Context context, String assetName) {
		InputStream is;
		try {
			is = context.getAssets().open(assetName);
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; // No pre-scaling

			Bitmap bm = BitmapFactory.decodeStream(is, null, options);
			if (bm == null) {
				throw new OutOfMemoryError();
			}
			
			return loadSubTexture(bm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public static int loadSubTexture(final Bitmap bitmap) {
		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Bitmap emptyBm = Bitmap.createBitmap(MathUtils.nextPowerOfTwo(w),
					MathUtils.nextPowerOfTwo(h), Config.ARGB_8888);
			
			if (emptyBm == null) {
				throw new OutOfMemoryError();
			}
			// Use a power of 2 container.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, emptyBm, 0);

			// Load the bitmap into the bound texture.
//			GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
			if(bitmap!=null&&!bitmap.isRecycled()){
				// Load the bitmap into the bound texture.
				GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
			}

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			emptyBm.recycle();
			bitmap.recycle();
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}

	public static int loadCurveTexture(Context cx, String assetName) {
		Curve curve = new Curve(cx, assetName);
		Bitmap bm = Bitmap.createBitmap(256, 1, Bitmap.Config.ARGB_8888);
		for (int i = 0; i < 256; i++) {
			bm.setPixel(
					i,
					0,
					Color.argb(255, curve.getCurveRed()[i],
							curve.getCurveGreen()[i], curve.getCurveBlue()[i]));
		}
		return loadTexture(bm);
	}
}
