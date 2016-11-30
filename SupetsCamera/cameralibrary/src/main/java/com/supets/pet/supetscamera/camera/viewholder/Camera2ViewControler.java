package com.supets.pet.supetscamera.camera.viewholder;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.supets.pet.supetscamera.R;
import com.supets.pet.supetscamera.camera.activity.CameraPictureActivity;
import com.supets.pet.supetscamera.camera.uiwidget.AutoFitTextureView;
import com.supets.pet.supetscamera.camera.utils.Exif;
import com.supets.pet.supetscamera.camera.utils.FileUtils;
import com.supets.pet.supetscamera.camera.utils.ImageUtils;
import com.supets.pet.supetscamera.camera.utils.MYCameraHelper;
import com.supets.pet.supetscamera.camera.utils.MYUtils;
import com.supets.pet.supetscamera.camera.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2ViewControler extends CameraDevice.StateCallback
        implements TextureView.SurfaceTextureListener,
        ImageReader.OnImageAvailableListener {

    private static final int DelayLensChangeTime = 1500;
    private static final int DelayTakeChangeTime = 1000;

    private ImageView mTakePictureImageView;
    private ImageView mCancelImageView, mCameraSatausTip;
    private View mCameraLensImageView;
    private boolean mFrontLens = false;


    private View mTopMaskView;
    private View mBottomMaskView;

    private View mCameraFlashImageViewLayout;

    private enum FlashMode {
        FlashAuto, FlashOn, FlashOFF
    }

    private FlashMode mFlashMode = FlashMode.FlashAuto;

    private boolean mCertificate = false; // 实名认证拍照  非实名认证
    private boolean frontSide = true;//身份证正面照片

    private CameraPictureActivity mContext;
    private View mWholeView;

    private MediaActionSound mMediaActionSound;

    public Camera2ViewControler(View view, CameraPictureActivity mContext) {
        this.mContext = mContext;
        this.mWholeView = view;
        initShutter();
        onCreateView();
    }

    public Camera2ViewControler(View view, CameraPictureActivity mContext
            , boolean mCertificate, boolean frontSide) {
        this.mContext = mContext;
        this.mWholeView = view;
        this.mCertificate = mCertificate; // 实名认证拍照  非实名认证
        this.frontSide = frontSide;//身份证正面照片
        initShutter();
        onCreateView();
    }

    public void onCreateView() {
        initView();
        delayOpenLens();

    }

    private void initShutter() {
        mMediaActionSound = new MediaActionSound();
        mMediaActionSound.load(MediaActionSound.SHUTTER_CLICK);
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

    private void initView() {
        mTextureView = (AutoFitTextureView) mWholeView.findViewById(R.id.camera_surfaceView2);
        mWholeView.findViewById(R.id.previewFrameLayout).setVisibility(View.VISIBLE);
        mTopMaskView = mWholeView.findViewById(R.id.top_mask);
        mBottomMaskView = mWholeView.findViewById(R.id.bottom_mask);
        mTakePictureImageView = (ImageView) mWholeView.findViewById(R.id.camera_button_imageview);
        mTakePictureImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mTakePictureImageView.setClickable(false);
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
        mCameraSatausTip = (ImageView) mWholeView.findViewById(R.id.camera_flash_imageview);
        mCameraFlashImageViewLayout = mWholeView.findViewById(R.id.camera_flash_imageview_relativeLayout);
        mCameraFlashImageViewLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                arg0.setEnabled(false);
                setCameraFlashOption();
                arg0.setEnabled(true);
            }
        });

        showCerTip();
        checkCameraId();

        if (!mIsExistFrontCamera) {
            mCameraLensImageView.setVisibility(View.GONE);
        }

        mOrientationEventListener = new OrientationEventListener(mContext,
                SensorManager.SENSOR_DELAY_NORMAL) {

            private CameraManager manager;

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return;
                }
                try {
                    if (mCameraDevice != null) {
                        if (manager == null) {
                            manager = (CameraManager) UIUtils.getContext()
                                    .getSystemService(Context.CAMERA_SERVICE);
                        }
                        orientation = (orientation + 45) / 90 * 90;
                        CameraCharacteristics characteristics = manager
                                .getCameraCharacteristics(mCameraId);
                        if (characteristics
                                .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                            int o = characteristics
                                    .get(CameraCharacteristics.SENSOR_ORIENTATION);
                            rotation = (o - orientation + 360) % 360;
                        } else {
                            int o = characteristics
                                    .get(CameraCharacteristics.SENSOR_ORIENTATION);
                            rotation = (o + orientation) % 360;
                        }
                        Log.e(TAG, "rotation:" + rotation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
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

    int rotation = 0;

    private int topMaskViewHeight = 0;

    private void changeCameraMaskView() {
        int mMaskViewHeight = (UIUtils.getScreenHeight() - UIUtils.dp2px(75) - UIUtils.getScreenWidth()) / 2;
        int minDp = UIUtils.dp2px(26f);
        topMaskViewHeight = Math.max(mMaskViewHeight, minDp);
        LayoutParams lParams = (LayoutParams) mTopMaskView.getLayoutParams();
        lParams.height = topMaskViewHeight;
        mTopMaskView.setLayoutParams(lParams);
        lParams = (LayoutParams) mBottomMaskView.getLayoutParams();
        lParams.height = UIUtils.getScreenHeight() - topMaskViewHeight - UIUtils.getScreenWidth();
        mBottomMaskView.setLayoutParams(lParams);
        View mTopView = mWholeView.findViewById(R.id.camera_option_relativeLayout);
        lParams = (LayoutParams) mTopView.getLayoutParams();
        lParams.height = topMaskViewHeight;
        mTopView.setLayoutParams(lParams);
    }

    private void setCameraLensOption() {
        try {
            if (mCameraDevice != null) {

                delayOpenLens();

                if (mFrontLens) {
                    mFrontLens = false;
                    mCameraId = mCameraEndId;
                    mCameraFlashImageViewLayout.setVisibility(View.VISIBLE);
                } else {
                    mCameraId = mCameraFrontId;
                    mFrontLens = true;
                    mCameraFlashImageViewLayout.setVisibility(View.INVISIBLE);
                }

                mState = STATE_PREVIEW;
                onPause();
                onResume();
            } else {
                MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
            }
        } catch (Exception e) {
            MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
        }
    }

    private void setCameraFlashOption() {

        try {
            if (mCameraDevice != null) {
                if (mFlashMode == FlashMode.FlashAuto) {
                    mFlashMode = FlashMode.FlashOFF;
                    mCameraSatausTip.setBackgroundResource(R.drawable.btn_camera_flash_off_selector);
                } else if (mFlashMode == FlashMode.FlashOFF) {
                    mFlashMode = FlashMode.FlashOn;
                    mCameraSatausTip.setBackgroundResource(R.drawable.btn_camera_flash_on_selector);
                } else if (mFlashMode == FlashMode.FlashOn) {
                    mFlashMode = FlashMode.FlashAuto;
                    mCameraSatausTip.setBackgroundResource(R.drawable.btn_camera_flash_auto_selector);
                }
            } else {
                MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
            }
        } catch (Exception e) {
            MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
        }
    }

    public void setCallBack(CameraViewControler.TakePictureCallBack callBack) {
        this.mCallBack = callBack;
    }

    //camera2配置

    private static final String TAG = "Camera2ViewControler";

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private ImageReader mImageReader;
    private String mCameraId;
    private AutoFitTextureView mTextureView;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private Size mPreviewSize;
    public CameraViewControler.TakePictureCallBack mCallBack;

    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;

    private int mState = STATE_PREVIEW;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    String mCameraFrontId;
    String mCameraEndId;
    boolean mIsExistFrontCamera = false;

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            // Log.v(TAG, "当前状态 :" + mState);
            switch (mState) {
                case STATE_PREVIEW: {
                    isOpenSuccess = true;
                    Log.v(TAG, "当前状态 :STATE_PREVIEW");
                    break;
                }
                case STATE_WAITING_LOCK: {

                    int afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    Log.v(TAG, "当前状态 :STATE_WAITING_LOCK:" + afState);

                    if (afState == 0 && mIsExistFrontCamera
                            && mCameraId.equals(mCameraFrontId)) {
                        mState = STATE_PICTURE_TAKEN;
                        Log.v(TAG, "当前状态前置变换 :STATE_PICTURE_TAKEN");
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState
                            || CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED == afState
                            || CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState
                            || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result
                                .get(CaptureResult.CONTROL_AE_STATE);
                        // 聚焦成功，同时闪光灯不可用就直接拍照
                        if (aeState == null
                                || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            Log.v(TAG, "当前状态变换 :STATE_PICTURE_TAKEN");
                            captureStillPicture();
                        } else {
                            // 散光灯没准备好
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    Log.v(TAG, "当前状态 :STATE_WAITING_PRECAPTURE");
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null
                            || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE
                            || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                        Log.v(TAG, "当前状态变换 :STATE_WAITING_NON_PRECAPTURE");
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    Log.v(TAG, "当前状态 :STATE_WAITING_NON_PRECAPTURE");
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null
                            || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        Log.v(TAG, "当前状态变换 :STATE_PICTURE_TAKEN");
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session,
                                        CaptureRequest request, CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session,
                                       CaptureRequest request, TotalCaptureResult result) {
            process(result);
        }


        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            Log.v(TAG, "onCaptureStarted");
        }
    };

    private void runPrecaptureSequence() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(),
                    mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public void onResume() {

        isOpenSuccess = false;
        isCropPicture = true;

        mTakePictureImageView.setClickable(true);
        try {
            if (mOrientationEventListener != null) {
                mOrientationEventListener.enable();
            }
            startBackgroundThread();
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else {
                mTextureView.setSurfaceTextureListener(this);
            }
        } catch (Exception e) {
            stopBackgroundThread();
        }
    }

    public void onPause() {
        try {
            if (mOrientationEventListener != null) {
                mOrientationEventListener.disable();
            }
            closeCamera();
            stopBackgroundThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OrientationEventListener mOrientationEventListener;

    private void setUpCameraOutputs(int width, int height) throws CameraAccessException {
        CameraManager manager = (CameraManager) UIUtils.getContext()
                .getSystemService(Context.CAMERA_SERVICE);
        for (String cameraId : manager.getCameraIdList()) {
            CameraCharacteristics characteristics = manager
                    .getCameraCharacteristics(cameraId);

            if (cameraId.equals(mCameraId)) {
                StreamConfigurationMap map = characteristics
                        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size mPreviewReSize = Collections
                        .max(Arrays.asList(map
                                        .getOutputSizes(ImageFormat.JPEG)),
                                new CompareSizesByArea());
                Log.e(getClass().getSimpleName(), "mPreviewOutSize:" + mPreviewReSize.getWidth() + "=" + mPreviewReSize.getHeight());
                mImageReader = ImageReader.newInstance(mPreviewReSize.getWidth(),
                        mPreviewReSize.getHeight(), ImageFormat.JPEG, 2);
                mImageReader.setOnImageAvailableListener(this,
                        mBackgroundHandler);
                mPreviewSize = chooseOptimalPreSize(map.getOutputSizes(SurfaceTexture.class)
                        , mPreviewReSize);

                Log.e(getClass().getSimpleName(), "mPreviewSize:" + mPreviewSize.getWidth() + "=" + mPreviewSize.getHeight());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int orientation = UIUtils.getContext().getResources().getConfiguration().orientation;
                            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                mTextureView.setAspectRatio(mPreviewSize.getWidth(),
                                        mPreviewSize.getHeight());
                            } else {
                                mTextureView.setAspectRatio(mPreviewSize.getHeight(),
                                        mPreviewSize.getWidth());
                            }
                        } catch (Exception e) {

                        }
                    }
                }, 400);
                return;
            }
        }

    }

    private Size chooseOptimalPreSize(Size[] choices, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<Size>();
        double ratio = (double) aspectRatio.getWidth() / aspectRatio.getHeight();
        Log.v(getClass().getSimpleName(), "ratio:" + ratio);
        for (Size option : choices) {
            double op = (double) option.getWidth() / option.getHeight();
            Log.v(getClass().getSimpleName(), "choice:" + op + "=" + option.getWidth() + "=" + option.getHeight());
            if (Math.abs(op - ratio) < 0.1) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.max(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    public void checkCameraId() {

        CameraManager manager = (CameraManager) UIUtils.getContext()
                .getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager
                        .getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    mIsExistFrontCamera = true;
                    mCameraFrontId = cameraId;
                    continue;
                }
                mCameraEndId = cameraId;
            }
        } catch (Exception e) {
            MYUtils.showToastMessage("查找相机失败");
        }

        mCameraId = mCameraEndId;
        mFrontLens = false;
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight()
                    - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    boolean isOpenSuccess = false;

    private void openCamera(int width, int height) {
        if (mCameraId == null) {
            return;
        }
        isOpenSuccess = false;
        try {
            setUpCameraOutputs(width, height);
            configureTransform(width, height);
            final CameraManager manager = (CameraManager) UIUtils.getContext()
                    .getSystemService(Context.CAMERA_SERVICE);
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext,
                    new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {

                        @Override
                        public void onGranted() {
                            try {
                                manager.openCamera(mCameraId, Camera2ViewControler.this, mBackgroundHandler);
                            } catch (CameraAccessException  e) {
                                e.printStackTrace();
                            }catch (SecurityException e){
                                e.printStackTrace();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onDenied(String permission) {
                            MYUtils.showToastMessage("Sorry, we need the Storage Permission to do that");
                        }
                    });
        } catch (Exception e) {
            MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
            stopBackgroundThread();
            mCameraOpenCloseLock.release();
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera2");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //拍照按钮
    private boolean isCropPicture = true;

    private void takePicture() {

        if (!isOpenSuccess) {
            return;
        }

        if (!isCropPicture) {
            return;
        }
        try {
            isCropPicture = false;

            mRoateOld = rotation;

            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = STATE_WAITING_LOCK;
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(),
                    mCaptureCallback, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
            isCropPicture = true;
        }
    }

    // 聚焦成功后，启动拍照
    private void captureStillPicture() {
        try {
            if (null == mContext || null == mCameraDevice) {
                return;
            }
            final CaptureRequest.Builder captureBuilder = mCameraDevice
                    .createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE,
                    CaptureRequest.CONTROL_MODE_AUTO);

            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    rotation);
            Log.e(TAG, "rotation" + rotation);
            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request, TotalCaptureResult result) {
                    unlockFocus();
                }
            };
            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void unlockFocus() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            mCaptureSession.capture(mPreviewRequestBuilder.build(),
                    mCaptureCallback, mBackgroundHandler);

            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest,
                    mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private int mSurfaceWidth = 0;
    private int mSurfaceHeight = 0;

    //surface创建成功后打开相机
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width,
                                          int height) {
        Log.v(TAG, "onSurfaceTextureAvailable: width" + width + " height:" + height);
        openCamera(width, height);
        changeCameraMaskView();
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width,
                                            int height) {
        Log.v(TAG, "onSurfaceTextureSizeChanged: width" + width + " height:" + height);
        configureTransform(width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
        Log.v(TAG, "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        Log.v(TAG, "onSurfaceTextureUpdated");
    }

    private void configureTransform(int viewWidth, int viewHeight) {

        if (null == mTextureView || null == mPreviewSize || null == mContext) {
            return;
        }
        int rotation = mContext.getWindowManager().getDefaultDisplay()
                .getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(),
                mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY
                    - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
        Log.v(TAG, "setTransform" + rotation);
    }

    //打开相机成功
    @Override
    public void onOpened(CameraDevice cameraDevice) {
        mCameraOpenCloseLock.release();
        mCameraDevice = cameraDevice;
        //创建预览请求
        createCameraPreviewSession();
    }

    @Override
    public void onDisconnected(CameraDevice cameraDevice) {
        mCameraOpenCloseLock.release();
        cameraDevice.close();
        mCameraDevice = null;
    }

    @Override
    public void onError(CameraDevice cameraDevice, int error) {
        mCameraOpenCloseLock.release();
        cameraDevice.close();
        mCameraDevice = null;
//        Looper.prepare();
//        MYUtils.showToastMessage("打开相机失败");
//        Looper.loop();
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(mPreviewSize.getWidth(),
                    mPreviewSize.getHeight());
            Surface surface = new Surface(texture);
            mPreviewRequestBuilder = mCameraDevice
                    .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(
                    Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(
                                CameraCaptureSession cameraCaptureSession) {
                            if (null == mCameraDevice) {
                                return;
                            }
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // 支持聚焦模式
                                mPreviewRequestBuilder
                                        .set(CaptureRequest.CONTROL_AF_MODE,
                                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                mPreviewRequestBuilder
                                        .set(CaptureRequest.CONTROL_AF_MODE,
                                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // 灯光自动模式
                                mPreviewRequestBuilder
                                        .set(CaptureRequest.CONTROL_AE_MODE,
                                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                // 设置自动白平衡
                                mPreviewRequestBuilder.set(
                                        CaptureRequest.CONTROL_AWB_MODE,
                                        CaptureRequest.CONTROL_AWB_MODE_AUTO);
                                // 开启预览
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                                        mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                CameraCaptureSession cameraCaptureSession) {
                            MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
                        }
                    }, null);
        } catch (CameraAccessException e) {
            MYUtils.showToastMessage(R.string.camera_quanxian_close_tip);
        }
    }


    //保存图片
    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireNextImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        new SavePictureTask().execute(bytes);
        mMediaActionSound.play(MediaActionSound.SHUTTER_CLICK);
    }

    int mRoateOld = 90;

    //拍照保存
    public class SavePictureTask extends AsyncTask<byte[], String, String> {

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
                Log.e(TAG, "originalfileWidth:" + originalfileWidth);
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
                //int maskHeight = (mPreviewSize.getWidth() - mPreviewSize.getHeight()) / 2;

                float top = topMaskViewHeight * 1f / mSurfaceHeight;
                float bottom = (mSurfaceHeight - mSurfaceWidth - topMaskViewHeight) * 1f / mSurfaceHeight;
                bottom = bottom > 0 ? bottom : 0;

                Log.v(TAG, "CropRate:" + top + "==" + bottom);
                if (originalFileHeight >= originalfileWidth) { // 高度裁剪
                    // 原始文件和预览大小比例--算出裁剪值
                    int cropY = (int) (originalFileHeight * top);
                    int cropY2 = (int) (originalFileHeight * bottom);
                    int cropHeight = originalFileHeight - cropY - cropY2;
                    if (bitmap != null) {
                        if (!mFrontLens) {
                            Log.e(TAG, "cropY:" + cropY2 + "=" + mRoateOld);
                            cropBitmap = Bitmap.createBitmap(bitmap, 0, mRoateOld == 90 ? cropY : cropY2,
                                    originalfileWidth, cropHeight, null, false);
                        } else {
                            Log.e(TAG, "cropY2:" + cropY2 + "=" + mRoateOld);
                            cropBitmap = Bitmap.createBitmap(bitmap, 0, mRoateOld == 90 ? cropY2 : cropY,
                                    originalfileWidth, cropHeight, null, false);
                        }
                        cropBitmap = Bitmap.createScaledBitmap(cropBitmap,
                                cropHeight, cropHeight, false);
                        if (cropBitmap != null) {
                            bitmap = cropBitmap;
                        }
                    }
                    Log.e(TAG, "cropY:" + cropY);
                    Log.e(TAG, "cropHeight:" + cropHeight);
                } else if (originalFileHeight < originalfileWidth) { // 宽度裁剪,跟方向有关
                    int cropX = (int) (originalfileWidth * top);
                    int cropX2 = (int) (originalfileWidth * bottom);
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
                    Log.e(TAG, "cropX:" + cropX);
                    Log.e(TAG, "cropWidth:" + cropWidth);

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
                isCropPicture = true;
                mTakePictureImageView.setClickable(true);// bug
                MYUtils.showToastMessage("裁剪失败");
            } else {
                if (mCallBack != null) {
                    mCallBack.onSuccess(result);
                }
            }
        }
    }
}
