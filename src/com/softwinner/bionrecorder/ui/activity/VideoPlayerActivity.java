package com.softwinner.bionrecorder.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.softwinner.bionrecorder.R;

// import butterknife.BindView;
// import butterknife.ButterKnife;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class VideoPlayerActivity extends AppCompatActivity{
    private static final String TAG = "VideoPlayerActivity";
    private static final boolean DEBUG = true;

    // @BindView(R.id.videoView)
    VideoView mVideoView;

    private int mCurrPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);

        // ButterKnife.bind(this);
        mVideoView = (VideoView) findViewById(R.id.videoView);

        String path = getIntent().getExtras().getString("path");
        if (path != null) {
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.setVideoPath(path);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideoView.start();
        mVideoView.seekTo(mCurrPosition);
        mCurrPosition = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mCurrPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
    }
}
