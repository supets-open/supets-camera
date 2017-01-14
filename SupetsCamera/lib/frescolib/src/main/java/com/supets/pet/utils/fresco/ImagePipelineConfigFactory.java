package com.supets.pet.utils.fresco;

import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.supets.lib.okhttplibrary.Net;

public  class ImagePipelineConfigFactory {
        private static final String IMAGE_PIPELINE_CACHE_DIR = "supets_fresco_imagepipeline_cache";
        private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
        public static final int MAX_DISK_CACHE_SIZE = 300 * ByteConstants.MB;
        public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 8;

        private static ImagePipelineConfig sImagePipelineConfig;

        public static ImagePipelineConfig getImagePipelineConfig(Context context) {

            if (sImagePipelineConfig == null) {
                ImagePipelineConfig.Builder configBuilder = OkHttpImagePipelineConfigFactory.newBuilder(context, Net.createOkHttp3());
                //ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
                configBuilder.setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                    public MemoryCacheParams get() {
                        return ImagePipelineConfigFactory.get();
                    }
                });
                configBuilder.setMainDiskCacheConfig(getDefaultMainDiskCacheConfig(context));
                configBuilder.setDownsampleEnabled(true);//设置向下采样
                sImagePipelineConfig = configBuilder.build();
            }
            return sImagePipelineConfig;
        }

        private static DiskCacheConfig getDefaultMainDiskCacheConfig(final Context context) {
            return DiskCacheConfig.newBuilder(context)
                    .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                    .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                    .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                    .setMaxCacheSizeOnLowDiskSpace(10485760L)
                    .setMaxCacheSizeOnVeryLowDiskSpace(2097152L).build();
        }

        private static MemoryCacheParams get() {
            return new MemoryCacheParams(
                    MAX_MEMORY_CACHE_SIZE,
                    128,
                    MAX_MEMORY_CACHE_SIZE,
                    Integer.MAX_VALUE,
                    Integer.MAX_VALUE);
        }
    }