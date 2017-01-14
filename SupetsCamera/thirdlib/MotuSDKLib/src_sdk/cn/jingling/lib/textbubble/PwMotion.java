package cn.jingling.lib.textbubble;

import android.view.MotionEvent;

public class PwMotion {
	protected MotionEvent me;

	public PwMotion() {
	}

	public void setEvent(MotionEvent m) {
		me = m;
	}

	public float getX() {
		return me.getX();
	}

	public float getY() {
		return me.getY();
	}

	public float getRawX() {
		return me.getRawX();
	}

	public float getRawY() {
		return me.getRawY();
	}

	public double getX(int pointerIndex) {
		return me.getX();
	}

	public double getY(int pointerIndex) {
		return me.getY();
	}

	public int getAction() {
		return me.getAction();
	}

	public int getPointerCount() {
		return 1;
	}

	public int getPointerId(int i) {
		return 0;
	}

	public int findPointerIndex(int pointerId) {
		return -1;
	}
	
	public double getXStillDown() {
		return me.getX();
	}
	
	public double getYStillDown() {
		return me.getY();
	}
}
