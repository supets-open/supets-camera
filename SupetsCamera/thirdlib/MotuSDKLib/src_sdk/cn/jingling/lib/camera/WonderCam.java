package cn.jingling.lib.camera;

import java.util.WeakHashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

/**
 * @Copyright(C) 2013 Baidu.Tech.Co.Ltd. All rights reserved.
 * @Author:Nodin
 * @Description:
 * 
 * @Version:1.0
 * @Update:
 * 
 */
public class WonderCam {
	public static final int TYPE_FILLLIGHT = 0;
	public static final int TYPE_EYEBLINK = 1;
	public static final int TYPE_TOUCHCAP = 2;
	public static final int TYPE_DELAY = 3;
	
	private static final String TAG = "WonderCam";
	
	private Handler mHandler;
	private static WeakHashMap<Context, WonderCam> sMap = new WeakHashMap<Context, WonderCam>();
	private SparseArray<WonderCamCap> mWonderCamMap;
	
	class WonderCameraHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}
	
	private WonderCam(Context cx) {
		mHandler = new WonderCameraHandler();
		mWonderCamMap = new SparseArray<WonderCamCap>(4);
		synchronized (sMap) {
			sMap.put(cx.getApplicationContext(), this);	
		}
	}
	
	public static WonderCam get(Context cx) {
		WonderCam obj = null;
		synchronized (sMap) {
			obj = sMap.get(cx.getApplicationContext());
		}
		
		if (null == obj) {
			obj = new WonderCam(cx.getApplicationContext()); 
		}
		
		return obj;
	}
	
	public static void destroy(Context cx) {
		synchronized (sMap) {
			sMap.remove(cx.getApplicationContext());
		}
	}
	
	public WonderCamCap getWonderCameraCap(int type) {
		Object obj = mWonderCamMap.get(type);
		if (null != obj) {
			if (obj instanceof WonderCamCap) {
				return (WonderCamCap) obj;
			} else {
				mWonderCamMap.remove(type);
			}
		}
		
		WonderCamCap instance = getInstanceByType(type);
		mWonderCamMap.put(type, instance);
		
		return instance;
	}
	
	private WonderCamCap getInstanceByType(int type) {
		WonderCamCap instance = null;
		
		switch (type) {
		case WonderCam.TYPE_FILLLIGHT:
			instance = new FillLightCap(mHandler);
			break;
		case WonderCam.TYPE_TOUCHCAP:
			instance = new TouchCap(mHandler);
			break;
		case WonderCam.TYPE_DELAY:
			instance = new DelayCap(mHandler);
			break;
		}
		
		return instance;
	}
}
