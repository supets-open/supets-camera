package cn.jingling.lib.filters.onekey;

import android.graphics.Point;
import cn.jingling.lib.filters.CMTProcessor;

public class IntelligentBeautify{

	private static final double FACE_WIDTH_COEF = 2; // the bigger, the wider
	private static final double FACE_INNER_WIDTH_COEF = 18; // the bigger, the
															// wider, from 1 to
															// maxint

	public static void partialFaceProcess(int[] bm, int w, int h, Point left, Point right,
			Point mouth, int eyeRatio, int thinRatio) {
		int eyeDist = Math.abs(left.x - right.x);
		enlarge(bm, w, h, left.x, left.y, eyeDist / 2, eyeRatio);
		enlarge(bm, w, h, right.x, right.y, eyeDist / 2, eyeRatio);
		if (thinRatio < 0) {
			Point negMouth = new Point();
			negMouth.y = ((left.y + right.y) + mouth.y) / 3;
			negMouth.x = ((left.x + right.x) + mouth.x) / 3;
			updateThin(bm, w, h, thinRatio, left, right, negMouth);
		} else {
			updateThin(bm, w, h, thinRatio, left, right, mouth);
		}
	}

	private static void enlarge(int[] bm, int w, int h, int x, int y, int r, int degree) {
		int[] pixels;
		int d = r * 2;
		x = x - r;
		y = y - r;
		pixels = getFullPixels(bm, w, h, x, y, d);
		CMTProcessor.eyeEnlarge(pixels, d, d, r, r, r, degree / 280f);
		setFullPixels(bm, w, h, pixels, x, y, d);
		pixels = null;
	}

	public static int[] getFullPixels(int[] bm, int w, int h, int x0, int y0, int d) {
		int pixels[] = new int[d * d];
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				int px = x0 + j;
				int py = y0 + i;
				if (px < 0 || py < 0 || px >= w || py >= h) {
					pixels[i * d + j] = 0;
				} else {
					pixels[i * d + j] = bm[py * w + px];
				}
			}
		}
		return pixels;
	}

	public static void setFullPixels(int[] bm, int w, int h, int[] pixels, int x0, int y0,
			int d) {
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				int px = x0 + j;
				int py = y0 + i;
				if (px < 0 || py < 0 || px >= w || py >= h
						|| pixels[i * d + j] == 0) {
					// do nothing
				} else {
					bm[py * w + px] = pixels[i * d + j];
				}
			}
		}
	}

	private static void thin(int[] bm, int w, int h, int cx, int cy, int x, int y, int degree) {
		// we need a square, not rectangle
		int r = Math.max(Math.abs(x - cx), Math.abs(y - cy));
		int d = 2 * r;

		// start point of the thin square
		int x0 = cx - r;
		int y0 = cy - r;

		// vector in square
		int vx = (x - cx) / 7 + r;
		int vy = (y - cy) / 7 + r;
		int[] pixels = getFullPixels(bm, w, h, x0, y0, d);
		CMTProcessor.thinEffect(pixels, d, d, r, r, vx, vy, r,
				degree / 100f * 0.8f, 1);
		setFullPixels(bm, w, h, pixels, x0, y0, d);
		pixels = null;
	}

	private static void updateThin(int[] bm, int w, int h, int progressThin, Point left,
			Point right, Point mouth) {
		// fetch the coordinates of the 3 points on the face
		final int x1 = left.x;
		final int y1 = left.y;
		final int x2 = right.x;
		final int y2 = right.y;
		// center of face, point C
		// int cx = (x1 + x2 + x3) / 3;
		// int cy = (y1 + y2 + y3) / 3;
		// the target point to push at, Point C
		int ecx = (x1 + x2) / 2;
		int ecy = (y1 + y2) / 2;
		final int x3 = (mouth.x * 4 - ecx) / 3;
		final int y3 = (mouth.y * 4 - ecy) / 3;
		int cx = x3;
		int cy = y3;
		// bottom center of face
		// int dx = (6 * x3 - x1 - x2) / 4;
		// int dy = (6 * y3 - y1 - y2) / 4;
		// left bottom and right bottom
		int ax, ay, bx, by;
		int ex, ey, fx, fy;

		// most left point on the line through two eyes' points(Point E)
		ex = (int) (x1 - (x2 - x1) / FACE_WIDTH_COEF);
		ey = (int) (y1 + (y1 - y2) / FACE_WIDTH_COEF);
		// most right point on the line through two eyes' points(Point F)
		fx = (int) (x2 + (x2 - x1) / FACE_WIDTH_COEF);
		fy = (int) (y2 - (y1 - y2) / FACE_WIDTH_COEF);
		// special situation
		if (x1 == x2) {
			ax = x3;
			bx = ax;
			ay = (int) (y1 + (y1 - y2) / FACE_WIDTH_COEF);
			by = (int) (y2 - (y1 - y2) / FACE_WIDTH_COEF);
		} else if (y1 == y2) {
			ax = (int) (x1 - (x2 - x1) / FACE_WIDTH_COEF);
			bx = (int) (x2 + (x2 - x1) / FACE_WIDTH_COEF);
			ay = y3;
			by = ay;
		} else {
			// use 3 lines to compute the left bottom point and the right bottom
			// point of the face
			// y = k1 * x + b1 the line parallels with the line through two
			// eyes' points, through the bottom
			// y = k2 * x + b2 the line through the most left point and the left
			// bottom point
			// y = k3 * x + b3 the line through the most right point and the
			// right bottom point
			// compute k1, k2, k3 and b1, b2, b3
			double k1 = (double) (y1 - y2) / (x1 - x2);
			int xec = (x1 + x2) / 2;
			int yec = (y1 + y2) / 2;
			double k2;
			if (xec == x3) {
				k2 = -1 / k1;
			} else {
				k2 = 1f * (yec - y3) / (xec - x3);
			}
			double k3 = k2;
			double b2 = ey - k2 * ex;
			double b3 = fy - k2 * fx;
			// inclined face
			double k4 = k1;
			double k5 = k1;
			double b4 = y3 - k4 * x3;
			double b5 = y3 - k5 * x3;

			// left bottom, point A
			ax = (int) (-(b4 - b2) / (k4 - k2));
			ay = (int) (k4 * ax + b4);
			// right bottom, point B
			bx = (int) (-(b5 - b3) / (k5 - k3));
			by = (int) (k5 * bx + b5);
		}
		int cax = (int) (cx - (cx - ax) / FACE_INNER_WIDTH_COEF);
		int cay = (int) (cy - (cy - ay) / FACE_INNER_WIDTH_COEF);
		int cbx = (int) (cx - (cx - bx) / FACE_INNER_WIDTH_COEF);
		int cby = (int) (cy - (cy - by) / FACE_INNER_WIDTH_COEF);
		//Point D: center between left cheekbone and right cheekbone
		int dx = (4 * (x1 + x2) / 2 + x3) / 5;
		int dy = (4 * (y1 + y2) / 2 + y3) / 5;
		//Point G: left cheekbone
		int gx = (ex * 3 + ax) / 4;
		int gy = (ey * 3 + ay) / 4;
		//move to left cheekbone
		gx = (int) (gx - (dx - gx) / FACE_INNER_WIDTH_COEF);
		gy = (int) (gy - (dy - gy) / FACE_INNER_WIDTH_COEF);
		//Point H: right cheekbone
		int hx = (fx * 3 + bx) / 4;
		int hy = (fy * 3 + by) / 4;
		//move to right cheekbone
		hx = (int) (hx - (dx - hx) / FACE_INNER_WIDTH_COEF);
		hy = (int) (hy - (dy - hy) / FACE_INNER_WIDTH_COEF);
		
		// thin
		if (progressThin < 0) {
			thin(bm, w, h, ax, ay, 2 * ax - cax, 2 * ay - cay, -progressThin); // left bottom
			thin(bm, w, h, bx, by, 2 * bx - cbx, 2 * by - cby, -progressThin); // right bottom
		} else {
			thin(bm, w, h, gx, gy, (dx * 2 + gx) / 3, (dy * 2 + gy) / 3, progressThin / 2); //left cheekbone thin
			thin(bm, w, h, hx, hy, (dx * 2 + hx) / 3, (dy * 2 + hy) / 3, progressThin / 2); //right cheekbone thin
			thin(bm, w, h, ax, ay, cax, cay, progressThin); // left bottom
			thin(bm, w, h, bx, by, cbx, cby, progressThin); // right bottom
		}
	}

}
