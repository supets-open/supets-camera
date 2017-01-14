package cn.jingling.lib.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import cn.jingling.lib.utils.LogUtils;
import cn.jingling.lib.utils.RotateOrFlipType;

public class ImageFile {

	private final static String TAG = "ImageFile";

	public final static int OTHER_ERROR = -1;

	public final static int FILE_NOT_EXSIT = -2;

	public final static int SUCCESSED = 0;

	public final static int URI_NOT_EXSIT = -3;

	public final static int FILE_PATH_NOT_EXIST = -4;

	public final static int OUT_OF_MEMORY = -5;

	public final static int STILL_RUNNING = -6;

	public final static int SDCARD_FULL = -7;

	public final static long MIN_SD_CARD_SPACE = 5 * 1024 * 1024L;

	public final static int TYPE_JPG = 0;

	public final static int TYPE_PNG = 1;

	public final static int ORIGINAL_SIZE = 9999;

	public final static int JPEG_QUALITY = 100;

	private static final int INDEX_ORIENTATION = 3;

	private static final String WHERE_CLAUSE = "(" + Media.MIME_TYPE
			+ " in (?, ?, ?))";

	private static final String[] ACCEPTABLE_IMAGE_TYPES = new String[] {
			"image/jpeg", "image/png", "image/gif" };

	private static final String[] IMAGE_PROJECTION = new String[] { Media._ID,
			Media.DATE_TAKEN, Media.DATE_ADDED, Media.ORIENTATION, Media.DATA };

	private static Set<ThreadNote> sImageTaskQueue = new HashSet<ThreadNote>();
	private static final int MAX_CONCURRENT_THREAD_NUM = 1;
	private ThreadNote mThreadNote;

	private OnFileLoadedListener mOnFileLoadedListener;

	private OnFileSavedListener mOnFileSavedListener;

	private OnSampleFileListener mOnSampleFileListener;

	private LoadTask mLoadTask;

	private SaveTask mSaveTask;

	private SampleFileTask mSampleFileTask;

	/**
	 * Load an image from Gallery, will generate a Bitmap in the callback
	 * method.
	 * 
	 * @param context
	 * @param uri
	 *            Uri of the image to be opened
	 * @param width
	 *            The width of the result Bitmap will not exceed this width
	 * @param height
	 *            The height of the result Bitmap will not exceed this height
	 * @param l
	 *            Callback listener
	 * @return Can be ignored
	 */
	public int loadImageAsync(Context context, Uri uri, int width, int height,
			OnFileLoadedListener l) {
		return loadImageAsync(context, uri, width, height, null, l);
	}

	/**
	 * Load an image from Gallery, will generate a Bitmap in the callback
	 * method.
	 * 
	 * @param context
	 * @param uri
	 *            Uri of the image to be opened
	 * @param width
	 *            The width of the result Bitmap will not exceed this width
	 * @param height
	 *            The height of the result Bitmap will not exceed this height
	 * @param l
	 *            Callback listener
	 * @param tag
	 *            Used as an identifier
	 * @return Can be ignored
	 */
	public int loadImageAsync(Context context, Uri uri, int width, int height,
			Object tag, OnFileLoadedListener l) {
		mOnFileLoadedListener = l;
		if (mLoadTask == null
				|| mLoadTask.getStatus() == AsyncTask.Status.FINISHED) {
			mLoadTask = new LoadTask();
		}
		if (mLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
			return STILL_RUNNING;
		}
		synchronized (sImageTaskQueue) {
			mThreadNote = new ThreadNote(mLoadTask, context, uri, width,
					height, tag);
			sImageTaskQueue.add(mThreadNote);
			if (sImageTaskQueue.size() <= MAX_CONCURRENT_THREAD_NUM) {
				((LoadTask) mThreadNote.task).execute(mThreadNote.params);
			}
		}
		return SUCCESSED;
	}

	/**
	 * Save a Bitmap into a file.
	 * 
	 * @param context
	 * @param bitmap
	 *            Bitmap to be saved.
	 * @param path
	 *            where to save, such as: /sdcard/pics/12345.jpg
	 * @param type
	 *            TYPE_JPG or TYPE_PNG
	 * @param l
	 *            Callback listener
	 * @return Can be ignored
	 */
	public int saveImageAsync(Context context, Bitmap bitmap, String path,
			int type, OnFileSavedListener l) {
		mOnFileSavedListener = l;
		if (mSaveTask == null
				|| mSaveTask.getStatus() == AsyncTask.Status.FINISHED) {
			mSaveTask = new SaveTask();
		}
		if (mSaveTask.getStatus() == AsyncTask.Status.RUNNING) {
			return STILL_RUNNING;
		}
		mSaveTask.execute(context, bitmap, path, type, JPEG_QUALITY);
		return SUCCESSED;
	}
	
	public int saveImageAsync(Context context, Bitmap bitmap, String path,
			int type, int quality, OnFileSavedListener l) {
		mOnFileSavedListener = l;
		if (mSaveTask == null
				|| mSaveTask.getStatus() == AsyncTask.Status.FINISHED) {
			mSaveTask = new SaveTask();
		}
		if (mSaveTask.getStatus() == AsyncTask.Status.RUNNING) {
			return STILL_RUNNING;
		}
		mSaveTask.execute(context, bitmap, path, type, quality);
		return SUCCESSED;
	}

	public int getSampleFileAsync(Context cx, Uri uri, int w, int h,
			String path, int type, OnSampleFileListener l) {
		mOnSampleFileListener = l;
		if (mSampleFileTask == null
				|| mSampleFileTask.getStatus() == AsyncTask.Status.FINISHED) {
			mSampleFileTask = new SampleFileTask();
		}
		if (mSampleFileTask.getStatus() == AsyncTask.Status.RUNNING) {
			return STILL_RUNNING;
		}
		mSampleFileTask.execute(cx, uri, w, h, path, type);
		return SUCCESSED;
	}

	/**
	 * Get a bitmap. It is a synchronized method which will block UI thread.
	 * 
	 * @param context
	 * @param uri
	 *            Uri of the image to be opened
	 * @param longEdge
	 *            The width of the result Bitmap will not exceed this width
	 * @param shortEdge
	 *            The height of the result Bitmap will not exceed this height
	 * @return Result Bitmap
	 * @throws FileNotFoundException
	 * @throws OutOfMemoryError
	 */
	public static Bitmap getBitmapSample(Context context, Uri uri,
			int longEdge, int shortEdge) throws FileNotFoundException,
			OutOfMemoryError {
		InputStream is;
		LogUtils.d(TAG, "getBitmapSample: uri: " + uri.toString());
		is = context.getContentResolver().openInputStream(uri);
		int sample = getBitmapSampleValue(is, longEdge, shortEdge);
		LogUtils.d(TAG, "sample: " + sample);
		is = context.getContentResolver().openInputStream(uri);
		return getBitmapSample(is, sample);
	}

	/**
	 * 
	 * @param data
	 * @param longEdge
	 * @param shortEdge
	 * @return
	 * @throws OutOfMemoryError
	 */
	public static Bitmap getBitmapSample(byte[] data, int longEdge,
			int shortEdge) throws OutOfMemoryError {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		int sample = getBitmapSampleValue(bais, longEdge, shortEdge);
		bais = new ByteArrayInputStream(data);
		return getBitmapSample(bais, sample);
	}

	private static Bitmap getBitmapSample(InputStream is, int sample)
			throws OutOfMemoryError {
		Options options;
		options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inJustDecodeBounds = false;
		options.inSampleSize = sample;
		options.inDither = true;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);

		try {
			is.close();
		} catch (java.io.IOException e) {
			LogUtils.w(TAG, "file close error");
		}
		return bitmap;
	}

	/**
	 * 
	 * @param is
	 * @param longEdge
	 *            the long edge of the decoded bitmap will not exceed this value
	 * @param shortEdge
	 *            the short edge of the decoded bitmap will not exceed this
	 *            value
	 * @return
	 * @throws OutOfMemoryError
	 */
	private static int getBitmapSampleValue(InputStream is, int longEdge,
			int shortEdge) throws OutOfMemoryError {
		Options options;
		options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);
		int nSample = 1;
		int nLong, nShort;
		if (options.outHeight > options.outWidth) {
			nLong = options.outHeight;
			nShort = options.outWidth;
		} else {
			nLong = options.outWidth;
			nShort = options.outHeight;
		}
		while (nShort > shortEdge * (nSample + 1)
				|| nLong > longEdge * (nSample + 1)) {
			nSample++;
		}
		try {
			is.close();
		} catch (java.io.IOException e) {
			LogUtils.w(TAG, "file close error");
		}
		return nSample;
	}

	public static String getFileNameFromPath(String path) {
		String file;
		if (path == null) {
			return null;
		}
		if (path.contains("/")) {
			int s, e;
			s = path.lastIndexOf("/") + 1;
			e = path.length();
			LogUtils.w(TAG, String.format("name: %s. s: %d. n:%d", path, s, e));
			file = path.substring(s, e);
		} else {
			file = path;
		}
		if (file.contains(".")) {
			return file.substring(0, file.indexOf("."));
		}
		return file;
	}

	@SuppressWarnings("deprecation")
	public static boolean isSdcardFull() {
		String sdcard = Environment.getExternalStorageDirectory().getPath();
		File file = new File(sdcard);
		StatFs statFs = new StatFs(file.getPath());
		long availableSpare = statFs.getBlockSize()
				* ((long) statFs.getAvailableBlocks() - 4);
		
		LogUtils.w("----------availableSpare-------------",
				String.valueOf(statFs.getBlockSize()));
		LogUtils.w("----------availableSpare-------------",
				String.valueOf(statFs.getAvailableBlocks()));
		LogUtils.w("----------availableSpare-------------",
				String.valueOf(availableSpare));
		return (availableSpare < MIN_SD_CARD_SPACE);
	}

	@SuppressWarnings("deprecation")
	public static String getRealPathFromUri(Context context, Uri contentUri) {
		// can post image
		LogUtils.w(TAG,
				String.format("getRealPathFromUri: %s", contentUri.toString()));
		if (contentUri.toString().startsWith("file://")) {
			return contentUri.getPath();
		}
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = null;
		try {
			cursor = ((Activity) context).managedQuery(contentUri, proj, // Which
					// columns
					// to
					// return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null); // Order-by clause (ascending by name)

			int column_index;
			if (cursor == null) {
				return null;
			}
			column_index = cursor.getColumnIndex(MediaColumns.DATA);
			cursor.moveToFirst();
			if (column_index != -1) {
				String path;
				try {
					path = cursor.getString(column_index);
				} catch (Exception e) {
					path = null;
				}
				return path;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] getImageByte(Bitmap bitmap, int type) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		if (type == TYPE_JPG) {
			bitmap.compress(CompressFormat.JPEG, JPEG_QUALITY, os);
		}
		byte[] b = os.toByteArray();
		return b;
	}

	/**
	 * @param context
	 * @param bitmap
	 * @param path 存储路径
	 * @param type JPEG或者PNG
	 * @param quality 压缩质量。如果传值为<=0,则使用默认值：100。
	 * @return
	 * @throws OtherException
	 * @throws SDCardFullException
	 */
	public static Uri saveImage(Context context, Bitmap bitmap, String path,
			int type, int quality) throws OtherException, SDCardFullException {
		return saveImage(context, bitmap, path, type, quality, null, true);
	}
	
	
	/**
	 * @param context
	 * @param bitmap
	 * @param path 存储路径
	 * @param type JPEG或者PNG
	 * @param quality 压缩质量。如果传值为<=0,则使用默认值：100。
	 * @param needScan 是否需要通知系统扫描新文件。
	 * @return
	 * @throws OtherException
	 * @throws SDCardFullException
	 */
	public static Uri saveImage(Context context, Bitmap bitmap, String path,
			int type, int quality, boolean needScan) throws OtherException,
			SDCardFullException {
		return saveImage(context, bitmap, path, type, quality, null, false);
	}
	
	/**
	 * @param context
	 * @param bitmap
	 * @param path 存储路径
	 * @param type JPEG或者PNG
	 * @param quality 压缩质量。如果传值为<=0,则使用默认值：100。
	 * @param exif exif信息
	 * @param needScan 是否需要通知系统扫描新文件。
	 * @return
	 * @throws OtherException
	 * @throws SDCardFullException
	 */
	public static Uri saveImage(Context context, Bitmap bitmap, String path,
			int type, int quality, ExifInfo exif, boolean needScan)
			throws OtherException, SDCardFullException {
		int pos = path.lastIndexOf('/');
		if (pos <= 0) {
			return null;
		}
		String dir = path.substring(0, pos + 1);
		String name = path.substring(pos + 1, path.length());
		if (type == TYPE_PNG) {
			return saveImage(context, bitmap, dir, name, ".png", TYPE_PNG, 100,
					exif, needScan);
		} else if (type == TYPE_JPG) {
			return saveImage(context, bitmap, dir, name, ".jpg", TYPE_JPG,
					quality, exif, needScan);
		}

		throw new OtherException("");
	}

	public static Bitmap loadImage(Context context, Uri uri, int width,
			int height) throws OtherException, OutOfMemoryError,
			FileNotFoundException {
		Bitmap bitmap = null;
		bitmap = getBitmapSample(context, uri, width, height);
		if (bitmap == null) {
			throw new OtherException();
		}
		if (bitmap.getWidth() > bitmap.getHeight()) {
			bitmap = resizeBitmap(bitmap, width, height);
		} else {
			bitmap = resizeBitmap(bitmap, height, width);
		}
		int orientation = getImageOrientation(context, uri);
		if (orientation != 0) {
			Matrix mtx = new Matrix();
			mtx.setRotate(orientation);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), mtx, true);
		}
		return bitmap;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int w1 = (int) (w / scale);
		int h1 = (int) (h / scale);
		
		Bitmap newBitmap = Bitmap.createBitmap(w1, h1, getConfig(bitmap));
		Canvas canvas = new Canvas(newBitmap);
		Paint paint = new Paint();
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawBitmap(bitmap, new Rect(0, 0, w, h), new Rect(0, 0, w1, h1),
				paint);

		bitmap.recycle();
		return newBitmap;
	}

	public static File getSampleFile(Context cx, Uri uri, int w, int h,
			String path, int type) throws OutOfMemoryError,
			FileNotFoundException, OtherException, SDCardFullException {
		Bitmap bm = loadImage(cx, uri, w, h);
		saveImage(cx, bm, path, TYPE_JPG, JPEG_QUALITY);
		return new File(path);
	}
	
	/**
	 * When load GIF file, the return of Bitmap.getConfig() will be null. 
	 * GIF will cause the Bitmap.copy(Bitmap.getConfig(), boolean) and other methods crash.
	 * 
	 * @param bm
	 * @return
	 */
	public static Config getConfig(Bitmap bm) {
		if (bm == null || bm.getConfig() == null) {
			return Config.ARGB_8888;
		}
		Config cfg = bm.getConfig();
		if(cfg == null){
			cfg = Config.ARGB_8888;
		}
		return cfg;
	}

	public interface OnFileLoadedListener {
		/**
		 * 
		 * @param rst
		 *            could be SUCCESSED, OUT_OF_MEMORY, OTHER_ERROR,
		 *            FILE_NOT_EXSIT
		 * @param bitmap
		 * @param tag
		 */
		public void onFileLoaded(int rst, Bitmap bitmap, Object tag);
	}

	public interface OnFileSavedListener {
		public void onFileSaved(int rst, Uri uri);
	}

	public interface OnSampleFileListener {
		public void onGetSampleFile(int rst, File file);
	}

	public static int getImageOrientation(Context cx, Uri imageUri) {
		int orientation = getOrientationFromMedia(cx, imageUri);
		if (orientation == 0) {
			orientation = getOrientationFromExif(imageUri);
		}
		return orientation;
	}

	private static int getOrientationFromMedia(Context context, Uri imageUri) {
		ContentResolver mContentResolver = context.getContentResolver();
		Cursor c = null;
		int orientation = 0;

		c = createCursor(mContentResolver, imageUri);
		if (null != c) {
			if (c.moveToFirst()) {
				orientation = c.getInt(INDEX_ORIENTATION);
			}
			c.close();
		}
		return orientation;
	}

	private static int getOrientationFromExif(Uri uri) {
		int orientation = 0;
		try {
			String targetScheme = uri.getScheme();
			if (targetScheme.equals("file")) {
				ExifInterface exif = new ExifInterface(uri.getPath());
				orientation = Shared.exifOrientationToDegrees(exif
						.getAttributeInt(ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_NORMAL));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return orientation;
	}

	private static String sortOrder() {
		String ascending = " DESC";

		// Use DATE_TAKEN if it's non-null, otherwise use DATE_MODIFIED.
		// DATE_TAKEN is in milliseconds, but DATE_MODIFIED is in seconds.
		String dateExpr = "case ifnull(datetaken,0)"
				+ " when 0 then date_modified*1000" + " else datetaken"
				+ " end";

		// Add id to the end so that we don't ever get random sorting
		// which could happen, I suppose, if the date values are the same.
		return dateExpr + ascending + ", _id" + ascending;
	}

	private static Cursor createCursor(ContentResolver mContentResolver,
			Uri mBaseUri) {
		Cursor c = null;
		try {
			if (mBaseUri.getScheme().startsWith("file")) {
				String[] args = { "" };
				args[0] = mBaseUri.getPath();
				c = Media.query(mContentResolver, Media.EXTERNAL_CONTENT_URI,
						IMAGE_PROJECTION, "(" + MediaColumns.DATA + "=?)",
						args, sortOrder());
			} else {
				c = Media.query(mContentResolver, mBaseUri, IMAGE_PROJECTION,
						WHERE_CLAUSE, ACCEPTABLE_IMAGE_TYPES, sortOrder());
			}
		} catch (Exception e) {
			return null;
		}
		return c;
	}

	// private static Uri saveImage(Context context, Bitmap bitmap, String dir,
	// String name, String suffix, String mimeType, CompressFormat format,
	// int quality) throws OtherException, SDCardFullException {
	// return saveImage(context, bitmap, dir, name, suffix, mimeType, format,
	// quality, null);
	// }

	/**
	 * @param context
	 * @param bitmap
	 * @param dir 存储路径
	 * @param name 存储图片文件名
	 * @param suffix 存储图片文件名后缀
	 * @param type JPEG或者PNG
	 * @param quality 压缩质量。如果传值为<=0,则使用默认值：100。
	 * @param exif exif信息
	 * @param needScan 是否需要通知系统扫描新文件。
	 * @return
	 * @throws OtherException
	 * @throws SDCardFullException
	 */
	private static Uri saveImage(Context context, Bitmap bitmap, String dir,
			String name, String suffix, int type, int quality, ExifInfo exif,
			boolean needScan) throws OtherException, SDCardFullException {

		if (isSdcardFull()) {
			throw new SDCardFullException();
		}
		
		if (quality <= 0) {
			quality = JPEG_QUALITY;
		}

		String[] nameArr = name.split("\\.");
		String newPath;
		if (nameArr.length >= 2
				&& (nameArr[nameArr.length - 1].equals("jpg")
						|| nameArr[nameArr.length - 1].equals("png") || nameArr[nameArr.length - 1]
						.equals("tmp"))) {
			newPath = dir + name;
			if (nameArr[nameArr.length - 1].equals("tmp")) {
				needScan = false;
			}
		} else {
			newPath = dir + name + suffix;
		}

		File f = new File(dir);
		f.mkdirs();
		f = new File(newPath);

		try {
			f.createNewFile();
			FileOutputStream fileOS = new FileOutputStream(f);
			bitmap.compress(type == TYPE_JPG ? CompressFormat.JPEG
					: CompressFormat.PNG, quality, fileOS);
			fileOS.close();
			if (needScan) {
				fileScan(context, newPath, type);
			}
			// low efficient broadcast
			// context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
			// .parse("file://"
			// + Environment.getExternalStorageDirectory())));
			LogUtils.d(TAG, "save ok at:" + newPath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OtherException("cannot save image");
		}

		// 存储exif信息
		try {
			if (exif != null) {
				ExifUtils.saveExifToFile(newPath, exif);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Uri.fromFile(new File(newPath));
	}

	private static Bitmap resizeBitmap(Bitmap bitmap, int newWidth,
			int newHeight) throws OtherException {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		float rw = (float) (w) / newWidth;
		float rh = (float) (h) / newHeight;
		float r = rw > rh ? rw : rh;

		if (r < 1) {
			Bitmap newBitmap = bitmap.copy(getConfig(bitmap), true);
			if(bitmap!=null){
				bitmap.recycle();
			}
			return newBitmap;
		} else {
			return scaleBitmap(bitmap, r);
		}
	}

	private class LoadTask extends AsyncTask<Object, Void, Integer> {

		private Bitmap miBitmap;

		private Object tag;

		@Override
		protected Integer doInBackground(Object... params) {
			try {
				Context context = (Context) params[0];
				Uri uri = (Uri) params[1];
				Integer width = (Integer) params[2];
				Integer height = (Integer) params[3];
				if (params.length == 5) {
					tag = params[4];
				}
				miBitmap = loadImage(context, uri, width, height);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return OUT_OF_MEMORY;
			} catch (OtherException e) {
				e.printStackTrace();
				return OTHER_ERROR;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return FILE_NOT_EXSIT;
			}
			return SUCCESSED;
		}

		@Override
		protected void onPostExecute(Integer rst) {
			synchronized (sImageTaskQueue) {
				sImageTaskQueue.remove(mThreadNote);
				if (sImageTaskQueue.size() > 0) {
					Iterator<ThreadNote> iter = sImageTaskQueue.iterator();
					while (iter.hasNext()) {
						ThreadNote note = iter.next();
						if (note.task.getStatus() == AsyncTask.Status.PENDING) {
							((LoadTask) note.task).execute(note.params);
							break;
						}
					}
				}
			}
			if (mOnFileLoadedListener != null) {
				mOnFileLoadedListener.onFileLoaded(rst, miBitmap, tag);
				mOnFileLoadedListener = null;
			}
		}

	}

	private class SaveTask extends AsyncTask<Object, Void, Integer> {

		private Uri miUri;

		@Override
		protected Integer doInBackground(Object... params) {
			Context context = (Context) params[0];
			Bitmap bitmap = (Bitmap) params[1];
			String path = (String) params[2];
			Integer type = (Integer) params[3];
			Integer quality = (Integer) params[4];

			try {
				miUri = saveImage(context, bitmap, path, type, quality);
			} catch (OtherException e) {
				e.printStackTrace();
				return OTHER_ERROR;
			} catch (SDCardFullException e) {
				e.printStackTrace();
				return SDCARD_FULL;
			}

			return SUCCESSED;
		}

		@Override
		protected void onPostExecute(Integer rst) {
			if (mOnFileSavedListener != null) {
				mOnFileSavedListener.onFileSaved(rst, miUri);
				mOnFileSavedListener = null;
			}
		}

	}

	private class SampleFileTask extends AsyncTask<Object, Void, Integer> {

		private File file;

		@Override
		protected Integer doInBackground(Object... params) {
			Context cx = (Context) params[0];
			Uri uri = (Uri) params[1];
			int w = (Integer) params[2];
			int h = (Integer) params[3];
			String path = (String) params[4];
			Integer type = (Integer) params[5];

			try {
				file = getSampleFile(cx, uri, w, h, path, type);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return OUT_OF_MEMORY;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return FILE_NOT_EXSIT;
			} catch (OtherException e) {
				e.printStackTrace();
				return OTHER_ERROR;
			} catch (SDCardFullException e) {
				e.printStackTrace();
				return SDCARD_FULL;
			}

			return SUCCESSED;
		}

		@Override
		protected void onPostExecute(Integer rst) {
			if (mOnSampleFileListener != null) {
				mOnSampleFileListener.onGetSampleFile(rst, file);
				mOnSampleFileListener = null;
			}
		}

	}

	private class ThreadNote {
		@SuppressWarnings("rawtypes")
		public AsyncTask task;
		public Object[] params;

		@SuppressWarnings("rawtypes")
		public ThreadNote(AsyncTask task, Object... params) {
			this.task = task;
			this.params = params;
		}
	}

	/**
	 * 
	 * @param context
	 * @param path
	 * @param type
	 *            TYPE_JPG or TYPE_PNG
	 * @param orientation
	 *            should be one of 0, 90, 180, 270
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static void fileScan(Context context, String path, int type,
			int orientation) {
		File file = new File(path);
		if (!file.exists() || file.length() == 0) {
			LogUtils.w(TAG, "File Scan failed: file doesn't exist or is an empty file.");
			return;
		}
		
		Uri uri = null;
		try {
			String filename = path.substring(path.lastIndexOf("/") + 1);
			String filetitle = filename.substring(0, filename.lastIndexOf("."));
			File parentFile = new File(path).getParentFile();
			if (parentFile == null) {
				parentFile = new File("/");
			}

			// Lowercase the path for hashing. This avoids duplicate buckets if
			// the
			// filepath case is changed externally.
			// Keep the original case for display.
			String parentPath = parentFile.toString().toLowerCase();
			String parentName = parentFile.getName();
			ContentValues values = new ContentValues(8);
			values.put(MediaColumns.TITLE, filetitle);
			values.put(MediaColumns.DISPLAY_NAME, filename);
			values.put(ImageColumns.DESCRIPTION,
					context.getString(context.getApplicationInfo().labelRes));
			values.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis());
			values.put(MediaColumns.DATE_ADDED, System.currentTimeMillis());
			values.put(MediaColumns.DATE_MODIFIED, System.currentTimeMillis());
			values.put(MediaColumns.MIME_TYPE, type == TYPE_JPG ? "image/jpeg"
					: "image/png");
			values.put(ImageColumns.ORIENTATION, orientation);
			values.put(ImageColumns.BUCKET_ID, parentPath.hashCode());
			values.put(ImageColumns.BUCKET_DISPLAY_NAME, parentName);
			values.put(MediaColumns.DATA, path);
			uri = context.getContentResolver().insert(
					Images.Media.EXTERNAL_CONTENT_URI, values);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (uri == null || uri.equals("")) {
			LogUtils.w(TAG, "Insertion into database failed! Now send the broadcast.");

			String fileUri = "file://" + path;
			fileUri = fileUri.replaceAll("%", "%25");
			fileUri = fileUri.replaceAll("#", "%23");
			fileUri = fileUri.replaceAll(" ", "%20");
			Uri data = Uri.parse(fileUri);
			context.sendBroadcast(new Intent(
					Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
		}
	}

	public static void fileScan(Context context, String path, int type) {
		fileScan(context, path, type, 0);
	}

	/**
	 * save orientation to both database and exif(jpg header)
	 */
	public static void setFileOrientation(Context cx, String path, int type,
			int orientation) {
		fileScan(cx, path, type, orientation);
		ExifUtils.setExifOrientation(path, orientation);
	}
	
	
	public static Bitmap rotateOrFlipImage(RotateOrFlipType rotateOrFlipType,Bitmap bitmap,boolean isNeedToRecycle)
	{		
		Matrix matrix = new Matrix();
		Bitmap newBitmap = null;
		
		if(rotateOrFlipType == RotateOrFlipType.CLOCKWISE)
		{
			matrix.setRotate(90.0f);
		}
		else if(rotateOrFlipType == RotateOrFlipType.ANTICLOCKWISE)
		{
			matrix.setRotate(-90.0f);
		}
		else if(rotateOrFlipType == RotateOrFlipType.LEFT_RIGHT)
		{
			matrix.setValues(new float[] { -1.0F, 0.0F, 0.0F, 0.0F, 1.0F,
					0.0F, 0.0F, 0.0F, 1.0F });
		}
		else if(rotateOrFlipType == RotateOrFlipType.UP_DOWN)
		{
			matrix.setValues(new float[] { 1.0F, 0.0F, 0.0F, 0.0F, -1.0F,
					0.0F, 0.0F, 0.0F, 1.0F });
		}
		else  if(rotateOrFlipType == RotateOrFlipType.UP_DOWN_AND_LEFT_RIGHT)
		{
			matrix.setScale(-1, -1);
		}
			
		
		try {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
					matrix, true);
			
			if(isNeedToRecycle)
			{
				bitmap.recycle();
				bitmap = null;
			}
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (NullPointerException e) {
		    e.printStackTrace();
        }
		
		return newBitmap;
	}
	
}
