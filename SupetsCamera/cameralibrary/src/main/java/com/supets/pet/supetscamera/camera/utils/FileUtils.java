package com.supets.pet.supetscamera.camera.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    public static File getAppFolder() {
        if (!isSDCardAvailable()) {
            return null;
        }
        String folderName = "supetscamera";
        File folder = new File(Environment.getExternalStorageDirectory(), folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static boolean isSDCardAvailable() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            MYUtils.showToastMessage("SD卡不可用");
            return false;
        }
        return true;
    }

    public static File createPhotoSavedFile() {
        File folder = getAppFolder();
        if (folder == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String fname = sdf.format(new Date()).toString() + ".jpg";
        return new File(folder, fname);
    }


}
