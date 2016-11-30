package com.supets.pet.supetscamera.camera.utils;

import android.widget.Toast;

public class MYUtils {
    public static void showToastMessage(int res) {
        Toast.makeText(UIUtils.getContext(),
                UIUtils.getResources().getString(res), Toast.LENGTH_SHORT).show();
    }

    public static void showToastMessage(String msg) {
        Toast.makeText(UIUtils.getContext(),msg, Toast.LENGTH_SHORT).show();
    }
}
