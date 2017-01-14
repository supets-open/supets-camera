package cn.jingling.lib.file;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;

public class ExifUtils {

	@SuppressLint({ "InlinedApi", "SimpleDateFormat" })
	public static boolean saveExifToFile(String path, ExifInfo exif) {
		try {
			ExifInterface newExif = new ExifInterface(path);
			if (newExif != null && exif != null) {
				// 值为空时候，可能会在ExifInterface源码里面包null异常
				setAttribute(newExif, ExifInterface.TAG_IMAGE_LENGTH,
						exif.imageLength);
				setAttribute(newExif, ExifInterface.TAG_IMAGE_WIDTH,
						exif.imageWidth);
				setAttribute(newExif, ExifInterface.TAG_MAKE, exif.make);
				setAttribute(newExif, ExifInterface.TAG_ORIENTATION,
						exif.orientation);
				setAttribute(newExif, ExifInterface.TAG_MODEL, exif.model);
				setAttribute(newExif, ExifInterface.TAG_FLASH, exif.flash);
				setAttribute(newExif, ExifInterface.TAG_WHITE_BALANCE,
						exif.whiteBalance);
				setAttribute(newExif, ExifInterface.TAG_GPS_LATITUDE,
						exif.latitude);
				setAttribute(newExif, ExifInterface.TAG_GPS_LATITUDE_REF,
						exif.latitudeRef);
				setAttribute(newExif, ExifInterface.TAG_GPS_LONGITUDE,
						exif.longitude);
				setAttribute(newExif, ExifInterface.TAG_GPS_LONGITUDE_REF,
						exif.longitudeRef);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					setAttribute(newExif, ExifInterface.TAG_APERTURE,
							exif.apertrue);
					setAttribute(newExif, ExifInterface.TAG_ISO, exif.iso);
					setAttribute(newExif, ExifInterface.TAG_EXPOSURE_TIME,
							exif.exposureTime);
				}
			}
			// 强制设置exif时间信息为当前系统时间,部分手机无效，但是不影响效果
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy:MM:dd HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String str = formatter.format(curDate);
			newExif.setAttribute(ExifInterface.TAG_DATETIME, str);
			newExif.saveAttributes();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void setAttribute(ExifInterface exif, String attr,
			String value) {
		if (!TextUtils.isEmpty(value)) {
			exif.setAttribute(attr, value);
		}
	}

	/**
	 * 根据文件路径，获取该文件的ExifInfo信息
	 * 
	 * @param path
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static ExifInfo getFileExifInfo(String path) {
		ExifInfo exifInfo = new ExifInfo();
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (exif != null) {
			exifInfo.imageLength = exif
					.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
			exifInfo.imageWidth = exif
					.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
			exifInfo.make = exif.getAttribute(ExifInterface.TAG_MAKE);
			exifInfo.orientation = exif
					.getAttribute(ExifInterface.TAG_ORIENTATION);
			exifInfo.model = exif.getAttribute(ExifInterface.TAG_MODEL);
			exifInfo.datetime = exif.getAttribute(ExifInterface.TAG_DATETIME);
			exifInfo.flash = exif.getAttribute(ExifInterface.TAG_FLASH);
			exifInfo.whiteBalance = exif
					.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
			exifInfo.latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			exifInfo.latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
			exifInfo.longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			exifInfo.longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				exifInfo.iso = exif.getAttribute(ExifInterface.TAG_ISO);
				exifInfo.apertrue = exif
						.getAttribute(ExifInterface.TAG_APERTURE);
				exifInfo.exposureTime = exif
						.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
			}
		}

		return exifInfo;
	}

	/**
	 * 
	 * @param path
	 * @param orientation
	 *            0, 90, 180, 270
	 */
	public static void setExifOrientation(String path, int orientation) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(path);
			int o = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
			orientation = Shared.degreesToExifOrientation(orientation);
			if (o != orientation) {
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation
						+ "");
				exif.saveAttributes();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
