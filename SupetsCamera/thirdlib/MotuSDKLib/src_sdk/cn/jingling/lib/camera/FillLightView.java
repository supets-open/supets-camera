package cn.jingling.lib.camera;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Copyright(C) 2013 Baidu.Tech.Co.Ltd. All rights reserved.
 * @Author:Nodin
 * @Description:
 * 
 * @Version:1.0
 * @Update:
 * 
 */
public final class FillLightView extends View {
	
	public FillLightView(Context context) {
		super(context);
		init();
	}

	public FillLightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FillLightView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		setBackgroundColor(Color.WHITE);
		setVisibility(View.GONE);
	}
}
