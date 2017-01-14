package com.supets.pet.supetscamera.camera.utils;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public static final int Photo_Max_Size = 300 * 1024; // 100-300k

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
}
