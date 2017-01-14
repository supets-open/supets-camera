//package com.supets.petdemo.activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//
//import com.supets.pet.multiimageselector.MultiViewCallBack;
//import com.supets.pet.multiimageselector.holder.MultiImageSelectorView;
//import com.supets.pet.multiimageselector.model.Image;
//import com.supets.pet.multiimageselector.ImageConfig;
//import com.supets.pet.supetscamera.camera.utils.CameraConfig;
//import com.supets.pet.supetscamera.camera.utils.MYUtils;
//
//import java.util.ArrayList;
//
//
///**
// * 头像裁剪：PicturePath
// */
//public class MultiPictureActivity extends Activity implements MultiViewCallBack {
//
//    private ArrayList<Image> resultList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        MultiImageSelectorView view=new MultiImageSelectorView(this);
//        setContentView(view);
//        view.setCallback(this);
//        view.setData(true, 0, ImageConfig.MODE_MULTI,resultList,5);
//    }
//
//    //拍照
//    @Override
//    public void onClickCamera() {
//        Intent intent = new Intent(CameraConfig.CAMERA_ACTION);
//        intent.setData(Uri.parse(String.format(CameraConfig.CAMERA_URI,false,false)));
//        startActivityForResult(intent, 8888);
//    }
//
//   //自定义数据加载
//    @Override
//    public void dismissProgressLoading() {
//
//    }
//
//    //自定义数据加载
//    @Override
//    public void showProgressLoading() {
//
//    }
//
//    public void onSelectFinished() {
//       Intent data = getIntent();
//       data.putExtra("picresult", resultList);
//       setResult(RESULT_OK, data);
//       finish();
//    }
//
//    @Override
//    public void onBack() {
//        finish();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 8888 && resultCode == RESULT_OK) {
//            MYUtils.showToastMessage(data.getStringExtra(CameraConfig.CAMERA_OUT_PATH_KEY));
//        }
//    }
//}
