package com.supets.pet.multiimageselector.holder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supets.pet.multiimageselector.ImageConfig;
import com.supets.pet.multiimageselector.MultiViewCallBack;
import com.supets.pet.multiimageselector.R;
import com.supets.pet.multiimageselector.adapter.FolderAdapter;
import com.supets.pet.multiimageselector.adapter.ImageGridAdapter;
import com.supets.pet.multiimageselector.model.Folder;
import com.supets.pet.multiimageselector.model.Image;
import com.supets.pet.multiimageselector.widget.CameraPictureViewHolder;
import com.supets.pet.multiimageselector.widget.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MultiImageSelectorView extends FrameLayout
        implements CameraPictureViewHolder.OnMYMultiImageListener, View.OnClickListener {

    private TextView mSubmitButton;
    private RecyclerView mRecyclerView;
    private TextView mCategoryText;
    private ListView mListview;

    private MultiViewCallBack mCallback;

    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;
    private Folder mSelectorFolder;

    private int mDesireImageCount = ImageConfig.MaxNum;
    private int mode = ImageConfig.MODE_SINGLE;
    private List<Image> result = new ArrayList<>();

    private PictureBase mApi = new PictureBase();

    public MultiImageSelectorView(Context context) {
        super(context);
        initView();
    }

    public MultiImageSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MultiImageSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiImageSelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void setCallback(MultiViewCallBack viewCallBack) {
        this.mCallback = viewCallBack;
    }

    public void initView() {
        View.inflate(getContext(), R.layout.multi_image_activity, this);
        mCategoryText = (TextView) findViewById(R.id.header_title_text);
        mCategoryText.setSelected(false);
        mListview = (ListView) findViewById(R.id.folder);
        mRecyclerView = (RecyclerView) findViewById(R.id.grid);
        mSubmitButton = (TextView) findViewById(R.id.header_right_btn);
        //mPreviewBtn = (Button) findViewById(R.id.preview);
        //mPreviewBtn.setOnClickListener(this);

        mSubmitButton.setOnClickListener(this);
        mCategoryText.setOnClickListener(this);
        findViewById(R.id.header_left).setOnClickListener(this);
    }

    public void setData(boolean isShowCamera, int cameraBg,
                        int mode, ArrayList<Image> results, int maxNum) {
        this.mDesireImageCount = maxNum;
        this.mode = mode;
        this.result = results;

        if (mode == ImageConfig.MODE_SINGLE) {
            mSubmitButton.setVisibility(View.GONE);
        } else {
            mSubmitButton.setVisibility(View.VISIBLE);
            updateSummitText();
        }

        mCategoryText.setText("所有图片");
        mCategoryText.setEnabled(false);

        //updatePr();

        mImageAdapter = new ImageGridAdapter(getContext());
        mImageAdapter.setCameraType(cameraBg);
        mImageAdapter.setShowCamera(isShowCamera);
        mImageAdapter.showSelectIndicator(mode == ImageConfig.MODE_MULTI);
        mImageAdapter.setOnMYMultiImageListener(this);
        mImageAdapter.select(result);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getContext()));
        mRecyclerView.setAdapter(mImageAdapter);

        mFolderAdapter = new FolderAdapter(getContext());

        mListview.setPadding(0, 0, 0, com.supets.commons.utils.UIUtils.getScreenHeight() / 8);
        mListview.setAdapter(mFolderAdapter);
        mListview.setVisibility(View.GONE);
        mListview.setOnTouchListener(new View.OnTouchListener() {

            private long mTouchDown;
            private PointF mPoint;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float dx = Math.abs(event.getY() - mPoint.y);
                    float dy = Math.abs(event.getX() - mPoint.x);
                    if (System.currentTimeMillis() - mTouchDown < 300 && dx < 8 && dy < 8) {
                        mListview.setVisibility(View.GONE);
                        mCategoryText.setSelected(false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTouchDown = System.currentTimeMillis();
                    mPoint = new PointF(event.getX(), event.getY());
                }
                return false;
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long l) {

                mFolderAdapter.setSelectIndex(position);
                MultiImageSelectorView.this.mListview.setVisibility(View.GONE);
                if (position == 0) {
                    mCategoryText.setText("所有照片");
                    mSelectorFolder = null;
                    mImageAdapter.addData(mApi.getAllImages(), true);
                } else {
                    Folder folder = (Folder) adapterView.getAdapter().getItem(
                            position);
                    if (null != folder) {
                        mSelectorFolder = folder;
                        mCategoryText.setText(folder.name);
                        mImageAdapter.addData(mSelectorFolder.images, true);
                    }
                }
                mRecyclerView.scrollToPosition(0);
            }
        });

        requestData();
    }

    private void requestData() {
        if (mCallback != null) {
            mCallback.showProgressLoading();
        }
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                mApi.getPictureData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    findResultCallBack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("all");
    }

    private void findResultCallBack() {
        mFolderAdapter.setData(mApi.getAllFolder());
        mImageAdapter.addData(mApi.getAllImages(), true);
        mCategoryText.setEnabled(true);
        if (mCallback != null) {
            mCallback.dismissProgressLoading();
        }
    }

    @Override
    public void onSelected(Image image) {
        selectImageFromGrid(image);
    }

    private void selectImageFromGrid(Image image) {

        if (image == null) {
            return;
        }
        // 多选模式
        if (mode == ImageConfig.MODE_MULTI) {
            if (result.contains(image)) {
                result.remove(image);
            } else {
                // 判断选择数量问题
                if (mDesireImageCount == result.size()) {
                    Toast.makeText(getContext(), R.string.msg_amount_limit,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                result.add(image);
            }

           // updatePr();
            updateSummitText();

            mImageAdapter.select(image);

        } else if (mode == ImageConfig.MODE_SINGLE) {
            // 单选模式
            onSingleImageSelected(image);
        }
    }

//    private void updatePr() {
//        mPreviewBtn.setVisibility(mode == ImageConfig.MODE_SINGLE ? View.GONE
//                : View.VISIBLE);
//
//        if (result.size() > 0) {
//            mPreviewBtn.setEnabled(true);
//            mPreviewBtn.setText(getResources().getString(R.string.preview)
//                    + "(" + result.size() + ")");
//        } else {
//            mPreviewBtn.setEnabled(false);
//            mPreviewBtn.setText(R.string.preview);
//        }
//    }

    @Override
    public void onItemClick(Image image) {
        if (image != null) {
            if (mode == ImageConfig.MODE_SINGLE) {
                // 单选模式
                onSingleImageSelected(image);
            } else {
                selectImageFromGrid(image);
                //viewpager筛选
            }
        }
    }

    @Override
    public void onCamera() {
        if (mCallback != null) {
            mCallback.onClickCamera();
        }
    }


    //判断文件夹是否显示
    private boolean isShowList() {
        if (mListview.getVisibility() == View.VISIBLE) {
            mListview.setVisibility(View.GONE);
            mCategoryText.setSelected(false);
            return true;
        }
        return false;
    }

    //显示文件夹列表
    private void showListView() {
        if (mFolderAdapter.isEmpty()) {
            return;
        }
        if (mListview.getVisibility() == View.VISIBLE) {
            mListview.setVisibility(View.GONE);
            mCategoryText.setSelected(false);
        } else {
            mListview.setVisibility(View.VISIBLE);
            mCategoryText.setSelected(true);
        }
        int index = mFolderAdapter.getSelectIndex();
        index = index == 0 ? index : index - 1;
        mListview.setSelection(index);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_title_text) {
            showListView();
        }
        if (v.getId() == R.id.header_left) {
            if (!isShowList()) {
                if (mCallback != null) {
                    mCallback.onBack();
                }
            }
        }
        if (v.getId() == R.id.header_right_btn) {
            if (result != null && result.size() > 0) {
                if (mCallback != null) {
                    mCallback.onSelectFinished();
                }
            }
        }
//        if (v.getId() == R.id.preview) {
//            if (result != null && result.size() > 0) {
//              //预览删除
//            }
//        }
    }

    private void updateSummitText() {
        boolean isEnableSubmit = result.size() > 0;
        mSubmitButton.setEnabled(isEnableSubmit);

        String submitText = isEnableSubmit ? "完成(" + result.size() + "/"
                + mDesireImageCount + ")" : "完成";
        mSubmitButton.setText(submitText);
    }

    //单选结果回调
    public void onSingleImageSelected(Image image) {
        result.add(image);
        mCallback.onSelectFinished();
    }

}
