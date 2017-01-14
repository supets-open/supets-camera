package com.supets.pet.supetscamera.camera.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;

import com.anthonycr.grant.PermissionsManager;

public class BaseActivity extends Activity {


    private ProgressDialog mProgressDialog;

    public void showProgressLoading() {
        showProgressLoading(true);
    }

    public void showProgressLoading(int resId) {
        showProgressLoading(getString(resId));
    }

    public void showProgressLoading(String message) {
        showProgressLoading(message, true);
    }

    public void showProgressLoading(final boolean cancelable) {
        showProgressLoading(null, cancelable);
    }

    public void showProgressLoading(String message, boolean cancelable) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }

        mProgressDialog.setCancelable(cancelable);
        if (message != null) {
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.show();
    }

    public void dismissProgressLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void finish() {
        dismissProgressLoading();
        super.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

}
