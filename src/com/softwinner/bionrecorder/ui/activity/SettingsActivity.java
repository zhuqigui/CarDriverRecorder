package com.softwinner.bionrecorder.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.ui.adapter.SettingsFragmentPagerAdapter;
import com.softwinner.bionrecorder.ui.fragment.BackCameraSettingsFragment;
import com.softwinner.bionrecorder.ui.fragment.FrontCameraSettingsFragment;
import com.softwinner.bionrecorder.ui.fragment.NormalSettingsFragment;
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

public class SettingsActivity extends AppCompatActivity{
    private static final String TAG = "SettingsActivity";
    private static final boolean DEBUG = true;

    // @BindView(R.id.settingsIndicator)
    BionIndicator mSettingsIndicator;
    // @BindView(R.id.settingsViewPager)
    VerticalViewPager mSettingsViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // ButterKnife.bind(this);
        mSettingsIndicator = (BionIndicator) findViewById(R.id.settingsIndicator);
        mSettingsViewPager = (VerticalViewPager) findViewById(R.id.settingsViewPager);

        init();
    }

    private void init() {
        List<String> mTabTitles = Arrays.asList(getResources().getStringArray(R.array.settings_tab));
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(NormalSettingsFragment.newInstance());
        mFragments.add(FrontCameraSettingsFragment.newInstance());
        mFragments.add(BackCameraSettingsFragment.newInstance());
        mSettingsViewPager.setAdapter( new SettingsFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
        mSettingsIndicator.setTabTitles(mTabTitles);
        mSettingsIndicator.setViewPager(mSettingsViewPager, 0);
    }
}
