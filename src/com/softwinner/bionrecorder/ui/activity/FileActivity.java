package com.softwinner.bionrecorder.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.ui.adapter.FileFragmentPagerAdapter;
import com.softwinner.bionrecorder.ui.fragment.ImageFileFragment;
import com.softwinner.bionrecorder.ui.fragment.VideoFileFragment;
import com.softwinner.bionrecorder.widget.BionIndicator;
import com.softwinner.bionrecorder.widget.VerticalViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// import butterknife.BindView;
// import butterknife.ButterKnife;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class FileActivity extends AppCompatActivity{
    private static final String TAG = "FileActivity";
    private static final boolean DEBUG = true;

    // @BindView(R.id.fileIndicator)
    BionIndicator mFileIndicator;
    // @BindView(R.id.fileViewPager)
    VerticalViewPager mFileViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        // ButterKnife.bind(this);
        mFileIndicator = (BionIndicator) findViewById(R.id.fileIndicator);
        mFileViewPager = (VerticalViewPager) findViewById(R.id.fileViewPager);

        init();
    }

    private void init() {
        List<String> mTabTitles = Arrays.asList(getResources().getStringArray(R.array.file_tab));
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_FRONT));
        mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_BACK));
        mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_LOCK));
        mFragments.add(ImageFileFragment.newInstance());
        mFileViewPager.setAdapter(new FileFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
        mFileIndicator.setTabTitles(mTabTitles);
        mFileIndicator.setViewPager(mFileViewPager, 0);
    }
}
