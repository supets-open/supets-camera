package cn.jingling.lib.textbubble;

import android.view.MotionEvent;

public class PwMotionHigh extends PwMotion {
	public PwMotionHigh() {
		super();
	}

	@Override
	public double getX(int pointerIndex) {
		return me.getX(pointerIndex);
	}

	@Override
	public double getY(int pointerIndex) {
		return me.getY(pointerIndex);

	}

	@Override
	public int getPointerCount() {
		return me.getPointerCount();
	}

	@Override
	public int getPointerId(int i) {
		return me.getPointerId(i);
	}

	@Override
	public int getAction() {
		if (me.getAction() == MotionEvent.ACTION_POINTER_1_UP
				|| me.getAction() == MotionEvent.ACTION_POINTER_2_UP) {
			return MotionEvent.ACTION_UP;
		}
		if (me.getAction() == MotionEvent.ACTION_POINTER_1_DOWN
				|| me.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
			return MotionEvent.ACTION_DOWN;
		}
		return me.getAction();
	}

	@Override
	public int findPointerIndex(int pointerId) {
		return me.findPointerIndex(pointerId);
	}
	
	@Override
	public double getXStillDown() {
		if (me.getPointerCount() == 2) {
			if (me.getAction() == MotionEvent.ACTION_UP) {
				int idx = me.getActionIndex();
				if (idx == 0) {
					return getX(1);
				} else {
					return getX(0);
				}
			}
		}
		return me.getX();
	}
	
	public double getYStillDown() {
		if (me.getPointerCount() == 2) {
			if (me.getAction() == MotionEvent.ACTION_UP) {
				int idx = me.getActionIndex();
				if (idx == 0) {
					return getY(1);
				} else {
					return getY(0);
				}
			}
		}
		return me.getY();
	}
}
