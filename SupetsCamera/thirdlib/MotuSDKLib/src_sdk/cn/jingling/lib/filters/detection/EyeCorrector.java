package cn.jingling.lib.filters.detection;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

public class EyeCorrector {

	private final static String TAG = "EyeCorrector";

	private final static int RADIUS_EYE_CENTER = 20;
	private final static int RADIUS_EYE_SIZE = 100;
	private final static int THRESHOLD_EYE_SIZE = 8;
	private int mEyePixelMaxNum = 2000;
	private float mBinaryThreshold = 0;
	private int mEyePixelNum;
	private int mEyeLeftSide;
	private int mEyeRightSide;
	private int mEyeUpSide;
	private int mEyeDownSide;
	private int mDiameterOfEye = 0;

	public Point getRealEyeCenter(Bitmap bm, int x, int y) {
		Params p = new Params();
		int r;
		int[] pixels;
		r = RADIUS_EYE_CENTER;
		fillBorders(bm, x, y, r, p);
		pixels = new int[p.w * p.h];
		bm.getPixels(pixels, 0, p.w, p.x0, p.y0, p.w, p.h);
		Point center = findCenterOfEye(pixels, p.w, p.h);
		center.x += x - r;
		center.y += y - r;
		r = RADIUS_EYE_SIZE;
		fillBorders(bm, p.x0, p.y0, r, p);
		pixels = new int[p.w * p.h];
		bm.getPixels(pixels, 0, p.w, p.x0, p.y0, p.w, p.h);
		mDiameterOfEye = (int) getDiameterOfEye(pixels, p.w, p.h, new Point(
				center.x - p.x0, center.y - p.y0));
		if (mDiameterOfEye > THRESHOLD_EYE_SIZE) {
			Log.d(TAG, "find eye.");
		} else {
			Log.d(TAG, "no eye.");
			center.x = x;
			center.y = y;
		}
		return center;
	}

	public int getDiameterofEye() {
		return mDiameterOfEye;
	}

	public static boolean fillBorders(Bitmap bm, int x, int y, int r, Params p) {
		p.x0 = x - r;
		p.y0 = y - r;
		p.x1 = x + r;
		p.y1 = y + r;
		if (p.x0 < 0) {
			p.x0 = 0;
		}
		if (p.y0 < 0) {
			p.y0 = 0;
		}
		if (p.x1 > bm.getWidth() - 1) {
			p.x1 = bm.getWidth() - 1;
		}
		if (p.y1 > bm.getHeight() - 1) {
			p.y1 = bm.getHeight() - 1;
		}
		p.w = p.x1 - p.x0;
		p.h = p.y1 - p.y0;
		
		return (p.w * p.h > 0);
	}

	private Point findCenterOfEye(int[] pixels, int width, int height) {
		int[] gray = new int[width * height];
		boolean[] binary = new boolean[width * height];
		int graySum = 0;
		int sumX = 0;
		int sumY = 0;
		int numOfBlackPixels = 0;
		Point centerOfEye = new Point(0, 0);

		// get gray image
		for (int i = 0; i < width * height; i++) {
			int color = pixels[i];
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = color & 0xFF;
			gray[i] = (int) (0.299 * r + 0.587 * g + 0.114 * b);
			graySum += gray[i];
		}

		// get binary image
		mBinaryThreshold = graySum / (width * height) * 0.6f;
		// Pwog.e("findCenterOfEye", "binaryThreshold" + binaryThreshold);
		for (int i = 0; i < width * height; i++) {
			if (gray[i] >= mBinaryThreshold)
				binary[i] = true; // white
			else {
				binary[i] = false; // black
				sumX += (i % width);
				sumY += (i / width);
				numOfBlackPixels++;
			}
		}
		if (numOfBlackPixels > 0) {
			centerOfEye.x = sumX / numOfBlackPixels;
			centerOfEye.y = sumY / numOfBlackPixels;
		} else {
			centerOfEye.x = width / 2;
			centerOfEye.y = height / 2;
		}
		gray = null;
		binary = null;
		return centerOfEye;
	}

	private float getDiameterOfEye(int[] pixels, int width, int height,
			Point centerOfEye) {
		int[] gray = new int[width * height];
		boolean[] binary = new boolean[width * height];

		// when the pixel(x, y) of the binaryImg is white,
		// we have to find a new point which is very close to (x,y) and is black
		// we save it as (newX, newY) and use it in the function 'findMaxRegion'
		int newX;
		int newY;

		mEyePixelNum = 0;
		mEyeLeftSide = 10000;
		mEyeRightSide = -10000;
		mEyeUpSide = 10000;
		mEyeDownSide = -10000;

		int x = centerOfEye.x;
		int y = centerOfEye.y;
		if (x < 0)
			x = 0;
		if (x > width - 1)
			x = width - 1;
		if (y < 0)
			y = 0;
		if (y > height - 1)
			y = height - 1;

		newX = x;
		newY = y;

		// Pwog.e("getDiameterOfEye", "centerOfEye" + x + " " + y);

		// get gray image
		for (int i = 0; i < width * height; i++) {
			int color = pixels[i];
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = color & 0xFF;
			gray[i] = (int) (0.299 * r + 0.587 * g + 0.114 * b);
		}

		// get binary image
		for (int i = 0; i < width * height; i++) {
			if (gray[i] >= mBinaryThreshold)
				binary[i] = true; // white
			else
				binary[i] = false; // black
		}

		if (binary[y * width + x] == true) // white
		{
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++)
					if (binary[(y - 3 + i) * width + (x - 3 + j)] == false) {
						newX = x - 3 + j;
						newY = y - 3 + i;
					}
			if (binary[newY * width + newX] == true) // still white
			{
				// Pwog.e("getDiameterOfEye", "All While!!!!!");
				gray = null;
				binary = null;
				return 0;
			} else {
				findMaxRegion(newX, newY, binary, width, height);
				if (mEyePixelNum > mEyePixelMaxNum) {
					// Pwog.e("getDiameterOfEye",
					// "Black pixels are too many!!!!!");
					gray = null;
					binary = null;
					return 0;
				} else {
					gray = null;
					binary = null;
					return (float) Math.sqrt((mEyeRightSide - mEyeLeftSide)
							* (mEyeDownSide - mEyeUpSide));
				}
			}
		} else {
			findMaxRegion(x, y, binary, width, height);
			if (mEyePixelNum > mEyePixelMaxNum) {
				gray = null;
				binary = null;
				return 0;
			} else {
				gray = null;
				binary = null;
				return (float) Math.sqrt((mEyeRightSide - mEyeLeftSide)
						* (mEyeDownSide - mEyeUpSide));
			}
		}
	}

	private void findMaxRegion(int x, int y, boolean[] binaryImg, int width,
			int height) {
		Vector<Point> pixelsForSearch = new Vector<Point>();
		int currentPixelX;
		int currentPixelY;
		boolean[] isEyePixel = new boolean[height * width];
		boolean[] searchedPixel = new boolean[height * width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				isEyePixel[i * width + j] = false;
				searchedPixel[i * width + j] = false;
			}

		pixelsForSearch.add(new Point(x, y));
		while (!pixelsForSearch.isEmpty()) {
			currentPixelX = pixelsForSearch
					.get(pixelsForSearch.size() - 1).x;
			currentPixelY = pixelsForSearch
					.get(pixelsForSearch.size() - 1).y;
			pixelsForSearch.remove(pixelsForSearch.size() - 1);
			// Pwog.e("findMaxRegion", "currentPixelX" + currentPixelX +
			// "currentPixelY" + currentPixelY);

			if ((currentPixelX >= width - 1) || (currentPixelX <= 0)
					|| (currentPixelY >= height - 1) || (currentPixelY <= 0))
				continue;

			if (mEyePixelNum > mEyePixelMaxNum)
				return;

			searchedPixel[currentPixelY * width + currentPixelX] = true;

			if (currentPixelX < mEyeLeftSide)
				mEyeLeftSide = currentPixelX;
			if (currentPixelX > mEyeRightSide)
				mEyeRightSide = currentPixelX;
			if (currentPixelY < mEyeUpSide)
				mEyeUpSide = currentPixelY;
			if (currentPixelY > mEyeDownSide)
				mEyeDownSide = currentPixelY;

			if (binaryImg[currentPixelY * width + currentPixelX] == false) // black
			{
				mEyePixelNum++;
				isEyePixel[currentPixelY * width + currentPixelX] = true;

				if (searchedPixel[(currentPixelY + 1) * width + currentPixelX] == false)
					pixelsForSearch.add(new Point(currentPixelX,
							currentPixelY + 1));
				if (searchedPixel[currentPixelY * width + currentPixelX + 1] == false)
					pixelsForSearch.add(new Point(currentPixelX + 1,
							currentPixelY));
				if (searchedPixel[currentPixelY * width + currentPixelX - 1] == false)
					pixelsForSearch.add(new Point(currentPixelX - 1,
							currentPixelY));
				if (searchedPixel[(currentPixelY - 1) * width + currentPixelX] == false)
					pixelsForSearch.add(new Point(currentPixelX,
							currentPixelY - 1));
			}
		}
		// Pwog.e("findMaxRegion", "eyeLeftSide" + eyeLeftSide + "eyeRightSide"
		// + eyeRightSide + "eyeUpSide" + eyeUpSide + "eyeDownSide" +
		// eyeDownSide);
		pixelsForSearch = null;
		isEyePixel = null;
		searchedPixel = null;
	}

	public static class Params {
		public int x0, y0, x1, y1, w, h;
	}

}
