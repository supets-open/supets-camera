package cn.jingling.lib.filters.realsize;

import android.content.Context;

public class FlipUtil {
	private static void swap(int[] data, int a, int b) {
		if (data[a] != data[b]) {
			data[a] ^= data[b];
			data[b] ^= data[a];
			data[a] ^= data[b];
		}
	}
	
	public static void flipLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
		int w = pixels.length;
		for (int j = 0; j < w / 2; j ++) {
			swap(pixels, j, w - j - 1);
		}
	}
}
