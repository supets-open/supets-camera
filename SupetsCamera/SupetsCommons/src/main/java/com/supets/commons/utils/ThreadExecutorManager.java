package com.supets.commons.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutorManager {

	private static ExecutorService mPool = Executors.newSingleThreadExecutor();
    
	public  static ExecutorService  getSinglePool(){
		return mPool;
	}
	
	public static void execute(Runnable callback){
		getSinglePool().execute(callback);	
	}
	
}
