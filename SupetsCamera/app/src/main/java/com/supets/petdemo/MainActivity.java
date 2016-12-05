package com.supets.petdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.supets.pet.supetscamera.camera.utils.CameraConfig;
import com.supets.pet.supetscamera.camera.utils.MYUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.supets.pet.supetscamera.R.layout.activity_main);

        findViewById(com.supets.pet.supetscamera.R.id.take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraConfig.CAMERA_ACTION);
                intent.setData(Uri.parse(String.format(CameraConfig.CAMERA_URI,false,false)));
                startActivityForResult(intent, 8888);
            }
        });

        findViewById(com.supets.pet.supetscamera.R.id.take2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraConfig.CAMERA_ACTION);
                intent.setData(Uri.parse(String.format(CameraConfig.CAMERA_URI,true,false)));
                startActivityForResult(intent, 8888);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888 && resultCode == RESULT_OK) {
            MYUtils.showToastMessage(data.getStringExtra(CameraConfig.CAMERA_OUT_PATH_KEY));
            MYUtils.showToastMessage(data.getData().getQueryParameter(CameraConfig.CAMERA_KEY_CER));
            MYUtils.showToastMessage(data.getData().getQueryParameter(CameraConfig.CAMERA_KEY_FRONT_SIDE));
        }
    }

}
