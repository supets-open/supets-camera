package cn.jingling.lib.filters.realsize;

import android.content.Context;

/**
 * 
 * @author jiankun.zhi
 */
public class RSFlipHorizontal extends RSLineFilter {
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		return super.apply(cx, inPath, outPath, args);
	}

	@Override
	public void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
		FlipUtil.flipLine(cx, pixels, line, height);
	}
	
	@Override
	protected void releaseLayers() {
		// TODO Auto-generated method stub
		
	}
	
}
