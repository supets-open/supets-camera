package com.supets.pet.utils.fresco;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.supets.lib.frescolibrary.R;
import com.supets.lib.supetscontext.App;

import java.io.File;

public class FrescoUtils {

    public static final String TAG = "FrescoImageHelper";

    public static void init() {
        DraweeHierarchyBuilder.init(App.INSTANCE);
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfigFactory
                .getImagePipelineConfig(App.INSTANCE);
        Fresco.initialize(App.INSTANCE, imagePipelineConfig);
    }

    public static void loadImage(String uri) {
        loadImage(uri, null);
    }

    public static void loadImage(String uri, final ImagePreloadListener listener) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }

        if (imageExist(uri)) {
            Bitmap bitmap = getImageBitmapFromDiskCache(uri);
            if (listener != null && bitmap != null) {
                Log.v(TAG, "SD缓存加载");
                listener.onLoadingSuccess(bitmap);
                return;
            }
            bitmap = getImageBitmap(uri);
            if (listener != null && bitmap != null) {
                Log.v(TAG, "mem缓存加载");
                listener.onLoadingSuccess(bitmap);
                return;
            }
        }
        preLocalImageMemCache(uri, listener);
    }

    public static void loadImagePaster(String uri, final ImagePreloadListener listener) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }

        if (imageExist(uri)) {
            Bitmap bitmap = getImageBitmapFromDiskCache(uri);
            if (listener != null && bitmap != null) {
                Log.v(TAG, "SD缓存加载");
                listener.onLoadingSuccess(bitmap);
                return;
            }
            bitmap = getImageBitmap(uri);
            if (listener != null && bitmap != null) {
                Log.v(TAG, "mem缓存加载");
                listener.onLoadingSuccess(bitmap);
                return;
            }
        }
        preLocalImageMemCachePaste(uri, listener);
    }

    public static boolean imageExistInMemory(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return false;
        }
        Uri formedUri = Uri.parse(uri);
        boolean result = Fresco.getImagePipeline().isInBitmapMemoryCache(formedUri);
        Log.v(TAG, "缓存存在判断" + result);
        return result;
    }

    public static boolean imageExistInSDCard(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return false;
        }
        Uri formedUri = Uri.parse(uri);
        DataSource<Boolean> ds = Fresco.getImagePipeline().isInDiskCache(formedUri);
        Boolean result = ds.getResult();
        ds.close();
        if (result != null) {
            Log.v(TAG, "SD存在判断" + result);
            return result;
        } else {
            Log.v(TAG, "SD存在判断false");
            return false;
        }
    }

    public static void preLocalImageMemCachePaste(final String uri, final ImagePreloadListener
            mListener) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }
        final Uri formedUri = Uri.parse(uri);
        ImageRequest imageRequest = getResizeImageRequest(formedUri).build();
        DataSource<Void> ds = Fresco.getImagePipeline().prefetchToBitmapCache(imageRequest, null);
        ds.subscribe(new BaseDataSubscriber<Void>() {

            @Override
            protected void onNewResultImpl(DataSource<Void> dataSource) {
                Log.v(TAG, "success");
                if (mListener != null) {
                    File bp = getCachedImageOnDisk(formedUri);
                    Log.v(TAG, "cache-success" + bp);
                    if (bp != null&&bp.exists()) {
                        mListener.onLoadingSuccess(ImageUtils.readPhotoBitmap(bp.getAbsolutePath()));
                    } else {
                        mListener.onLoadingFailed();
                    }
                }
            }

            @Override
            protected void onFailureImpl(DataSource<Void> dataSource) {
                Log.v(TAG, "fail");
                if (mListener != null) {
                    mListener.onLoadingFailed();
                }
            }
        }, UiThreadImmediateExecutorService.getInstance());
    }

    public static void preLocalImageMemCache(final String uri, final ImagePreloadListener
            mListener) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }
        final Uri formedUri = Uri.parse(uri);
        ImageRequest imageRequest = getResizeImageRequest(formedUri).build();
        DataSource<Void> ds = Fresco.getImagePipeline().prefetchToBitmapCache(imageRequest, null);
        ds.subscribe(new BaseDataSubscriber<Void>() {

            @Override
            protected void onNewResultImpl(DataSource<Void> dataSource) {
                Log.v(TAG, "success");
                if (mListener != null) {
                    Bitmap bp = getImageBitmap(uri);
                    Log.v(TAG, "cache-success" + bp);
                    if (bp != null) {
                        mListener.onLoadingSuccess(bp);
                    } else {
                        mListener.onLoadingFailed();
                    }
                }
            }

            @Override
            protected void onFailureImpl(DataSource<Void> dataSource) {
                Log.v(TAG, "fail");
                if (mListener != null) {
                    mListener.onLoadingFailed();
                }
            }
        }, UiThreadImmediateExecutorService.getInstance());
    }

    public static void prefetchToBitmapCache(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }
        Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(uri), null);
    }

    public static void prefetchToDiskCache(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }
        Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(uri), null);
    }

    public static boolean imageExist(String uri) {
        return imageExistInMemory(uri) || imageExistInSDCard(uri);
    }

    public static Bitmap getImageBitmap(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Uri formedUri = Uri.parse(url);
        ImageRequest imageRequest = getResizeImageRequest(formedUri).build();

        DataSource<CloseableReference<CloseableImage>> ds = null;
        try {
            ds = Fresco.getImagePipeline().fetchImageFromBitmapCache(imageRequest, null);
            if (ds != null && ds.hasResult()) {
                CloseableReference<CloseableImage> obj = ds.getResult();
                if (obj != null) {
                    CloseableStaticBitmap csb = (CloseableStaticBitmap) obj.get();
                    return Bitmap.createBitmap(csb.getUnderlyingBitmap());
                }
            }
        } finally {
            if (ds != null) {
                ds.close();
            }
        }

        return null;
    }

    public static Bitmap getImageBitmapFromDiskCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Uri formedUri = Uri.parse(url);
        ImageRequest imageRequest = getResizeImageRequest(formedUri).build();

        DataSource<CloseableReference<CloseableImage>> ds = null;
        try {
            ds = Fresco.getImagePipeline().fetchDecodedImage(imageRequest, null);
            if (ds != null && ds.hasResult()) {
                CloseableReference<CloseableImage> obj = ds.getResult();
                if (obj != null) {
                    CloseableStaticBitmap csb = (CloseableStaticBitmap) obj.get();
                    return Bitmap.createBitmap(csb.getUnderlyingBitmap());
                }
            }
        } finally {
            if (ds != null) {
                ds.close();
            }
        }

        return null;
    }

    public static void displayImage(String uri, SimpleDraweeView simpleDraweeView, ImageLoadingListener
            listener, boolean playGif) {
        displayImageWithControllerListener(uri, simpleDraweeView, null, listener, playGif);
    }

    public static void displayImage(String uri, SimpleDraweeView simpleDraweeView, ImageLoadingListener
            listener) {
        displayImageWithControllerListener(uri, simpleDraweeView, null, listener, true);
    }

    public static void displayImage(String uri, SimpleDraweeView simpleDraweeView) {
        displayImageWithControllerListener(uri, simpleDraweeView, null, null, true);
    }

    public static void displayImage(String uri, SimpleDraweeView simpleDraweeView,
                                    GenericDraweeHierarchy genericDraweeHierarchy) {
        displayImageWithControllerListener(uri, simpleDraweeView, genericDraweeHierarchy, null, true);
    }

    private static void displayImageWithControllerListener(String uri,
                                                           SimpleDraweeView simpleDraweeView,
                                                           GenericDraweeHierarchy genericDraweeHierarchy,
                                                           ImageLoadingListener imageLoadingListener, boolean playGif
    ) {
        if (genericDraweeHierarchy != null) {
            simpleDraweeView.setHierarchy(genericDraweeHierarchy);
        }

        Uri formUri = Uri.parse(TextUtils.isEmpty(uri) ? "" : uri);
        ImageRequest request = getResizeImageRequest(formUri).build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(simpleDraweeView.getController())
                .setControllerListener(imageLoadingListener)
                .setAutoPlayAnimations(playGif)
                .setImageRequest(request)
                .build();
        simpleDraweeView.setController(controller);
    }

    public static void displayImageWithSmall(String uri, SimpleDraweeView view) {
        int width = App.INSTANCE.getResources().getDisplayMetrics().widthPixels/3;
        displayImageWithSmall(uri, view, width, width);
    }

    public static void displayImageWithSmall(String uri, SimpleDraweeView view, int
            width, int height) {

        if (isLoadingUri(uri, view)) {
            return;
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri ==
                null ? "" : uri))
                .setResizeOptions(new ResizeOptions(width, height))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(view.getController())
                .setImageRequest(imageRequest)
                .setControllerListener(new ImageLoadingRepeatListener(uri, view))
                .setAutoPlayAnimations(true)
                .build();
        view.setController(controller);
    }

    public static void displayImageWithNoRepeat(final String uri, final SimpleDraweeView view) {
        if (isLoadingUri(uri, view)) {
            return;
        }
        displayImage(uri, view, new ImageLoadingRepeatListener(uri, view));
    }

    private static boolean isLoadingUri(String uri, SimpleDraweeView view) {
        String tag = (String) view.getTag(R.id.tag_fresco_uri);
        Boolean success = (Boolean) view.getTag(R.id.tag_fresco_flag);
        if (tag != null && tag.equals(uri)) {
            if (imageExistInMemory(uri) && (success != null && success)) {
                Log.v(TAG, "相同URL不需要加载");
                return true;
            }
            Log.v(TAG, "相同URL需要请求");
        }
        Log.v(TAG, "不同URL加载");
        view.setTag(R.id.tag_fresco_uri, uri);
        view.setTag(R.id.tag_fresco_flag, false);
        return false;
    }

    public static void evictFromMemoryCache(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(uri);
    }

    public static void evictFromDiskCache(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromDiskCache(uri);
    }

    public static void evictFromCache(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromCache(uri);
    }

    public static void clearMemoryCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
    }

    public static void clearCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }

    public static void clearDiskCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearDiskCaches();
    }

    private static ImageRequestBuilder getResizeImageRequest(Uri formUri) {
        return ImageRequestBuilder.newBuilderWithSource(formUri);
                //.setResizeOptions(new ResizeOptions(ImageUtils.Max_Scale_Width, ImageUtils.Max_Scale_Height));
    }

    public static boolean isImageDownloaded(Uri loadUri) {
        if (loadUri == null) {
            return false;
        }
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri),null);
        return ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey) || ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey);
    }

    //return file or null
    public static File getCachedImageOnDisk(Uri loadUri) {
        File localFile = null;
        if (loadUri != null) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri),null);
            if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return localFile;
    }
}