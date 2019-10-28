package com.softwinner.bionrecorder.ui.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.common.AppConfig;
import com.softwinner.bionrecorder.ui.fragment.DoubleRecordFragment;
import com.softwinner.bionrecorder.ui.fragment.RecordFragment;
import com.softwinner.bionrecorder.ui.service.RecordService;
import com.softwinner.bionrecorder.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// import butterknife.BindView;
// import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.R.attr.duration;
import static android.R.attr.onClick;
import static com.softwinner.bionrecorder.R.id.recordDurationTV;
import static com.softwinner.bionrecorder.util.Utils.getFormatRecordTime;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class RecordActivity extends AppCompatActivity{
    public static String TAG = "RecordActivity";
    public static boolean DEBUG = true;
    public static int STATE_RECORD = 1; // 单录
    public static int STATE_DOUBLE_RECORD = 2; // 双录

    // @BindView(R.id.recordDurationTV)
    TextView mRecordDurationTV;
    // @BindView(R.id.recordDurationRL)
    RelativeLayout mRecordDurationView;

    private ImageButton mRecordBtn = null;
    private ImageButton mRecordLockBtn = null;
    private ImageButton mTakePictureBtn = null;
    private ImageButton mVideoFileBtn = null;
    private ImageButton mRecordSettingsBtn = null;

    private LinearLayout mFloatLayout = null;

    private Disposable timer;
    private int mState = STATE_DOUBLE_RECORD;
    private boolean isRecording = false;
    private boolean isLocked = false;
    private RecordService mRecordService = null;
    private RecorderListener mRecorderListener = null;
    private List<ServiceBindedListener> mServiceBindedListeners = new ArrayList<>();
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (DEBUG) {
                Log.d(TAG, "-----onServiceDisconnected------");
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (DEBUG) {
                Log.d(TAG, "-----onServiceConnected------");
            }
            mRecordService = ((RecordService.RecordBinder)service).getRecordService();
            if (DEBUG) {
                Log.d(TAG, "-----onServiceConnected----mServiceBindedListeners size = " +
                        mServiceBindedListeners.size());
            }
            for (ServiceBindedListener listener : mServiceBindedListeners) {
                listener.onServiceBinded(mRecordService);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record);

        mRecordDurationTV = (TextView) findViewById(R.id.recordDurationTV); 
        mRecordDurationView = (RelativeLayout) findViewById(R.id.recordDurationRL);

        // ButterKnife.bind(this);
    }

    private void init() {
        if (isRecording) {
            mRecordBtn.setImageResource(R.drawable.ic_fiber_manual_record_red_600_48dp);
        }

        if (isLocked) {
            mRecordLockBtn.setImageResource(R.drawable.ic_lock_outline_red_a400_48dp);
        }

        mRecordSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    new AlertDialog.Builder(RecordActivity.this, android.R.style.Theme_Material_Dialog_Alert)
                            .setTitle("进入设置会停止录像")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    stopRecord();
                                    startSettingsActivity();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                } else {
                    startSettingsActivity();
                }
            }
        });

        mVideoFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileActivity();
            }
        });

        mTakePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorderListener.takePicture();
            }
        });

        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    if (DEBUG) {
                        Log.d(TAG, "---recordBtn.setOnClickListener will start record----" + isRecording);
                    }
                    startRecord();
                    // 暂时录制一分钟
                    final long duration = mRecorderListener.getDuration() * 60;
                    timer = Flowable.interval(0L, 1L, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long time) throws Exception {
                                    if (DEBUG) Log.d(TAG, "----current record time = $time----");
                                    if (time != 0L && time%duration == 0L) {
                                        // 单词录制结束，开始录制下一个
                                        mRecorderListener.setNextSaveFile();
                                        mRecordDurationTV.setText(getFormatRecordTime(duration));
                                        Toast.makeText(RecordActivity.this, "录制下一个视频",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        mRecordDurationTV.setText(getFormatRecordTime(time % duration));
                                    }
                                }
                            });

//                    Flowable.intervalRange(0L, 32L, 0L, 1L, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
//                            .subscribe(new Consumer<Long>() {
//                                @Override
//                                public void accept(Long aLong) throws Exception {
//                                    mCurrentTime++;
//                                    mRecordDurationTV.setText(getFormatRecordTime(mCurrentTime));
//                                    if (DEBUG) {
//                                        Log.d(TAG, "------mCurrentTime-----$mCurrentTime");
//                                    }
//                                    if (mCurrentTime == 31L) {
//                                        stopRecord();
//                                    }
//                                }
//                            });
                } else {
                    if (DEBUG) {
                        Log.d(TAG, "---recordBtn.setOnClickListener will stop record----" + isRecording);
                    }
                    stopRecord();
                }
            }
        });

        mRecordLockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    // 如果不处于录制状态，则不做任何处理
                    Toast.makeText(RecordActivity.this, "不在录制状态", Toast.LENGTH_SHORT).show();
                    return;
                }
                isLocked = !isLocked;
                mRecorderListener.lock(isLocked);
                if (isLocked) {
                    // 锁定
                    Toast.makeText(RecordActivity.this, "锁定", Toast.LENGTH_SHORT).show();
                    mRecordLockBtn.setImageResource(R.drawable.ic_lock_outline_red_a400_48dp);
                } else {
                    // 解锁
                    Toast.makeText(RecordActivity.this, "解锁", Toast.LENGTH_SHORT).show();
                    mRecordLockBtn.setImageResource(R.drawable.ic_lock_open_grey_400_48dp);
                }
            }
        });

        loadFragmentByState();
        startService(new Intent(this, RecordService.class));
        bindService(new Intent(this, RecordService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void startRecord() {
        mRecordBtn.setImageResource(R.drawable.ic_fiber_manual_record_red_600_48dp);
        mRecordDurationView.setVisibility(View.VISIBLE);
        mRecordDurationView.bringToFront();
        mRecorderListener.startRecord();
        Toast.makeText(this, "开始录像", Toast.LENGTH_SHORT).show();
        isRecording = true;
    }

    private void stopRecord() {
        isLocked = false;
        timer.dispose();
        mRecordLockBtn.setImageResource(R.drawable.ic_lock_open_grey_400_48dp);
        mRecordBtn.setImageResource(R.drawable.ic_fiber_manual_record_grey_400_48dp);
        mRecordDurationView.setVisibility(View.GONE);
        mRecorderListener.stopRecord();
        Toast.makeText(this, "停止录像", Toast.LENGTH_SHORT).show();
        isRecording = false;
    }

    private void startSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void startFileActivity() {
        startActivity(new Intent(this, FileActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        createFloatingView();
        init();
        if (DEBUG) {
            Log.d(TAG, "--------onResume------");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFloatLayout != null) {
            getWindowManager().removeView(mFloatLayout);
        }
        if (DEBUG) {
            Log.d(TAG, "--------onPause------");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) {
            Log.d(TAG, "------onDestroy------");
        }
        unbindService(mServiceConnection);
    }

    /**
     * 根据当前状态决定加载哪个Fragment
     */
    private void loadFragmentByState() {
        if (mState == STATE_RECORD) {
            if (DEBUG) {
                Log.d(TAG, "----单录----");
            }
            // 加载单录，默认为前录
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("record_fragment");
            if (fragment == null) {
                fragment = RecordFragment.newInstance(AppConfig.FRONT_CAMERA, AppConfig.FRONT_CAMERA_ID);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.previewContent, fragment, "record_fragment")
                    .commit();
        } else if (mState == STATE_DOUBLE_RECORD) {
            if (DEBUG) {
                Log.d(TAG, "----双录-----");
            }
            // 加载双录
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("double_record_fragment");
            if (fragment == null) {
                fragment = DoubleRecordFragment.newInstance(AppConfig.FRONT_CAMERA_ID,
                        AppConfig.BACK_CAMERA_ID);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.previewContent, fragment, "double_record_fragment")
                    .commit();
        }
    }

    public void addServiceBindedListener(ServiceBindedListener listener) {
        if (DEBUG) {
            Log.d(TAG, "------addServiceBindedListener size = " + mServiceBindedListeners.size());
        }
        mServiceBindedListeners.add(listener);
    }

    public void setRecorderListener(RecorderListener listener) {
        mRecorderListener = listener;
    }

    private void createFloatingView() {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        //        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = 500;
        wmParams.height = 62;

        LayoutInflater inflater = LayoutInflater.from(this);
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //添加mFloatLayout
        getWindowManager().addView(mFloatLayout, wmParams);

        mRecordBtn = (ImageButton) mFloatLayout.findViewById(R.id.recordBtn);
        mRecordLockBtn = (ImageButton) mFloatLayout.findViewById(R.id.recordLockBtn);
        mTakePictureBtn = (ImageButton) mFloatLayout.findViewById(R.id.takePictureBtn);
        mVideoFileBtn = (ImageButton) mFloatLayout.findViewById(R.id.videoFileBtn);
        mRecordSettingsBtn = (ImageButton) mFloatLayout.findViewById(R.id.recordSettingsBtn);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

    }

    public interface ServiceBindedListener {
        void onServiceBinded(RecordService recordService);
    }

    public interface RecorderListener {
        void takePicture();
        void startRecord();
        void stopRecord();
        void lock(boolean lock);
        int getDuration();
        void setNextSaveFile();
    }
}
