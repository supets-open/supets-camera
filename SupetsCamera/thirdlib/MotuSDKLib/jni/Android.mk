LOCAL_PATH:= $(call my-dir)

# first lib, which will be built statically
#
include $(CLEAR_VARS)

LOCAL_MODULE    := mtprocessor-core
LOCAL_SRC_FILES := mtprocessor.c layer.c operation.c jlalgorithm.c faceBuffing.c darkCircle.c USM.c darkCircleArtificial.c bilateral.c DeHaze.c whiteBalance.c hdr.c gifProcess.c Progressive.c ColorTemperature.c MSRCR.c ColorViberation.c Decolorization.c LocaEnhance.c NightShoot.c autoContrast.c beeps.c shadow.c ShadowHighLight.c Edg.c skin.c lipStick.c
LOCAL_LDLIBS 		:= -L$(SYSROOT)/usr/lib -llog -lstdlib -lstdio -lmemory -lmath


include $(BUILD_STATIC_LIBRARY)


# second lib, which will depend on and include the first one
#
include $(CLEAR_VARS)

LOCAL_MODULE    := mtprocessor-jni
LOCAL_SRC_FILES := CMTProcessor.c
LOCAL_LDLIBS 		:= -L$(SYSROOT)/usr/lib -llog

LOCAL_STATIC_LIBRARIES := mtprocessor-core

include $(BUILD_SHARED_LIBRARY)

# third lib
#
include $(CLEAR_VARS)

LOCAL_SHARED_LIBRARIES := libandroid_runtime
LOCAL_MODULE    := opengljni
LOCAL_SRC_FILES := opengljni.cpp GLShaders.cpp
LOCAL_LDLIBS 		:= -L$(SYSROOT)/usr/lib -llog -lGLESv2

include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE := libjpeg-support
LOCAL_SRC_FILES := prebuilt/$(TARGET_ARCH_ABI)/libjpeg-support.so

include $(PREBUILT_SHARED_LIBRARY)

