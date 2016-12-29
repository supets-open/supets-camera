package com.supets.pet.multiimageselector.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.supets.pet.multiimageselector.R;
import com.supets.pet.multiimageselector.model.Folder;
import com.supets.pet.utils.fresco.FrescoUtils;
import com.supets.pet.utils.fresco.RatioFrescoImageView;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends BaseAdapter {


    private List<Folder> mFolders = new ArrayList<Folder>();

    int lastSelected = 0;

    Context mContext;

    public FolderAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Folder> folders) {
        if (folders != null && folders.size() > 0) {
            mFolders = folders;
        } else {
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size() + 1;
    }

    @Override
    public Folder getItem(int i) {
        if (i == 0)
            return null;
        return mFolders.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.multi_image_list_item_folder,
                    null);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if (i == 0) {
                String all = "所有图片" + "(" + getTotalImageSize() + ")";
                holder.name.setText(all);
                if (mFolders.size() > 0) {
                    final Folder f = mFolders.get(0);
                    if (lazyLoad) {
                        if (TextUtils.isEmpty(f.cover.mThumb)) {
                            FrescoUtils.displayImageWithSmall("file://" + f.cover.path, holder.cover);
                        } else {
                            FrescoUtils.displayImageWithSmall("file://" + f.cover.mThumb, holder.cover);
                        }
                    } else {
                        FrescoUtils.displayImage("", holder.cover);
                    }
                }
            } else {
                holder.bindData(getItem(i), lazyLoad);
            }
            //if (lastSelected == i) {
            //	holder.indicator.setVisibility(View.VISIBLE);
            //} else {
            holder.indicator.setVisibility(View.INVISIBLE);
            //}
        }
        return view;
    }

    private int getTotalImageSize() {
        int result = 0;
        if (mFolders != null && mFolders.size() > 0) {
            for (Folder f : mFolders) {
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i)
            return;

        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    public static class ViewHolder {
        private RatioFrescoImageView cover;
        private TextView name;
        private ImageView indicator;

        public ViewHolder(View view) {
            cover = (RatioFrescoImageView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }

        public void bindData(Folder data, boolean lazyLoad) {
            name.setText(getFormatSize(data.name, data.getSize()));
            if (lazyLoad) {
                FrescoUtils.displayImage("file://" + data.cover.path, cover);
            } else {
                FrescoUtils.displayImage("", cover);
            }
        }

        public String getFormatSize(String name, int size) {
            return name + "(" + size + ")";
        }
    }

    private boolean lazyLoad = true;

    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }
}
