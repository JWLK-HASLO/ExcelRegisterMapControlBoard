LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

FILES := NativeProcessing.cpp

LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := $(FILES)
LOCAL_CFLAGS = -DSTDC_HEADERS
LOCAL_LDLIBS := -llog


include $(BUILD_SHARED_LIBRARY)