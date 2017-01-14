package cn.jingling.lib.livefilter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView {
	private SurfaceHolder mHolder;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public CameraPreview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mHolder = getHolder();
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

}
