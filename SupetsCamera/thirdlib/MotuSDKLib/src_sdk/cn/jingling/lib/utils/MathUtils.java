package cn.jingling.lib.utils;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

public class MathUtils {
	public static int sqr(int a) {
		return a * a;
	}
	
	public static double sqr(double a) {
		return a * a;
	}
	
	public static int dist(int ax, int ay, int bx, int by) {
		return (int) Math.sqrt(sqr(ax - bx) + sqr(ay - by));
	}
	
	public static double dist(double ax, double ay, double bx, double by) {
		return Math.sqrt(sqr(ax - bx) + sqr(ay - by));
	}
	
	public static float dist(float ax, float ay, float bx, float by) {
		return (float)Math.sqrt(sqr(ax - bx) + sqr(ay - by));
	}
	
	public static int dist(Point a, Point b) {
		return dist(a.x, a.y, b.x, b.y);
	}
	
	public static float dist(PointF a, PointF b) {
		return dist(a.x, a.y, b.x, b.y);
	}
	
	public static int findMax(int number1,int number2,int number3)
	{
		int maxNumber = number1;
		if(number2 > maxNumber)
		{
			maxNumber = number2;
		}
		if(number3 > maxNumber)
		{
			maxNumber = number3;
		}
		return maxNumber;
	}
	
	public static int findMin(int number1,int number2,int number3)
	{
		int minNumber = number1;
		if(number2 < minNumber)
		{
			minNumber = number2;
		}
		if(number3 < minNumber)
		{
			minNumber = number3;
		}
		return minNumber;
	}
	
	
	public static int toInt(Byte b) {
		return b >= 0 ? b : b + 256;
	}
	
	public static int nextPowerOfTwo(int x) {
		int r = 1;
		while (r < x) {
			r = r * 2;
		}
		return r;
	}
	
	public static float clamp(float v, float min, float max) {
		return Math.min(Math.max(v, min), max);
	}

	public static int clamp(int v, int min, int max) {
		return Math.min(Math.max(v, min), max);
	}

	/**
	 * 
	 * @param rect will be changed to result
	 * @param dst make rect in dst
	 * @return rect for convenience.
	 */
	public static Rect clamp(Rect rect, Rect dst) {
		rect.left = clamp(rect.left, dst.left, dst.right);
		rect.right = clamp(rect.right, dst.left, dst.right);
		rect.top = clamp(rect.top, dst.top, dst.bottom);
		rect.bottom = clamp(rect.bottom, dst.top, dst.bottom);
		return rect;
	}

}
