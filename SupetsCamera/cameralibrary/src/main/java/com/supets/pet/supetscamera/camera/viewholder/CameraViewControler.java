package com.supets.pet.supetscamera.camera.viewholder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.supets.pet.supetscamera.R;
import com.supets.pet.supetscamera.camera.activity.CameraPictureActivity;
import com.supets.pet.supetscamera.camera.utils.Exif;
import com.supets.pet.supetscamera.camera.utils.FileUtils;
import com.supets.pet.supetscamera.camera.utils.ImageUtils;
import com.supets.pet.supetscamera.camera.utils.MYCameraHelper;
import com.supets.pet.supetscamera.camera.utils.MYUtils;
import com.supets.pet.supetscamera.camera.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class CameraViewControler implements
        SurfaceHolder.Callback, Camera.PictureCallback {

    private static final int DelayLensChangeTime = 1500;
    private static final int DelayTakeChangeTime = 1000;

    private final String TAG = "CameraActivity";

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    private ImageView mTakePictureImageView;
    private ImageView mCancelImageView;
    private View mCameraLensImageView;


    private int mCameraFrontIndex;
    private int mCameraBackIndex;
    private int mCameraId;
    private boolean mFrontLens = false;


    private View mTopMaskView;
    private View mBottomMaskView;

    private OrientationEventListener mOrientationEventListener;

    private Size mPreviewSize;
    private int mScreenWidth;


    private ImageView mCameraSatausTip;
    private View mCameraFlashImageViewLayout;

    private enum FlashMode {
        FlashAuto, FlashOn, FlashOFF
    }

    private FlashMode mFlashMode = FlashMode.FlashAuto;

    //相机打开控制
    private boolean isCamercaReady = false;
    private boolean mCameraOpen = false;

    private boolean mCertificate = false; // 实名认证拍照  非实名认证
    private boolean frontSide = true;//身份证正面照片

    private CameraPictureActivity mContext;
    private View mWholeView;

    private int dy = 0;//底部Bar高度

    public CameraViewControler(View view, CameraPictureActivity mContext) {
        this.mContext = mContext;
        this.mWholeView = view;
        onCreateView();
    }

    public CameraViewControler(View view, CameraPictureActivity mContext
            , boolean mCertificate, boolean frontSide) {
        this.mContext = mContext;
        this.mWholeView = view;
        this.mCertificate = mCertificate; // 实名认证拍照  非实名认证
        this.frontSide = frontSide;//身份证正面照片
        onCreateView();
    }

    public void onCreateView() {
        mScreenWidth = UIUtils.getScreenWidth();
        dy = UIUtils.dp2px(75f) / 2;

        initView();
        checkCameraFrontLens();
        setupSurfaceView();
        addOrientationListener();
    }

    private void delayOpenLens() {
        mCameraLensImageView.setEnabled(false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mCameraLensImageView.setEnabled(true);
            }
        }, DelayLensChangeTime);

        mTakePictureImageView.setEnabled(false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mTakePictureImageView.setEnabled(true);
            }
        }, DelayTakeChangeTime);
    }


    private void addOrientationListener() {
        mOrientationEventListener = new OrientationEventListener(mContext,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return;
                }
                try {
                    if (mCamera != null) {
                        CameraInfo info = new CameraInfo();
                        Camera.getCameraInfo(mCameraId, info);
                        orientation = (orientation + 45) / 90 * 90;
                        //int rotation = 0;
                        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                            rotation = (info.orientation - orientation + 360) % 360;
                        } else { // back-facing camera
                            rotation = (info.orientation + orientation) % 360;
                        }
                        Log.e(TAG, "rotation:" + rotation);
                        Parameters parameters = mCamera.getParameters();
                        parameters.setRotation(rotation);
                        mCamera.setParameters(parameters);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void checkCameraFrontLens() {

        boolean mHasFrontLens = false;

        int cameraNum = Camera.getNumberOfCameras();
        if (cameraNum > 1) {
            mHasFrontLens = true;
        } else {
            mHasFrontLens = false;
        }

        if (!mHasFrontLens) {
            mCameraLensImageView.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < cameraNum; i++) {
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraFrontIndex = i;
                } else if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                    mCameraBackIndex = i;
                }
            }
        }

        mCameraId = mCameraBackIndex;
    }

    @SuppressWarnings("deprecation")
    private void setupSurfaceView() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initView() {

        mSurfaceView = (SurfaceView) mWholeView.findViewById(R.id.camera_surfaceView);
        mSurfaceView.setVisibility(View.VISIBLE);
        mTopMaskView = mWholeView.findViewById(R.id.top_mask);
        mBottomMaskView = mWholeView.findViewById(R.id.bottom_mask);
        mTakePictureImageView = (ImageView) mWholeView.findViewById(R.id.camera_button_imageview);
        mTakePictureImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mTakePictureImageView.setClickable(false);
                Log.v("autoFocus", "false");
                takePicture();
            }
        });

        mCancelImageView = (ImageView) mWholeView.findViewById(R.id.cancel_imageview);
        mCancelImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                mContext.finish();
            }
        });
        mCameraLensImageView = mWholeView.findViewById(R.id.camera_lens_imageview_layout);
        mCameraLensImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setCameraLensOption();
            }
        });


        //
        mCameraSatausTip = (ImageView) mWholeView.findViewById(R.id.camera_flash_imageview);
        mCameraFlashImageViewLayout = mWholeView.findViewById(R.id.camera_flash_imageview_relativeLayout);
        mCameraFlashImageViewLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                arg0.setEnabled(false);
                setCameraFlashOption(true);
                arg0.setEnabled(true);
            }
        });

        showCerTip();
    }


    private void showCerTip() {
        if (mCertificate) {
            TextView certifyPhotoTips1TextView = (TextView) mWholeView.findViewById(R.id.truename_certify_tips1);
            if (frontSide) {
                certifyPhotoTips1TextView.setText(mContext.getString(R.string.certify_photo_frontside_tips));
            } else {
                certifyPhotoTips1TextView.setText(mContext.getString(R.string.certify_photo_backside_tips));
            }
            TextView certifyPhotoTips2TextView = (TextView) mWholeView.findViewById(R.id.truename_certify_tips2);
            certifyPhotoTips2TextView.setText(mContext.getString(R.string.take_certify_photo_tips));
        }
    }

    private void changeCameraMaskView() {

        int mMaskViewHeight;
        int surfaceHeight = mSurfaceView.getHeight();
        if (surfaceHeight > mScreenWidth) {
            mMaskViewHeight = (surfaceHeight - mScreenWidth) / 2;
        } else {
            mMaskViewHeight = 0;
        }

        LayoutParams lParams = (LayoutParams) mTopMaskView.getLayoutParams();
        lParams.height = mMaskViewHeight - dy;
        mTopMaskView.setLayoutParams(lParams);
        lParams = (LayoutParams) mBottomMaskView.getLayoutParams();
        lParams.height = mMaskViewHeight + dy;
        mBottomMaskView.setLayoutParams(lParams);

        View mTopView = mWholeView.findViewById(R.id.camera_option_relativeLayout);
        lParams = (LayoutParams) mTopView.getLayoutParams();
        lParams.height = mMaskViewHeight - dy;
        mTopView.setLayoutParams(lParams);
    }

    private void setCameraLensOption() {
        if (mCamera != null) {

            delayOpenLens();

            if (mFrontLens) {
                mFrontLens = false;
                mCameraId = mCameraBackIndex;
                mCameraFlashImageViewLayout.setVisibility(View.VISIBLE);
            } else {
                mFrontLens = true;
                mCameraId = mCameraFrontIndex;
                mCameraFlashImageViewLayout.setVisibility(View.INVISIBLE);
            }

            openCamera();
        } else {
            if (!isCamercaReady) {
                MYUtils.showToastMessage(R.string.camerca_preview_no_ready_ok);
            } else {
                MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
            }
        }
    }

    private void setCameraFlashOption(boolean isCycle) {
        try {
            if (mCamera != null) {

                Parameters parameters = mCamera.getParameters(); // Camera
                // to obtain
                if (mFlashMode == FlashMode.FlashAuto) {
                    mFlashMode = FlashMode.FlashOFF;
                    parameters
                            .setFlashMode(Parameters.FLASH_MODE_OFF);
                    mCameraSatausTip.setBackgroundResource(R.drawable.btn_camera_flash_off_selector);

                } else if (mFlashMode == FlashMode.FlashOFF) {
                    mFlashMode = FlashMode.FlashOn;
                    parameters
                            .setFlashMode(Parameters.FLASH_MODE_ON);
                    mCameraSatausTip.setBackgroundResource(R.drawable.btn_camera_flash_on_selector);


                } else if (mFlashMode == FlashMode.FlashOn) {
                    mFlashMode = FlashMode.FlashAuto;
                    parameters
                            .setFlashMode(Parameters.FLASH_MODE_AUTO);
                    mCameraSatausTip.setBackgroundResource(R.drawable.btn_camera_flash_auto_selector);
                }
                mCamera.setParameters(parameters);
            } else {
                if (!isCamercaReady) {
                    MYUtils.showToastMessage(R.string.camerca_preview_no_ready_ok);
                } else {
                    MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
                }
            }
        } catch (Exception e) {
            MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
        }
    }


    private int mRoateOld = 0;
    private int rotation = 90;

    private void takePicture() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {
                        mRoateOld = rotation;
                        if (!isCamercaReady) {
                            MYUtils.showToastMessage(R.string.camerca_preview_no_ready_ok);
                            mTakePictureImageView.setClickable(true);
                            return;
                        }

                        try {

                            if (mCamera != null) {
                                ShutterCallback shutterCallback = new ShutterCallback() {
                                    public void onShutter() {
                                        AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                                        mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
                                    }
                                };
                                mCamera.takePicture(shutterCallback, null, CameraViewControler.this); // picture
                                if (mOrientationEventListener != null) {
                                    mOrientationEventListener.disable();
                                }
                            } else {
                                MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
                                mTakePictureImageView.setClickable(true);
                                return;
                            }
                        } catch (Exception e) {
                            Log.v("CameraActivity", "takePicture failed");
                            mTakePictureImageView.setClickable(true);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onDenied(String permission) {
                        MYUtils.showToastMessage("Sorry, we need the Storage Permission to do that");
                    }
                });
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (mCamera != null) {
            try {
                Parameters parameters = mCamera.getParameters();
                if (parameters.getFocusMode() != null) {
                    mCamera.autoFocus(new AutoFocusCallback() {

                        @Override
                        public void onAutoFocus(boolean success, Camera arg1) {
                            if (success) {
                                mCamera.cancelAutoFocus();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
        changeCameraMaskView();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void openCamera() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext,
                new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {
                        openCamera2();
                    }

                    @Override
                    public void onDenied(String permission) {
                        MYUtils.showToastMessage("Sorry, we need the camera Permission to do that");
                    }
                });
    }


    private void openCamera2(){
        isCamercaReady = false;

        Log.e(TAG, "openCamera start");
        mCameraOpen = true;
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;// bug---引起方向变化判断mCamera不为空
        }
        Log.i(TAG, "camera id:" + mCameraId);
        try {
            mCamera = Camera.open(mCameraId);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            setCameraDisplayOrientation();

            new Handler().postDelayed(new Runnable() {
                @SuppressLint("InlinedApi")
                @Override
                public void run() {
                    if (mCamera == null) {
                        return;
                    }
                    try {
                        mCamera.startPreview();

                        final Parameters parameters = mCamera.getParameters();
                        if (Build.VERSION.SDK_INT >= 14) {
                            parameters
                                    .setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        } else {
                            if (parameters.getFocusMode() != null) {
                                mCamera.autoFocus(new AutoFocusCallback() {

                                    @Override
                                    public void onAutoFocus(boolean success,
                                                            Camera arg1) {
                                        if (success) {
                                            mCamera.cancelAutoFocus();
                                        }
                                    }
                                });
                            }
                        }
                        parameters
                                .setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);

                        List<Size> previewSizes = parameters
                                .getSupportedPreviewSizes();
                        int surfaceHeight = mSurfaceView.getHeight();

                        mPreviewSize = getOptimalPreviewSize(previewSizes,
                                surfaceHeight, mScreenWidth);
                        if (mPreviewSize != null) {
                            Log.i(TAG, "preview width:" + mPreviewSize.width
                                    + " height:" + mPreviewSize.height);
                            parameters.setPreviewSize(mPreviewSize.width,
                                    mPreviewSize.height);
                        }
                        List<Size> sizes = parameters
                                .getSupportedPictureSizes();
                        int sizess = sizes.size();

                        for (int i = 0; i < sizess; i++) {
                            Size size = sizes.get(i);
                            if (mPreviewSize != null) {
                                if (mPreviewSize.height == size.height
                                        && mPreviewSize.width == size.width) {
                                    Log.i(TAG, "optimal picture width:"
                                            + size.width + " height:"
                                            + size.height);
                                    parameters.setPictureSize(size.width,
                                            size.height);
                                    break;
                                }
                            } else if (size.width >= 1080 && size.width <= 2000
                                    && size.height >= 1080
                                    && size.height <= 2000) {
                                Log.i(TAG, "picture width:" + size.width
                                        + " height:" + size.height);
                                parameters.setPictureSize(size.width,
                                        size.height);
                                break;
                            }
                        }

                        mCamera.setParameters(parameters);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isCamercaReady = true;
                }
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            if (message != null && message.contains("permission")) {
                MYUtils.showToastMessage(R.string.camera_permission_for_supets);
            } else {
                MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
            }

            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }

            isCamercaReady = true;
        }
        Log.e(TAG, "openCamera end");
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (data != null) {
            mCamera.stopPreview();
            new SavePictureTask().execute(data);
        } else {
            mTakePictureImageView.setClickable(true);
            mCamera.startPreview();
            if (mOrientationEventListener != null) {
                mOrientationEventListener.enable();
            }
        }
    }

    class SavePictureTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... params) {
            Log.i(TAG, "start process picture data");
            File picture = FileUtils.createPhotoSavedFile();
            if (picture == null) {
                return null;
            }

            try {
                byte[] data = params[0];
                int rotate = Exif.getOrientation(data);
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = null;
                options.inJustDecodeBounds = true;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                        options);
                int originalFileHeight = options.outHeight;
                int originalfileWidth = options.outWidth;

                options.inJustDecodeBounds = false;
                int inSampleSize = 1;
                if ((originalfileWidth > ImageUtils.Max_Width)
                        || originalFileHeight > ImageUtils.Max_Height) {
                    if (originalfileWidth > originalFileHeight) {
                        inSampleSize = Math.round((float) originalFileHeight
                                / (float) ImageUtils.Max_Height);
                    } else {
                        inSampleSize = Math.round((float) originalfileWidth
                                / (float) ImageUtils.Max_Width);
                    }
                }
                Log.v(TAG, "originalfileWidth:" + originalfileWidth);
                Log.v(TAG, "originalFileHeight:" + originalFileHeight);
                Log.v(TAG, "inSampleSize:" + inSampleSize);

                options.inSampleSize = inSampleSize;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                        options);
                if (bitmap == null) {
                    return null;
                }

                // 旋转处理
                if (rotate > 0) {
                    Matrix matrix = new Matrix();
                    matrix.reset();
                    matrix.setRotate(rotate);
                    bitmap = Bitmap
                            .createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), matrix, true);
                }

                // 镜像处理
                if (mFrontLens) {
                    bitmap = MYCameraHelper.convertBmp(bitmap);
                }

                originalFileHeight = bitmap.getHeight();
                originalfileWidth = bitmap.getWidth();

                Log.v(TAG, "originalfileWidth-2:" + originalfileWidth);
                Log.v(TAG, "originalFileHeight-2:" + originalFileHeight);

                Bitmap cropBitmap = null;
                // int maskHeight = (mPreviewSize.width - mScreenWidth) / 2;
                // 裁剪部分按预览大小算比列---和在显示屏幕上的比列相符合
                int maskHeight = (mPreviewSize.width - mPreviewSize.height) / 2;
                Log.v(TAG, "maskHeight:" + maskHeight);

                if (originalFileHeight >= originalfileWidth) { // 高度裁剪
                    // 原始文件和预览大小比例--算出裁剪值
                    int cropY = (originalFileHeight * (maskHeight - dy))
                            / mPreviewSize.width;
                    int cropY2 = (originalFileHeight * (maskHeight + dy))
                            / mPreviewSize.width;
                    int cropHeight = originalFileHeight - cropY - cropY2;
                    if (bitmap != null) {
                        if (!mFrontLens) {
                            cropBitmap = Bitmap.createBitmap(bitmap, 0, mRoateOld == 90 ? cropY : cropY2,
                                    originalfileWidth, cropHeight, null, false);
                        } else {
                            cropBitmap = Bitmap.createBitmap(bitmap, 0, mRoateOld == 90 ? cropY2 : cropY,
                                    originalfileWidth, cropHeight, null, false);
                        }
                        cropBitmap = Bitmap.createScaledBitmap(cropBitmap,
                                cropHeight, cropHeight, false);
                        if (cropBitmap != null) {
                            bitmap = cropBitmap;
                        }
                    }
                    Log.v(TAG, "cropY:" + cropY);
                    Log.v(TAG, "cropHeight:" + cropHeight);

                } else if (originalFileHeight < originalfileWidth) { // 宽度裁剪,跟方向有关
                    int cropX = (originalfileWidth * (maskHeight - dy))
                            / mPreviewSize.width;
                    int cropX2 = (originalfileWidth * (maskHeight + dy))
                            / mPreviewSize.width;
                    int cropWidth = originalfileWidth - cropX - cropX2;
                    if (bitmap != null) {
                        //如果是方向右边，下边，应该是coopX2，但这里这个逻辑应该不会走了，统一文件纠正一个高大于宽的图了

                        if (!mFrontLens) {
                            cropBitmap = Bitmap.createBitmap(bitmap, mRoateOld == 0 ? cropX : cropX2, 0,
                                    cropWidth, originalFileHeight, null, false);
                        } else {
                            cropBitmap = Bitmap.createBitmap(bitmap, mRoateOld == 180 ? cropX2 : cropX, 0,
                                    cropWidth, originalFileHeight, null, false);
                        }

                        cropBitmap = Bitmap.createScaledBitmap(cropBitmap,
                                cropWidth, cropWidth, false);
                        if (cropBitmap != null) {
                            bitmap = cropBitmap;
                        }
                    }
                    Log.v(TAG, "cropX:" + cropX);
                    Log.v(TAG, "cropWidth:" + cropWidth);

                }

                data = ImageUtils.Bitmap2Bytes(bitmap,
                        ImageUtils.Photo_Max_Size);
                FileOutputStream fos = new FileOutputStream(picture.getPath());
                fos.write(data);
                fos.close();
                bitmap.recycle();
                bitmap = null;
                if (cropBitmap != null) {
                    cropBitmap.recycle();
                    cropBitmap = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            Log.i(TAG, "end process picture data");
            return picture.getAbsolutePath();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mContext.showProgressLoading();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mContext.dismissProgressLoading();
            if (result == null) {
                mTakePictureImageView.setClickable(true);// bug
                mCamera.startPreview();
            } else {
                if (callBack != null) {
                    callBack.onSuccess(result);
                }
            }
        }
    }

    private void setCameraDisplayOrientation() {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int rotation = mContext.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        //final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        Size optimalSize2 = null;
        int targetWidth = w;
        Log.e(TAG, "targetRatio:" + targetRatio + " w:" + w + " h:" + h);
        // Try to find an size match aspect ratio and size

        double minRatio = 0.1;
        double minDiff = Double.MAX_VALUE;

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            Log.e(TAG, "ratio:" + ratio + " width:" + size.width + " height:" + size.height);
            if (h == size.height
                    && w == size.width) {
                Log.e(TAG, "optimal picture width:"
                        + size.width + " height:"
                        + size.height);
                optimalSize = size;
                break;
            }

            if ((targetWidth - size.width) > 0 && Math.abs(targetWidth - size.width) < minDiff) {
                optimalSize2 = size;
                minDiff = Math.abs(size.width - targetWidth);
            }

            if (Math.abs(ratio - targetRatio) > minRatio)
                continue;
            if (Math.abs(ratio - targetRatio) < minRatio) {
                optimalSize = size;
                minRatio = Math.abs(ratio - targetRatio);
            }
        }

        if (optimalSize != null && optimalSize.height == h) {
            return optimalSize;
        }

        if (optimalSize2 != null && optimalSize2.height == h) {
            return optimalSize2;
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.width - targetWidth) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.width - targetWidth);
                }
            }
        }
        return optimalSize;
    }

    public void onResume() {

        if (mOrientationEventListener != null) {
            mOrientationEventListener.enable();
        }
        if (mCameraOpen) {
            Log.e(TAG, "came is open");
            openCamera();
        }

        mTakePictureImageView.setClickable(true);// bug

        delayOpenLens();

    }


    public void onPause() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private TakePictureCallBack callBack;

    public void setCallBack(TakePictureCallBack callBack) {
        this.callBack = callBack;
    }

    public interface TakePictureCallBack {
        void onSuccess(String PicturePath);
    }
}
