# supets-camera

疯狂桔子相机库，实现Camera和Camera2预览拍照，自动裁剪为正方形

## 1 演示

![pic1](https://github.com/supets-open/supets-camera/blob/master/SupetsCamera/doc/Screenshot_2016-12-05-18-22-07.jpeg)
![pic1](https://github.com/supets-open/supets-camera/blob/master/SupetsCamera/doc/Screenshot_2016-12-05-18-22-19.jpeg)
![pic1](https://github.com/supets-open/supets-camera/blob/master/SupetsCamera/doc/Screenshot_2016-12-05-18-24-09.jpeg)

## 2 使用说明

 普通拍照  
       Intent intent = new Intent(CameraConfig.CAMERA_ACTION);
       intent.setData(Uri.parse(String.format(CameraConfig.CAMERA_URI,false,false)));
       startActivityForResult(intent, 8888);
       
 身份拍照
       Intent intent = new Intent(CameraConfig.CAMERA_ACTION);
       intent.setData(Uri.parse(String.format(CameraConfig.CAMERA_URI,true,false)));
       startActivityForResult(intent, 8888);
       
 结果返回
      if (requestCode == 8888 && resultCode == RESULT_OK) {
          MYUtils.showToastMessage(data.getStringExtra(CameraConfig.CAMERA_OUT_PATH_KEY));
          MYUtils.showToastMessage(data.getData().getQueryParameter(CameraConfig.CAMERA_KEY_CER));
          MYUtils.showToastMessage(data.getData().getQueryParameter(CameraConfig.CAMERA_KEY_FRONT_SIDE));
       } 
 
