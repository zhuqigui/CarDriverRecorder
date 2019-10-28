package com.softwinner.bionrecorder.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.common.AppConfig;
import com.softwinner.bionrecorder.ui.activity.RecordActivity;
import com.softwinner.bionrecorder.ui.receiver.UsbCameraStateReceiver;
import com.softwinner.bionrecorder.ui.service.RecordService;

// import butterknife.BindView;
// import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class DoubleRecordFragment extends Fragment implements RecordActivity.ServiceBindedListener,
        RecordActivity.RecorderListener, UsbCameraStateReceiver.CameraStatusListener{
    private static final String TAG = "DoubleRecordFragment";
    private static final boolean DEBUG = true;
    private static final String ARG_FRONT_CAMERA_ID = "front_camera_id";
    private static final String ARG_BACK_CAMERA_ID = "back_camera_id";

    private int mCurrCameraType = AppConfig.FRONT_CAMERA; // 默认前录是大屏
    private RecordService mRecordService;
    private int mFrontCameraId = AppConfig.FRONT_CAMERA_ID;
    private int mBackCameraId = AppConfig.BACK_CAMERA_ID;
    private SurfaceHolderCallback mFrontCallback;
    private SurfaceHolderCallback mBackCallback;

    // @BindView(R.id.frontRecordSV)
    SurfaceView mFrontRecordSV;
    // @BindView(R.id.backRecordSV)
    SurfaceView mBackRecordSV;
    // @BindView(R.id.frontFL)
    FrameLayout mFrontFL;
    // @BindView(R.id.backFL)
    FrameLayout mBackFL;
    // @BindView(R.id.doubleRecordFL)
    FrameLayout mDoubleRecordFL;


    public DoubleRecordFragment() {}

    public static DoubleRecordFragment newInstance(int frontCameraId, int backCameraId) {
        DoubleRecordFragment fragment = new DoubleRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_FRONT_CAMERA_ID, frontCameraId);
        bundle.putInt(ARG_BACK_CAMERA_ID, backCameraId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onPlugIn(int cameraType) {
        if (DEBUG) {
            Log.d(TAG, "------Plug In Camera cameraType = " + cameraType);
        }
        if (mRecordService == null) {
            return;
        }

        if (cameraType == AppConfig.FRONT_CAMERA && !mRecordService.isPreviewing(mFrontCameraId)) {
            Flowable.just("onPlugIn")
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (DEBUG) {
                                Log.d(TAG, "----onPlugIn--for front camera---");
                            }
                            mRecordService.addCamera(AppConfig.FRONT_CAMERA, mFrontCameraId);
                            mRecordService.openCamera(mFrontCameraId);
                            mRecordService.setPreviewDisplay(mFrontCameraId, mFrontRecordSV.getHolder());
                            mRecordService.startPreview(mFrontCameraId);
                        }
                    });

        }

        if (cameraType == AppConfig.BACK_CAMERA && !mRecordService.isPreviewing(mBackCameraId)) {
            Flowable.just("onPlugIn")
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (DEBUG) {
                                Log.d(TAG, "----onPlugIn--for back camera---");
                            }
                            mRecordService.addCamera(AppConfig.BACK_CAMERA, mBackCameraId);
                            mRecordService.openCamera(mBackCameraId);
                            mRecordService.setPreviewDisplay(mBackCameraId, mBackRecordSV.getHolder());
                            mRecordService.startPreview(mBackCameraId);
                        }
                    });
        }
    }

    @Override
    public void onPlugOut(int cameraType) {
        if (DEBUG) {
            Log.d(TAG, "------Plug Out Camera cameraType = " + cameraType);
        }

        if (mRecordService == null) {
            return;
        }

        if (cameraType == AppConfig.FRONT_CAMERA) {
            if (DEBUG) Log.d(TAG, "----onPlugOut---for front camera---");
            if (mRecordService.isRecording(mFrontCameraId)) {
                mRecordService.stopRecord(mFrontCameraId);
                mRecordService.closeCamera(mFrontCameraId);
            }

            if (mRecordService.isPreviewing(mFrontCameraId)) {
                mRecordService.stopPreview(mFrontCameraId);
                mRecordService.closeCamera(mFrontCameraId);
            }

        } else if (cameraType == AppConfig.BACK_CAMERA) {
            if (DEBUG) Log.d(TAG, "----onPlugOut---for back camera---");
            if (mRecordService.isRecording(mBackCameraId)) {
                mRecordService.stopRecord(mBackCameraId);
                mRecordService.closeCamera(mBackCameraId);
            }

            if (mRecordService.isPreviewing(mBackCameraId)) {
                mRecordService.stopPreview(mBackCameraId);
                mRecordService.closeCamera(mBackCameraId);
            }
        }
    }

    @Override
    public void startRecord() {
        if (mCurrCameraType == AppConfig.FRONT_CAMERA) {
            mRecordService.startRecordAsync(mFrontCameraId);
        } else if (mCurrCameraType == AppConfig.BACK_CAMERA) {
            mRecordService.startRecordAsync(mBackCameraId);
        }
    }

    @Override
    public void takePicture() {
        if (mCurrCameraType == AppConfig.FRONT_CAMERA) {
            mRecordService.takePictureAsync(mFrontCameraId);
        } else if (mCurrCameraType == AppConfig.BACK_CAMERA) {
            mRecordService.takePictureAsync(mBackCameraId);
        }
    }

    @Override
    public void stopRecord() {
        if (mCurrCameraType == AppConfig.FRONT_CAMERA) {
            mRecordService.stopRecordAsync(mFrontCameraId);
        } else if (mCurrCameraType == AppConfig.BACK_CAMERA) {
            mRecordService.stopRecord(mBackCameraId);
        }
    }

    @Override
    public void lock(boolean lock) {
        if (mRecordService != null) {
            if (mCurrCameraType == AppConfig.FRONT_CAMERA) {
                mRecordService.setLock(mFrontCameraId, lock);
            } else if (mCurrCameraType == AppConfig.BACK_CAMERA) {
                mRecordService.setLock(mBackCameraId, lock);
            }
        }
    }

    @Override
    public int getDuration() {
        if (mCurrCameraType == AppConfig.FRONT_CAMERA) {
            return AppConfig.getInstance(getActivity().getApplicationContext())
                    .getInt(AppConfig.FRONT_RECORD_DURATION, AppConfig.DEFAULT_RECORD_DURATION);

        } else if (mCurrCameraType == AppConfig.BACK_CAMERA) {
            return AppConfig.getInstance(getActivity().getApplicationContext())
                    .getInt(AppConfig.BACK_RECORD_DURATION, AppConfig.DEFAULT_RECORD_DURATION);
        }
        return AppConfig.DEFAULT_RECORD_DURATION;
    }

    @Override
    public void setNextSaveFile() {
        if (mRecordService != null) {
            if (mCurrCameraType == AppConfig.FRONT_CAMERA) {
                mRecordService.setNextSaveFileAsync(mFrontCameraId);
            } else if (mCurrCameraType == AppConfig.BACK_CAMERA) {
                mRecordService.setNextSaveFileAsync(mBackCameraId);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFrontCameraId = getArguments().getInt(ARG_FRONT_CAMERA_ID);
            mBackCameraId = getArguments().getInt(ARG_BACK_CAMERA_ID);
        }
        ((RecordActivity) getActivity()).addServiceBindedListener(this);
        ((RecordActivity) getActivity()).setRecorderListener(this);

        if (DEBUG) {
            Log.d(TAG, "------onCreate-----");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_double_record, container, false);

        mFrontRecordSV = (SurfaceView) view.findViewById(R.id.frontRecordSV);
        mBackRecordSV = (SurfaceView) view.findViewById(R.id.backRecordSV);
        mFrontFL = (FrameLayout) view.findViewById(R.id.frontFL);
        mBackFL = (FrameLayout) view.findViewById(R.id.backFL);
        mDoubleRecordFL = (FrameLayout) view.findViewById(R.id.doubleRecordFL);

        // ButterKnife.bind(this, view);

        mFrontCallback = new SurfaceHolderCallback(AppConfig.FRONT_CAMERA, mFrontCameraId);
        mFrontRecordSV.getHolder().addCallback(mFrontCallback);

        mBackCallback = new SurfaceHolderCallback(AppConfig.BACK_CAMERA, mBackCameraId);
        mBackRecordSV.getHolder().addCallback(mBackCallback);

        mBackRecordSV.setZOrderMediaOverlay(true);

        addClickListener();

        return view;
    }

    private void addClickListener() {
        mFrontFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG) {
                    Log.d(TAG, "------click frontRecordSV type = " + mCurrCameraType);
                }
                if (mCurrCameraType == AppConfig.BACK_CAMERA) {
                    if (DEBUG) {
                        Log.d(TAG, "------click frontRecordSV and switch view-----");
                    }
                    switchView();
                }
            }
        });

        mBackFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG) {{
                    Log.d(TAG, "------click backRecordSV type = " + mCurrCameraType);
                }}
                if (mCurrCameraType == AppConfig.FRONT_CAMERA) {
                    if (DEBUG) Log.d(TAG, "------click backRecordSV and switch view-----");
                    switchView();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onServiceBinded(RecordService recordService) {
        if (DEBUG) {
            Log.d(TAG, "----onServiceBinded--");
        }
        mRecordService = recordService;
        mRecordService.setUsbCameraStateListener(this);
        if (mRecordService != null && !mRecordService.isPreviewing(mFrontCameraId)) {
            Flowable.just("onServiceBinded")
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (DEBUG) {
                                Log.d(TAG, "----onServiceBinded--for front camera---");
                            }
                            mRecordService.addCamera(AppConfig.FRONT_CAMERA, mFrontCameraId);
                            mRecordService.openCamera(mFrontCameraId);
                            mRecordService.setPreviewDisplay(mFrontCameraId, mFrontRecordSV.getHolder());
                            mRecordService.startPreview(mFrontCameraId);
                        }
                    });
        }

        if (mRecordService != null && !mRecordService.isPreviewing(mBackCameraId)) {
            Flowable.just("onServiceBinded")
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (DEBUG) {
                                Log.d(TAG, "----onServiceBinded--for back camera---");
                            }
                            mRecordService.addCamera(AppConfig.BACK_CAMERA, mBackCameraId);
                            mRecordService.openCamera(mBackCameraId);
                            mRecordService.setPreviewDisplay(mBackCameraId, mBackRecordSV.getHolder());
                            mRecordService.startPreview(mBackCameraId);
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        /*
         * 如果正在录制，停止录制，并关闭camera
         */
        if (mRecordService.isRecording(mFrontCameraId)) {
            mRecordService.stopRecord(mFrontCameraId);
            mRecordService.closeCamera(mFrontCameraId);
        }

        if (mRecordService.isRecording(mBackCameraId)) {
            mRecordService.stopRecord(mBackCameraId);
            mRecordService.closeCamera(mBackCameraId);
        }

        /*
         * 如果正在预览，停止预览，并关闭camera
         */
        if (mRecordService.isPreviewing(mFrontCameraId)) {
            mRecordService.stopPreview(mFrontCameraId);
            mRecordService.closeCamera(mFrontCameraId);
        }

        if (mRecordService.isPreviewing(mBackCameraId)) {
            mRecordService.stopPreview(mBackCameraId);
            mRecordService.closeCamera(mBackCameraId);
        }

        super.onDestroy();
    }


    /**
     * 切换前后录的大小屏显示
     */
    private void switchView() {
        if (mCurrCameraType == AppConfig.FRONT_CAMERA) {

            if (DEBUG) {
                Log.d(TAG, "------switch view from front to back-----");
            }
            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mBackFL.setLayoutParams(lp1);
            mDoubleRecordFL.removeView(mFrontFL);
            mDoubleRecordFL.removeView(mBackFL);

            FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(240, 160);
            lp2.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            mFrontFL.setLayoutParams(lp2);
            mDoubleRecordFL.addView(mBackFL);
            mDoubleRecordFL.addView(mFrontFL);

            addClickListener();

            mCurrCameraType = AppConfig.BACK_CAMERA;
            // 小窗口位于顶层
            mBackRecordSV.setZOrderMediaOverlay(false);
            mFrontRecordSV.setZOrderMediaOverlay(true);

        } else if (mCurrCameraType == AppConfig.BACK_CAMERA) {
            if (DEBUG) {
                Log.d(TAG, "------switch view from back to front-----");
            }
            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mFrontFL.setLayoutParams(lp1);
            mDoubleRecordFL.removeView(mBackFL);
            mDoubleRecordFL.removeView(mFrontFL);

            FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(240, 160);
            lp2.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            mBackFL.setLayoutParams(lp2);
            mDoubleRecordFL.addView(mFrontFL);
            mDoubleRecordFL.addView(mBackFL);

            addClickListener();
            mCurrCameraType = AppConfig.FRONT_CAMERA;

            // 小窗口位于顶层
            mFrontRecordSV.setZOrderMediaOverlay(false);
            mBackRecordSV.setZOrderMediaOverlay(true);
        }

//        frontRecordSV.backgroundColor = Color.parseColor("#00000000")
//        backRecordSV.backgroundColor = Color.parseColor("#00000000")
    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        private int mCameraType;
        private int mCameraId;

        public SurfaceHolderCallback(int cameraType, int cameraId) {
            mCameraType = cameraType;
            mCameraId = cameraId;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (DEBUG) {
                Log.d(TAG, "--------surfaceDestroyed-------" + mRecordService);
            }

            if (mRecordService == null) {
                return;
            }

//            if (mRecordService.isPreviewing(mCameraId)) {
//                Flowable.just("surfaceDestroyed")
//                        .observeOn(Schedulers.newThread())
//                        .subscribe(new Consumer<String>() {
//                            @Override
//                            public void accept(String s) throws Exception {
//                                if (DEBUG) {
//                                    Log.d(TAG, "--------surfaceDestroyed-------cameraId = " + mCameraId);
//                                }
//                                mRecordService.stopPreview(mCameraId);
//                                mRecordService.closeCamera(mCameraId);
//                            }
//                        });
//
//            }

            if (mRecordService.isPreviewing(mCameraId)) {
                if (DEBUG) {
                    Log.d(TAG, "--------surfaceDestroyed-------cameraId = " + mCameraId);
                }
                mRecordService.stopPreview(mCameraId);
                mRecordService.closeCamera(mCameraId);
            }
        }

        @Override
        public void surfaceCreated(final SurfaceHolder holder) {
            if (DEBUG) {
                Log.d(TAG, "--------surfaceCreated-------" + mRecordService);
            }

            if (mRecordService != null && !mRecordService.isPreviewing(mCameraId)) {
                Flowable.just("surfaceCreated")
                        .observeOn(Schedulers.io())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                if (DEBUG) {
                                    Log.d(TAG, "-----surfaceCreated-----cameraId =" + mCameraId);
                                }
                                mRecordService.addCamera(mCameraType, mCameraId);
                                mRecordService.openCamera(mCameraId);
                                mRecordService.setPreviewDisplay(mCameraId, holder);
                                mRecordService.startPreview(mCameraId);
                            }
                        });
            }
        }
    }
}
