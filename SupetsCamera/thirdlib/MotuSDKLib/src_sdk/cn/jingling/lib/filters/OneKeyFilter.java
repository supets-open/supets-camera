package cn.jingling.lib.filters;

import android.content.Context;
import android.graphics.Bitmap;

abstract public class OneKeyFilter extends Filter {
	abstract public Bitmap apply(Context cx, Bitmap bm);
	public Bitmap apply(Context cx, Bitmap bm, int[] args) {
		return apply(cx, bm);
	}
	
	protected void statisticEvent() {
		String label = this.getClass().getSimpleName();
	}
}
