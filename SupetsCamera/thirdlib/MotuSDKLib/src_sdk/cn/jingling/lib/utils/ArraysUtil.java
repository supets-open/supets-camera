package cn.jingling.lib.utils;

public class ArraysUtil {

	public static int[] copyOf(int[] src) {
		int[] dst = new int[src.length];
		for (int i = 0; i < src.length; i ++) {
			dst[i] = src[i];
		}
		return dst;
	}
	
	public static int[] copyOf(String[] src) {
		int[] dst = new int[src.length];
		for (int i = 0; i < src.length; i ++) {
			dst[i] = Integer.parseInt(src[i]);
		}
		return dst;
	}
	
	public static String toString(int[] src, String split) {
		if (src == null || src.length < 1) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < src.length; i++) {
			builder.append(src[i]);
			if (i != (src.length - 1)) {
				builder.append(split);
			}
		}
		
		return builder.toString();
	}
	
	public static int[] toArray(String src, String split) {
		String[] strArray = src.split(split);
		return ArraysUtil.copyOf(strArray);
	}
}
