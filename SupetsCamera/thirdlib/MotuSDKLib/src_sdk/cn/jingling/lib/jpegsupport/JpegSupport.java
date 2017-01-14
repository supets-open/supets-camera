package cn.jingling.lib.jpegsupport;

public class JpegSupport {
	static {
		System.loadLibrary("jpeg-support");
	}

	/**
	 * init jpeg reader.
	 * 
	 * @param srcImagePath
	 *            srcImagePath in file_pattern, not in uri_pattern.
	 * @return 0 means success.
	 */
	public native static int initJpegReader(String srcImagePath);

	/**
	 * init jpeg writer. If you give a parameter (such as targetImageWidth
	 * targetImageHeight targetImageQuality) an invalid value, for example
	 * negative numbers. This parameter will take a default value. For
	 * targetImageWidth and targetImageHeight, default values are the same as
	 * your src image's width and heigt. For targetImageQuality, default value
	 * depends on your libjpeg version.
	 * 
	 * @param targetImagePath
	 *            targetImagePath in file_pattern, not in uri_pattern.
	 * @param targetImageWidth -1 as default value
	 * @param targetImageHeight -1 as default value
	 * @param targetImageQuality from 0 to 100       
	 * @return 0 means success.
	 */
	public native static int initJpegWriter(String targetImagePath,
			int targetImageWidth, int targetImageHeight, int targetImageQuality);

	public native static int[] readJpegLines(int read_next_x_lines);

	public native static int writeJpegLines(int[] pixels, int write_next_x_lines);

	/** 释放JpegReader。必须读完全部h行像素后，才能执行此API。
	 * 
	 */
	public native static void finishReadingAndRelease();

	/** 释放JpegWriter。必须写完全部h行像素后，才能执行此API。
	 * 
	 */
	public native static void finishWritingAndRelease();

	public native static int getReaderSrcImageWidth();

	public native static int getReaderSrcImageHeight();

	public native static int getNextReadLine();

	public native static int getNextWriteLine();

}
