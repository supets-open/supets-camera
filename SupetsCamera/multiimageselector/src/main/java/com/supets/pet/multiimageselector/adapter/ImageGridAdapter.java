package com.supets.pet.multiimageselector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supets.commons.utils.UIUtils;
import com.supets.pet.multiimageselector.R;
import com.supets.pet.multiimageselector.model.Image;
import com.supets.pet.multiimageselector.widget.CameraPictureViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends
        RecyclerView.Adapter<CameraPictureViewHolder> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;
    private boolean showCamera = true;
    private boolean showSelectIndicator = false;
    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();
    private int cellHeight = 240;
    private int isTa=0;

    public ImageGridAdapter(Context context) {
        mContext = context;
        int dp = UIUtils.getDimension(R.dimen.camera_picture_divheight);
        cellHeight = (UIUtils.getScreenWidth() - UIUtils.dp2px(dp + dp)) / 3;
    }

    public CameraPictureViewHolder.OnMYMultiImageListener mListener;

    public void setOnMYMultiImageListener(CameraPictureViewHolder.OnMYMultiImageListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 显示选择指示器
     *
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b)
            return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     *
     * @param image
     */
    public void select(Image image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    public void select(List<Image> result) {

        if (result == null || result.size() == 0) {
            mSelectedImages.clear();

        } else {
            for (int i = 0; i < result.size(); i++) {
                Image image = result.get(i);
                if (!mSelectedImages.contains(image)) {
                    mSelectedImages.add(image);
                }
            }

            for (int i = 0; i < mSelectedImages.size(); i++) {
                Image image = mSelectedImages.get(i);
                if (!result.contains(image)) {
                    mSelectedImages.remove(image);
                }
            }
        }

        notifyDataSetChanged();

    }


    /**
     * 设置数据集
     *
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }


    public int getCount() {
        return mImages.size();
    }

    public void addData(List<Image> images, boolean isfirst) {

        if (isfirst) {
            mImages.clear();
        }
        if (images != null && images.size() > 0) {
            mImages.addAll(images);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    public Image getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        } else {
            return mImages.get(i);
        }
    }

    public int getSelectPosition(Image image) {
        return mImages.indexOf(image);
    }

    @Override
    public int getItemCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public void onBindViewHolder(CameraPictureViewHolder mView, int position) {
        if (showCamera && position == 0) {
            mView.bindDataCamera(isTa);
        } else {
            Image myData = getItem(position);
            mView.bindData(myData, showSelectIndicator,
                    mSelectedImages);
        }
    }

    @Override
    public CameraPictureViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.multi_image_list_item_image, parent, false);
        RecyclerView.LayoutParams params = new LayoutParams(cellHeight, cellHeight);
        view.setLayoutParams(params);
        CameraPictureViewHolder holder = new CameraPictureViewHolder(view);
        holder.setOnMYMultiImageListener(mListener);
        return holder;
    }

    public void setCameraType(int ta) {
        isTa = ta;
    }
}
