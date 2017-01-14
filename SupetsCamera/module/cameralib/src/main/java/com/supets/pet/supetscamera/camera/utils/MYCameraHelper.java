package com.supets.pet.supetscamera.camera.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

import java.io.File;
import java.io.FileNotFoundException;

public class MYCameraHelper {

    public static void scanPicture(File path, Context ct) {
        ct.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                .fromFile(path)));
    }

    public static void saveToGallery(File path, Context ct) {
        try {
            Media.insertImage(ct.getContentResolver(), path.getAbsolutePath(),
                    path.getName(), null);
        } catch (FileNotFoundException e) {
        }
    }

    //水平翻转
    public static Bitmap convertBmp(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
        return convertBmp;
    }
}
