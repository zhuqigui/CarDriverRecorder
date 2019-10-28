package com.softwinner.bionrecorder.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.common.AppConfig;
import com.softwinner.bionrecorder.util.Storage;

import java.util.Arrays;

// import butterknife.BindView;
// import butterknife.ButterKnife;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class NormalSettingsFragment extends Fragment {
    private static final String TAG = "NormalSettingsFragment";
    private static final boolean DEBUG = true;

    // @BindView(R.id.autoRun)
    Switch mAutoRun;
    // @BindView(R.id.storagePathRL)
    View mStoragePathView;
    // @BindView(R.id.storagePathTV)
    TextView mStoragePathTV;

    public NormalSettingsFragment() {}

    public static NormalSettingsFragment newInstance() {
        NormalSettingsFragment fragment = new NormalSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_settings, container, false);

        // ButterKnife.bind(this, view);
        mAutoRun = (Switch) view.findViewById(R.id.autoRun);
        mStoragePathView = view.findViewById(R.id.storagePathRL);
        mStoragePathTV = (TextView) view.findViewById(R.id.storagePathTV);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        init();
    }

    private void init() {
        final AppConfig appconfig = AppConfig.getInstance(getActivity().getApplicationContext());
        mStoragePathTV.setText(appconfig.getString(AppConfig.STORAGE_PATH,
                AppConfig.DEFAULT_STORAGE_PATH));

        mStoragePathView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] paths = Storage.getStoragePaths();
                int checkedItem = Arrays.binarySearch(paths,
                        appconfig.getString(AppConfig.STORAGE_PATH, AppConfig.DEFAULT_STORAGE_PATH));
                new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                        .setTitle(getString(R.string.settings_file_storage_path))
                        .setSingleChoiceItems(paths, checkedItem,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (DEBUG) {
                                            Log.d(TAG, "-----storage path----" + paths[which]);
                                        }
                                        appconfig.putString(AppConfig.STORAGE_PATH, paths[which]);
                                        mStoragePathTV.setText(paths[which]);
                                        dialog.dismiss();
                                    }
                                })
                        .create()
                        .show();
            }
        });
    }

}
