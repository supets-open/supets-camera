package com.supets.pet.multiimageselector.widget;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.supets.pet.multiimageselector.R;
import com.supets.pet.multiimageselector.model.Image;
import com.supets.pet.utils.fresco.FrescoUtils;
import com.supets.pet.utils.fresco.RatioFrescoImageView;

import java.util.List;

public class CameraPictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RatioFrescoImageView image;
    private ImageView indicator;
    private View mask;
    private View mCamera;
    private View mWholeView;
    private View mCameraBg;

    private Image mData;

    public CameraPictureViewHolder(View view) {
        super(view);
        mWholeView = view;
        image = (RatioFrescoImageView) view.findViewById(R.id.image);
        indicator = (ImageView) view.findViewById(R.id.checkBox);
        mask = view.findViewById(R.id.mask);
        mCamera = view.findViewById(R.id.camera);
        mCameraBg = view.findViewById(R.id.camerabg);

        mCamera.setOnClickListener(this);
        image.setOnClickListener(this);
        indicator.setOnClickListener(this);
    }


    public View getWholeView() {
        return mWholeView;
    }

    public void bindData(final Image data, boolean showSelectIndicator,
                         List<Image> mSelectedImages) {
        if (data == null)
            return;
        this.mData = data;

        image.setVisibility(View.VISIBLE);
        mCamera.setVisibility(View.GONE);
        // 处理单选和多选状态
        if (showSelectIndicator) {
            indicator.setVisibility(View.VISIBLE);
            if (mSelectedImages.contains(data)) {
                // 设置选中状态
                indicator.setSelected(true);
                mask.setVisibility(View.VISIBLE);
            } else {
                // 未选择
                indicator.setSelected(false);
                mask.setVisibility(View.GONE);
            }
        } else {
            indicator.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(mData.mThumb)) {
            FrescoUtils.displayImageWithSmall("file://" + this.mData.path, image);
        } else {
            FrescoUtils.displayImageWithSmall("file://" + this.mData.mThumb, image);
        }
    }

    public void bindDataCamera(int isTa) {
        image.setVisibility(View.GONE);
        indicator.setVisibility(View.GONE);
        mask.setVisibility(View.GONE);
        mCamera.setVisibility(View.VISIBLE);
        if (isTa == 0) {
            mCameraBg.setBackgroundResource(R.drawable.camera_piture_take);
        }
        if (isTa == 1) {
            mCameraBg.setBackgroundResource(R.drawable.camera_bg_ta);
        }

        if (isTa == 2) {
            mCameraBg.setBackgroundResource(R.drawable.camera_bg_money);
        }
    }

    public interface OnMYMultiImageListener {
        void onSelected(Image image);

        void onItemClick(Image image);

        void onCamera();
    }

    private OnMYMultiImageListener mListener;

    public void setOnMYMultiImageListener(OnMYMultiImageListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v.getId() == R.id.checkBox) {
                mListener.onSelected(mData);
            }
            if (v.getId() == R.id.image) {
                mListener.onItemClick(mData);
            }
            if (v.getId() == R.id.camera) {
                mListener.onCamera();
            }
        }
    }
}
