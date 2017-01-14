package cn.jingling.lib.camera;

import android.os.Handler;

/**
 * @Copyright(C) 2013 Baidu.Tech.Co.Ltd. All rights reserved.
 * @Author:Nodin
 * @Description:
 * 
 * @Version:1.0
 * @Update:
 * 
 */
public abstract class WonderCamCap {
	protected Handler mHandler;
	
	public WonderCamCap(Handler handler) {
		this.mHandler = handler;
	}
}
