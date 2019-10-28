package com.softwinner.bionrecorder.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

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

import static com.softwinner.bionrecorder.R.id.recordSV;
import static com.softwinner.bionrecorder.common.AppConfig.FRONT_CAMERA_ID;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class RecordFragment extends Fragment implements RecordActivity.ServiceBindedListener,
        RecordActivity.RecorderListener, UsbCameraStateReceiver.CameraStatusListener {
    private static final String TAG = "RecordFragment";
    private static final boolean DEBUG = true;
    private static final String ARG_CAMERA_TYPE = "camera_type";
    private static final String ARG_CAMERA_ID = "camera_id";
    private int mCameraType = AppConfig.FRONT_CAMERA; // camera类型，默认前路
    private int mCameraId = FRONT_CAMERA_ID;
    private RecordService mRecordService;

    // @BindView(R.id.recordSV)
    SurfaceView mRecordSV;

    public RecordFragment() {}

    public static RecordFragment newInstance(int cameraType, int cameraId) {
        RecordFragment fragment = new RecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CAMERA_TYPE, cameraType);
        bundle.putInt(ARG_CAMERA_ID, cameraId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onPlugIn(int cameraType) {
        if (DEBUG) {
            Log.d(TAG, "------Plug In Camera cameraType = " + cameraType);
        }
        if (mRecordService != null && !mRecordService.isPreviewing(mCameraId)) {
            if (DEBUG) {
                Log.d(TAG, "----onPlugIn--");
            }
            Flowable.just("onPlugIn")
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (DEBUG) {
                                Log.d(TAG, "-----onPlugIn-----cameraId = " + mCameraId);
                            }
                            mRecordService.addCamera(mCameraType, mCameraId);
                            mRecordService.openCamera(mCameraId);
                            mRecordService.setPreviewDisplay(mCameraId, mRecordSV.getHolder());
                            mRecordService.startPreview(mCameraId);
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

        if (mRecordService.isRecording(mCameraId)) {
            mRecordService.stopRecord(mCameraId);
            mRecordService.closeCamera(mCameraId);
        }

        if (mRecordService.isPreviewing(mCameraId)) {
            mRecordService.stopPreview(mCameraId);
            mRecordService.closeCamera(mCameraId);
        }
    }

    @Override
    public void startRecord() {
        if (mRecordService != null) {
            mRecordService.startRecordAsync(mCameraId);
        }
    }

    @Override
    public void takePicture() {
        if (mRecordService != null) {
            mRecordService.takePictureAsync(mCameraId);
        }
    }

    @Override
    public void stopRecord() {
        if (mRecordService != null) {
            mRecordService.stopRecordAsync(mCameraId);
        }
    }

    @Override
    public void lock(boolean lock) {
        if (mRecordService != null) {
            mRecordService.setLock(mCameraId, lock);
        }
    }

    @Override
    public int getDuration() {
        if (mCameraType == AppConfig.FRONT_CAMERA) {
            return AppConfig.getInstance(getActivity().getApplicationContext())
                    .getInt(AppConfig.FRONT_RECORD_DURATION, AppConfig.DEFAULT_RECORD_DURATION);

        } else if (mCameraType == AppConfig.BACK_CAMERA) {
            return AppConfig.getInstance(getActivity().getApplicationContext())
                    .getInt(AppConfig.BACK_RECORD_DURATION, AppConfig.DEFAULT_RECORD_DURATION);
        }
        return AppConfig.DEFAULT_RECORD_DURATION;
    }

    @Override
    public void setNextSaveFile() {
        if (mRecordService != null) {
            mRecordService.setNextSaveFileAsync(mCameraId);
        }
    }

    SurfaceHolder.Callback callback =  new SurfaceHolder.Callback() {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (DEBUG) {
                Log.d(TAG, "--------surfaceDestroyed-------");
            }

            if (mRecordService != null && mRecordService.isPreviewing()) {
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
                Log.d(TAG, "--------surfaceCreated-------");
            }

            if (mRecordService != null && !mRecordService.isPreviewing()) {
                Flowable.just("surfaceCreated")
                        .observeOn(Schedulers.newThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                if (DEBUG) {
                                    Log.d(TAG, "-----surfaceCreated-----cameraId = " + mCameraId);
                                }
                                mRecordService.addCamera(mCameraType, mCameraId);
                                mRecordService.openCamera(mCameraId);
                                mRecordService.setPreviewDisplay(mCameraId, holder);
                                mRecordService.startPreview(mCameraId);
                            }
                        });

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCameraId = getArguments().getInt(ARG_CAMERA_ID);
            mCameraType = getArguments().getInt(ARG_CAMERA_TYPE);
        }
        ((RecordActivity) getActivity()).addServiceBindedListener(this);
        ((RecordActivity) getActivity()).setRecorderListener(this);
        if (DEBUG) Log.d(TAG, "------onCreate-----");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        // ButterKnife.bind(this, view);
        mRecordSV = (SurfaceView) view.findViewById(R.id.recordSV);

        mRecordSV.getHolder().addCallback(callback);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onServiceBinded(RecordService recordService) {
        mRecordService = recordService;
        mRecordService.setUsbCameraStateListener(this);
        if (mRecordService != null && !mRecordService.isPreviewing(mCameraId)) {
            if (DEBUG) {
                Log.d(TAG, "----onServiceBinded--");
            }
            Flowable.just("onServiceBinded")
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (DEBUG) {
                                Log.d(TAG, "-----onServiceBinded-----cameraId = " + mCameraId);
                            }
                            mRecordService.addCamera(mCameraType, mCameraId);
                            mRecordService.openCamera(mCameraId);
                            mRecordService.setPreviewDisplay(mCameraId, mRecordSV.getHolder());
                            mRecordService.startPreview(mCameraId);
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        if (mRecordService != null && mRecordService.isRecording(mCameraId)) {
            mRecordService.stopRecord(mCameraId);
            mRecordService.closeCamera(mCameraId);
        }

        if (mRecordService != null && mRecordService.isPreviewing(mCameraId)) {
            mRecordService.stopPreview(mCameraId);
            mRecordService.closeCamera(mCameraId);
        }

        super.onDestroy();
    }
}
