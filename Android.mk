LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

ifeq ($(TARGET_BUILD_APPS),)
support_library_root_dir := frameworks/support
else
support_library_root_dir := prebuilts/sdk/current/support
endif

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v7-cardview \
                               android-support-v7-recyclerview \
                               android-support-v7-appcompat \
                               android-support-v4 \
                               photoview \
                               rxandroid \
                               reactive \
                               rxjava

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res \
                      ${support_library_root_dir}/v7/cardview/res \
                      ${support_library_root_dir}/v7/recyclerview/res \
                      ${support_library_root_dir}/v7/appcompat/res
                      

LOCAL_AAPT_FLAGS := --auto-add-overlay 
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.cardview                       
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.recyclerview 
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat

# LOCAL_RESOURCE_DIR += prebuilts/sdk/current/support/v7/appcompat/res
# LOCAL_RESOURCE_DIR += frameworks/base/core/res/res

LOCAL_PACKAGE_NAME := CarletDriverRecorder

LOCAL_PRIVILEGED_MODULE := true

LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

# LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := butterknife:libs/butterknife-8.7.0.jar
# LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += butterknife-annotations:libs/butterknife-annotations-8.7.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += photoview:libs/PhotoView-2.1.3.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += rxandroid:libs/rxandroid-2.0.1.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += rxjava:libs/rxjava-2.1.3.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += reactive:libs/reactive-streams-1.0.1.jar
# LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += appcompat:libs/appcompat-v7-25.3.1-sources.jar
# LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += recyclerview:libs/recyclerview-v7-25.3.1-sources.jar
# LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += cardview:libs/cardview-v7-25.3.1-sources.jar

include $(BUILD_MULTI_PREBUILT)
