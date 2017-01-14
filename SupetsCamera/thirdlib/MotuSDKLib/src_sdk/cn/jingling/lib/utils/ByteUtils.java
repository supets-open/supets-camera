package cn.jingling.lib.utils;

public class ByteUtils {
	public static int byteToInt(byte b) {
		return b >= 0 ? b : b + 256;
	}
}
