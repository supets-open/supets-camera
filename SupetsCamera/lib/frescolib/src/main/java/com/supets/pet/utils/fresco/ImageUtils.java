package com.supets.pet.utils.fresco;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.supets.lib.supetscontext.App;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by b.s.lee on 2016/1/27.
 * <p/>
 * module   name:
 * module action:
 */
public class ImageUtils {

    public static final int Max_Width = 1080;
    public static final int Max_Height = 1080;
    public static final int Bitmap_Quality = 100;
    public static final int MINBitmap_Quality = 40;//不能低于30
    public static final int PhotoWidthMixLimit = 320;
    public static final int PhotoHeightMixLimit = 320;
    public static final int Photo_Max_Size = 300 * 1024; // 100-300k
    //固定所有图片显示最大范围
    public static final int Max_Scale_Width = 1080;
    public static final int Max_Scale_Height = 1080;

    public static String insertImageToGallery(ContentResolver cr, Bitmap source, String title,
                                              String description, String mimetype) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        if (mimetype == null) {
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        } else {
            values.put(MediaStore.Images.Media.MIME_TYPE, mimetype);
        }
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                    if ("image/png".equalsIgnoreCase(mimetype)) {
                        format = Bitmap.CompressFormat.PNG;
                    }
                    source.compress(format, 100, imageOut);
                } finally {
                    imageOut.close();
                }

                /*long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = Images.Thumbnails.getThumbnail(cr, id, Images.Thumbnails
                .MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F,Images.Thumbnails.MICRO_KIND);*/
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    public static Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    public static Bitmap convertViewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config
                .ARGB_8888);
        // 利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        // 把view中的内容绘制在画布上
        view.draw(canvas);
        return bitmap;
    }

    public static boolean isValidateSize(Uri uri, int width, int height) {
        return isValidateSize(getImagePathFromUri(uri), width, height);
    }

    public static boolean isValidateSize(String imagePath, int width, int height) {
        int[] size = new int[2];
        getImageSize(imagePath, size);

        if (size[0] < width) {
            return false;
        }
        if (size[1] < height) {
            return false;
        }

        return true;
    }

    public static String getImagePathFromUri(Uri uri) {
        if ("file".equals(uri.getScheme())) {
            return uri.getPath();
        }
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = App.INSTANCE.getContentResolver().query(uri,
                proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images
                .Media.DATA);
        actualimagecursor.moveToFirst();

        String imagePath = actualimagecursor.getString(actual_image_column_index);
        actualimagecursor.close();
        return imagePath;
    }

    public static void getImageSize(String imagePath, int[] size) {
        if (size == null || size.length < 2) {
            throw new IllegalArgumentException("size must be an array of two integers");
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;
    }

    public static void getImageSize(Uri uri, int[] size) {
        getImageSize(getImagePathFromUri(uri), size);
    }

    public static byte[] readPhotobyte(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        int fileWidth = options.outWidth;
        int fileHeight = options.outHeight;

        options.inJustDecodeBounds = false;
        int inSampleSize = 1;
        if ((fileWidth > Max_Width) || fileHeight > Max_Height) {
            if (fileWidth > fileHeight) {
                inSampleSize = Math.round((float) fileHeight / (float) Max_Height);
            } else {
                inSampleSize = Math.round((float) fileWidth / (float) Max_Width);
            }
        }
        options.inSampleSize = inSampleSize;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap != null) {
            return Bitmap2Bytes(bitmap);
        }
        return null;
    }

    public static Bitmap readPhotoBitmap(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int fileWidth = options.outWidth;
        int fileHeight = options.outHeight;

        options.inJustDecodeBounds = false;
        int inSampleSize = 1;
        if ((fileWidth > Max_Width) || fileHeight > Max_Height) {
            if (fileWidth > fileHeight) {
                inSampleSize = Math.round((float) fileHeight / (float) Max_Height);
            } else {
                inSampleSize = Math.round((float) fileWidth / (float) Max_Width);
            }
        }
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap readPhotoBitmap(String filePath, int width, int height) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int originalFileHeight = options.outHeight;
        int originalfileWidth = options.outWidth;
        Log.v("FrescoImageHelper", originalfileWidth + "=before=" + originalFileHeight);

        options.inJustDecodeBounds = false;
        int inSampleSize = 1;
        if ((originalfileWidth > width)
                || originalFileHeight > height) {
            if (originalfileWidth > originalFileHeight) {
                inSampleSize = Math.round((float) originalFileHeight
                        / height);
            } else {
                inSampleSize = Math.round((float) originalfileWidth
                        / width);
            }
        }

        if (inSampleSize < 0) {
            inSampleSize = 1;
        }

        Log.v("FrescoImageHelper", inSampleSize + "=scale=");

        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        Log.v("FrescoImageHelper", bitmap.getWidth() + "=after=" + bitmap.getHeight());
        return bitmap;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, Bitmap_Quality, output);

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm, int MaxSize) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] result = null;

        int compressQuality = Bitmap_Quality;
        while (true) {
            if (compressQuality <= 0) {
                break;
            }

            bm.compress(Bitmap.CompressFormat.JPEG, compressQuality, output);

            result = output.toByteArray();

            if (compressQuality < MINBitmap_Quality) {//30
                Log.v("compressQuality", compressQuality + "==" + result.length / 1024);
                break;
            }

            if (result.length > MaxSize) {
                compressQuality = compressQuality - 10;
                Log.v("compressQuality2", compressQuality + "==" + result.length / 1024);
                output.reset();
                continue;
            } else {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.v("compressQuality3", compressQuality + "==" + result.length / 1024);
                break;
            }
        }

        return result;
    }

    public static int getImageWidth(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        return options.outWidth;
    }

    public static int getImageHeight(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        return options.outHeight;
    }
}
