package cn.jingling.lib.filters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import cn.jingling.lib.file.ExifInfo;
import cn.jingling.lib.file.ExifUtils;
import cn.jingling.lib.file.ImageFile;
import cn.jingling.lib.utils.ErrorHandleHelper;

abstract public class RealsizeFilter extends Filter {
	
	protected int orientation = 0;
	private static final String TAG = "Realsize";
	protected Bitmap mPoster = null;
	protected float mPosterRangeTop = 0.0f;
	protected float mPosterRangeBottom = 1.0f;
	protected float mPosterRangeLeft = 0.0f;
	protected float mPosterRangeRight = 1.0f;
	
	public void setPoster(Bitmap poster) {
		mPoster = poster;
	}
	
	/** 设置Poster贴图范围。默认为0.0f ~ 1.0f
	 * @param begin
	 * @param end
	 */
	public void setPosterRange(float top, float bottom, float left, float right) {
		if (top < 0.0f) {
			top = 0.0f;
		}
		if (bottom > 1.0f) {
			bottom = 1.0f;
		}
		
		if (top > bottom) {
			top = bottom;
		}
		
		if (left < 0.0f) {
			left = 0.0f;
		}
		if (right > 1.0f) {
			right = 1.0f;
		}
		
		if (left > right) {
			left = right;
		}
		
		mPosterRangeTop = top;
		mPosterRangeBottom = bottom;
		mPosterRangeLeft = left;
		mPosterRangeRight = right;
	}
	
	/** 
	 * @param cx
	 * @param inPath
	 * @param outPath
	 * @param args 如需先flip再使用滤镜，请设置args[0] = RSLineFilter.NEED_FLIP
	 */
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		Uri uri = Uri.fromFile(new File(inPath));
		orientation = ImageFile.getImageOrientation(cx, uri);
		return true;
	}
	
	
	/**
	 * Compute an image orientation after flip horizontal.
	 * @param originalOrientation the orientation of the original image
	 * @return the orientation after flip horizontal
	 */
	public static int getFilpOrientation(int originalOrientation) {
		switch (originalOrientation) {
		case 90:
			return 270;
		case 180:
			return 180;
		case 270:
			return 90;
		}
		return 0;
	}
	
	protected void setExif(String inPath, String outPath) {
		ExifInfo exifInfo = ExifUtils.getFileExifInfo(inPath);
		ExifUtils.saveExifToFile(outPath, exifInfo);
	}
	
	protected boolean checkJpg(String filePath) {
		FileInputStream inputStream = null;
		try {
			// 从SDCARD下读取一个文件
			inputStream = new FileInputStream(filePath);
			byte[] buffer = new byte[2];
			// 文件类型代码
			String filecode = "";

			if (inputStream.read(buffer) == -1) {
				ErrorHandleHelper.handleErrorMsg("Realsize error : inPath file is an empty file !", TAG);
				return false;
			}

			// 通过读取出来的前两个字节来判断文件类型
			for (int i = 0; i < buffer.length; i++) {
				// 获取每个字节与0xFF进行与运算来获取高位，这个读取出来的数据不是出现负数
				// 并转换成字符串
				filecode += Integer.toString((buffer[i] & 0xFF));
			}
			// 把字符串再转换成Integer进行类型判断

			int codeType = Integer.parseInt(filecode);
			
			if (codeType == 255216) {
				return true;
			} else {
				ErrorHandleHelper.handleErrorMsg("Realsize error : inPath file is not a Jpg file !", TAG);
				return false;
			}

		} catch (FileNotFoundException e) {
			ErrorHandleHelper.handleErrorMsg("Realsize error : inPath file doesn't exist !", TAG);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
