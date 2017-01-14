
package com.supets.commons.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutorManager {

	private static class PoolHolder{
		private static final ExecutorService mPool = Executors.newSingleThreadExecutor();
	}

	public  static ExecutorService  getSinglePool(){
		return PoolHolder.mPool;
	}
	
	public static void execute(Runnable callback){
		getSinglePool().execute(callback);	
	}
	
}
