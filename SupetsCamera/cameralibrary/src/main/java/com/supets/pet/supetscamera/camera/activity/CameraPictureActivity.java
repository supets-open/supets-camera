package com.supets.pet.supetscamera.camera.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.supets.pet.supetscamera.R;
import com.supets.pet.supetscamera.camera.utils.CameraConfig;
import com.supets.pet.supetscamera.camera.utils.MYCameraHelper;
import com.supets.pet.supetscamera.camera.viewholder.Camera2ViewControler;
import com.supets.pet.supetscamera.camera.viewholder.CameraViewControler;

import java.io.File;

//参数来源2个:type(true实名认证，非实名认证)  frontSide(true正面 false 反面)

public class CameraPictureActivity extends BaseActivity implements CameraViewControler.TakePictureCallBack {

    private View mWholeView;
    private CameraViewControler mControler;

    private Camera2ViewControler mControler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        boolean isCer = false;
        boolean isfront = false;

        Uri uri = getIntent().getData();
        if (uri != null && uri.getScheme().equals("supets")) {
            isCer = "true".equals(uri.getQueryParameter("certificate"));
            isfront = "true".equals(uri.getQueryParameter("frontSide"));
        }


        mWholeView = findViewById(R.id.cameraView);
        if (Build.VERSION.SDK_INT >= 21) {
            mControler2 = new Camera2ViewControler(mWholeView, this, isCer, isfront);
            mControler2.setCallBack(this);
        } else {
            mControler = new CameraViewControler(mWholeView, this, isCer, isfront);
            mControler.setCallBack(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 21) {
            mControler2.onResume();
        } else {
            mControler.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 21) {
            mControler2.onPause();
        } else {
            mControler.onPause();
        }
    }

    @Override
    public void onSuccess(String mPicturePath) {
        if (mPicturePath == null) {
            return;
        }

        MYCameraHelper.scanPicture(new File(mPicturePath), this);
        Intent intent = getIntent();
        intent.putExtra(CameraConfig.CAMERA_OUT_PATH_KEY, mPicturePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
