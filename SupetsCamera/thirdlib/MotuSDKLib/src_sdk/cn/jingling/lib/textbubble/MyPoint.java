package cn.jingling.lib.textbubble;

import android.graphics.Matrix;
import android.graphics.Point;

public class MyPoint {
	public float x;
	public float y;

	public Point toPoint() {
		Point p = new Point();
		p.x = (int)x;
		p.y = (int)y;
		return p;
	}
	
	public MyPoint() {
		x = 0;
		y = 0;
	}

	public MyPoint(float x1, float y1) {
		x = x1;
		y = y1;
	}

	public MyPoint(double x1, double y1) {
		x = (float) x1;
		y = (float) y1;
	}

	public MyPoint(MyPoint src) {
		x = src.x;
		y = src.y;
	}

	public void set(float x1, float y1) {
		x = x1;
		y = y1;
	}

	public void set(double x1, double y1) {
		x = (float) x1;
		y = (float) y1;
	}

	public void set(MyPoint src) {
		x = src.x;
		y = src.y;
	}
	
	public static Point givePointAfterTransform(Point point, Matrix matrix) {

		float[] values = new float[9];

		matrix.getValues(values);
		int x1 = (int)(values[0] * point.x + values[1] * point.y + values[2]);
		int y1 = (int)(values[3] * point.x + values[4] * point.y + values[5]);

		return new Point(x1, y1);
	}
	
	public static Point givePointBeforeTransform(Point point, Matrix matrix) {

		Matrix inverseMatrix = new Matrix();
		matrix.invert(inverseMatrix);

		return givePointAfterTransform(point, inverseMatrix);
	}

	public MyPoint givePointAfterTransform(Matrix matrix) {

		float[] values = new float[9];

		matrix.getValues(values);
		float x1 = values[0] * x + values[1] * y + values[2];
		float y1 = values[3] * x + values[4] * y + values[5];

		return new MyPoint(x1, y1);
	}

	public MyPoint givePointBeforTransform(Matrix matrix) {

		Matrix inverseMatrix = new Matrix();
		matrix.invert(inverseMatrix);

		return givePointAfterTransform(inverseMatrix);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return " " + x + " " + y;
	}

	public void setScale(float scale) {
		x *= scale;
		y *= scale;
	}

	public MyPoint rotate(float angle) {
		float x1 = x * (float) Math.cos(angle) - y * (float) Math.sin(angle);
		float y1 = x * (float) Math.sin(angle) + y * (float) Math.cos(angle);
		return new MyPoint(x1, y1);
	}

	public MyPoint add(MyPoint rhs) {
		float x1 = x + rhs.x;
		float y1 = y + rhs.y;
		return new MyPoint(x1, y1);
	}

	public static float xmul(MyPoint p1, MyPoint p2, MyPoint p0) {
		return (p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y);
	}

	public static Boolean oppositeSide(MyPoint u1, MyPoint u2, MyPoint v1,
			MyPoint v2) {
		return (xmul(v1, u1, v2) * xmul(v1, u2, v2) < 0);
	}

	public static Boolean parallel(MyPoint u1, MyPoint u2, MyPoint v1,
			MyPoint v2) {
		return ((u1.x - u2.x) * (v1.y - v2.y) - (v1.x - v2.x) * (u1.y - u2.y) == 0.0);
	}

	public static Boolean isIntersection(MyPoint u1, MyPoint u2, MyPoint v1,
			MyPoint v2) {

		return (oppositeSide(u1, u2, v1, v2) && oppositeSide(v1, v2, u1, u2));
	}

	public static Boolean intersection(MyPoint u1, MyPoint u2, MyPoint v1,
			MyPoint v2, MyPoint res) {

		if (parallel(u1, u2, v1, v2) == true) {
			res = new MyPoint(v2);
			return true;
		}

		if (isIntersection(u1, u2, v1, v2) == false) {
			return false;
		}

		res.set(u1);

		float t = ((u1.x - v1.x) * (v1.y - v2.y) - (u1.y - v1.y)
				* (v1.x - v2.x))
				/ ((u1.x - u2.x) * (v1.y - v2.y) - (u1.y - u2.y)
						* (v1.x - v2.x));
		res.x += (u2.x - u1.x) * t;
		res.y += (u2.y - u1.y) * t;

		return true;
	}

	public static float distance(MyPoint p1, MyPoint p2) {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;

		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	public static float distance(Point p1, Point p2) {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;

		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public static MyPoint midPoint(MyPoint point1, MyPoint point2) {
		MyPoint point = new MyPoint();
		float x = point1.x + point2.x;
		float y = point1.y + point2.y;
		point.set(x / 2, y / 2);
		return point;
	}

	public static MyPoint getSinCos(MyPoint point1, MyPoint point2,
			MyPoint point0) {
		float x1 = point1.x - point0.x;
		float y1 = point1.y - point0.y;
		float x2 = point2.x - point0.x;
		float y2 = point2.y - point0.y;

		float d1 = (float) (Math.sqrt(x1 * x1 + y1 * y1));
		float d2 = (float) (Math.sqrt(x2 * x2 + y2 * y2));

		float dd = d1 * d2;

		float sin = (x1 * y2 - x2 * y1) / dd;
		float cos = (x1 * x2 + y1 * y2) / dd;

		return new MyPoint(sin, cos);
	}

	/**
	 * the values of the transform matrix [scale * cos( alpha ) -scale * sin(
	 * alpha ) dx; scale * sin( alpha ) scale * cos( alpha ) dy; 0 0 1.0]
	 */

	public static MyPoint getSinCos(Matrix matrix) {

		float[] values = new float[9];
		matrix.getValues(values);

		float scale = (float) Math.sqrt(values[0] * values[0] + values[1]
				* values[1]);

		float sin = -values[1] / scale;
		float cos = values[0] / scale;

		return new MyPoint(sin, cos);
	}
	
	public static MyPoint getVector(MyPoint firstPoint,MyPoint secondPoint) {
		MyPoint vectorPointer = new MyPoint();
		vectorPointer.x = secondPoint.x - firstPoint.x;
		vectorPointer.y = secondPoint.y - firstPoint.y;
		return vectorPointer;
	}

	public void add(float x, float y){
		this.x += x;
		this.y += y;
	}
}