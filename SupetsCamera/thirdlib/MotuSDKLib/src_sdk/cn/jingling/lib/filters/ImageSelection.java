package cn.jingling.lib.filters;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageSelection {

	public enum Align {
		CENTER, LEFT_TOP, RIGHT_BOTTOM
	};

	private int[] mSelection; // the value is from 0 to 255. the size is the
								// same as the image being selected.
	private int mWidth, mHeight;

	public ImageSelection(int width, int height) {
		mWidth = width;
		mHeight = height;
		mSelection = new int[mWidth * mHeight];
	}

	public ImageSelection(ImageSelection imageSelection) {
		mWidth = imageSelection.getWidth();
		mHeight = imageSelection.getHeight();
		try {
			mSelection = (int[]) (imageSelection.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Bitmap getSelectedBitmap(Bitmap bm) {
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		for (int i = 0; i < w * h; i ++) {
			int c = pixels[i];
			pixels[i] = Color.argb(mSelection[i], Color.red(c), Color.green(c), Color.blue(c));
		}
		Bitmap rst = Bitmap.createBitmap(pixels, w, h, bm.getConfig());
		return rst;
	}
	
	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

	public int[] getPixels() {
		return mSelection;
	}

	public void selectAll() {
		for (int i = 0; i < mWidth * mHeight; i++) {
			mSelection[i] = 255;
		}
	}

	public void reverse() {
		for (int i = 0; i < mWidth * mHeight; i++) {
			mSelection[i] = 255 - mSelection[i];
		}
	}
	
	public void setPoint(int x, int y, int value) {
		mSelection[y * mWidth + x] = value;
	}

	public void selectSquare(int width, int height, Align align) {
		selectSquare(width, height, align, 0, 0);
	}

	public void selectSquare(int width, int height, Align align,
			int roundCornerRadius, int featherSize) {
		final int r = roundCornerRadius;
		int x, y;
		switch (align) {
		case CENTER:
			y = (mHeight - height) / 2;
			x = (mWidth - width) / 2;
			break;
		case RIGHT_BOTTOM:
			y = mHeight - height;
			x = mWidth - width;
			break;
		default:
			y = 0;
			x = 0;
			break;
		}
		for (int i = 0; i < mHeight; i++) {
			for (int j = 0; j < mWidth; j++) {
				if (i >= y && i < y + height && j >= x && j < x + width) {
					if (isOutTheCorner(width, height, j - x, i - y, r)) {
						mSelection[i * mWidth + j] = 0;
					} else {
						mSelection[i * mWidth + j] = getSquareFeatherValue(
								width, height, j - x, i - y, featherSize);
					}
				} else {
					mSelection[i * mWidth + j] = 0;
				}
			}
		}
	}
	
	public void selectRound(int r, Align align) {
		selectRound(r, align, 0);
	}

	public void selectRound(int r, Align align, int featherSize) {
		int x, y; // the center of the round
		switch (align) {
		case CENTER:
			y = mHeight / 2;
			x = mWidth / 2;
			break;
		case RIGHT_BOTTOM:
			y = mHeight - r;
			x = mWidth - r;
			break;
		default:
			y = r;
			x = r;
			break;
		}
		selectRound(x, y, r, featherSize);
	}
	
	public void selectRound(int x, int y, int r, int featherSize) {
		final int f = featherSize;
		int r2 = r * r;
		int fr2 = (r - f) * (r - f);
		for (int i = 0; i < mHeight; i++) {
			for (int j = 0; j < mWidth; j++) {
				int d2 = dist2(i, j, y, x);
				if (d2 < fr2) {
					mSelection[i * mWidth + j] = 255;
				} else if (d2 < r2) {
					int d = (int)Math.sqrt(d2);
					mSelection[i * mWidth + j] = 255 * (r - d) / f;
				} else {
					mSelection[i * mWidth + j] = 0;
				}
			}
		}
		
	}

	private boolean isOutTheCorner(int w, int h, int x, int y, int r) {
		if (r == 0) {
			return false;
		}
		final int r2 = r * r;
		if (x < r && y < r && dist2(x, y, r, r) > r2) {
			return true;
		}
		if (x > w - r && y < r && dist2(x, y, w - r, r) > r2) {
			return true;
		}
		if (x < r && y > h - r && dist2(x, y, r, h - r) > r2) {
			return true;
		}
		if (x > w - r && y > h - r && dist2(x, y, w - r, h - r) > r2) {
			return true;
		}
		return false;
	}

	private int getSquareFeatherValue(int w, int h, int x, int y,
			int featherSize) {
		final int f = featherSize;
		if (f == 0) {
			return 255;
		}
		double rate = 1;
		if (x < f) {
			rate = rate * x / f;
		}
		if (x > w - f) {
			rate = rate * (w - x) / f;
		}
		if (y < f) {
			rate = rate * y / f;
		}
		if (y > h - f) {
			rate = rate * (h - y) / f;
		}
		return (int) (255 * rate);
	}

	private int dist2(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}
}
