package cn.jingling.lib.utils;

import java.util.HashMap;

public class TimeChecker {
	
	private static final String TAG = "TimeChecker";
	
	private static HashMap<String, Checker> mCheckerMap = new HashMap<String, Checker>();
	
	public static void start(String label) {
		long time = System.currentTimeMillis();
		Checker checker = new Checker();
		checker.start = checker.last = time;
		checker.num = 1;
		mCheckerMap.put(label, checker);
	}
	
	public static void check(String label) {
		long time = System.currentTimeMillis();
		Checker checker = mCheckerMap.get(label);
		LogUtils.d(TAG, String.format("%s-%d: %d", label, checker.num ++, (int)(time - checker.last)));
		checker.last = time;
	}
	
	public static void end(String label) {
		long time = System.currentTimeMillis();
		Checker checker = mCheckerMap.get(label);
		LogUtils.d(TAG, String.format("Whole time -> %s-%d: %d", label, checker.num ++, (int)(time - checker.start)));
		mCheckerMap.remove(checker);
	}
	
	private static class Checker {
		private long start;
		private long last;
		private int num;
	}
	
}
