package cn.jingling.lib.file;

import java.io.Serializable;

/**
 * ExifInterface信息的封装类 用于其他模块调用MotuSDK时传递需要存储得exif信息
 * 
 * @author yutinglong
 */
public class ExifInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7445923159249560122L;
	
	public String orientation;
	public String make;
	public String imageWidth;
	public String imageLength;
	public String model;
	public String iso;
	public String apertrue;
	public String exposureTime;
	public String flash;
	public String whiteBalance;
	public String datetime;
	public String latitude;
	public String latitudeRef;
	public String longitude;
	public String longitudeRef;

}
