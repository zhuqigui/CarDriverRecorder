package com.softwinner.bionrecorder.ui.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.common.AppConfig;

import java.util.Arrays;

// import butterknife.BindView;
// import butterknife.ButterKnife;

import static com.softwinner.bionrecorder.R.id.backMute;
import static com.softwinner.bionrecorder.R.id.backPictureQualityRL;
import static com.softwinner.bionrecorder.R.id.backPictureQualityTV;
import static com.softwinner.bionrecorder.R.id.backRecordDurationRL;
import static com.softwinner.bionrecorder.R.id.backRecordDurationTV;
import static com.softwinner.bionrecorder.R.id.backRecordQualityRL;
import static com.softwinner.bionrecorder.R.id.backRecordQualityTV;
import static com.softwinner.bionrecorder.R.id.backWaterMark;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackCameraSettingsFragment extends Fragment {
    private static final String TAG = "BackCameraSettingsFragment";
    private static final boolean DEBUG = true;

    private AppConfig mAppConfig;

    // @BindView(R.id.backMute)
    Switch mBackMute;
    // @BindView(R.id.backWaterMark)
    Switch mBackWaterMark;
    // @BindView(R.id.backRecordQualityTV)
    TextView mBackRecordQualityTV;
    // @BindView(R.id.backRecordQualityRL)
    View mBackRecordQualityView;
    // @BindView(R.id.backPictureQualityTV)
    TextView mBackPictureQualityTV;
    // @BindView(R.id.backPictureQualityRL)
    View mBackPictureQualityView;
    // @BindView(R.id.backRecordDurationTV)
    TextView mBackRecordDurationTV;
    // @BindView(R.id.backRecordDurationRL)
    View mBackRecordDurationView;


    public BackCameraSettingsFragment() {}

    public static BackCameraSettingsFragment newInstance() {
        BackCameraSettingsFragment fragment = new BackCameraSettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_back_cameral_settings, container, false);

        // ButterKnife.bind(this, view);
        mBackMute = (Switch) view.findViewById(R.id.backMute);
        mBackWaterMark = (Switch) view.findViewById(R.id.backWaterMark);
        mBackRecordQualityTV = (TextView) view.findViewById(R.id.backRecordQualityTV);
        mBackRecordQualityView = view.findViewById(R.id.backRecordQualityRL);
        mBackPictureQualityTV = (TextView) view.findViewById(R.id.backPictureQualityTV);
        mBackPictureQualityView = view.findViewById(R.id.backPictureQualityRL);
        mBackRecordDurationTV = (TextView) view.findViewById(R.id.backRecordDurationTV);
        mBackRecordDurationView = view.findViewById(R.id.backRecordDurationRL);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        Context context = getActivity().getApplicationContext();

        mAppConfig = AppConfig.getInstance(context);

        mBackMute.setChecked(mAppConfig.getBoolean(AppConfig.BACK_MUTE, false));
        mBackWaterMark.setChecked(mAppConfig.getBoolean(AppConfig.BACK_WATERMARK, false));

        mBackPictureQualityTV.setText(AppConfig.getInstance(context)
                .getInt(AppConfig.BACK_PICTURE_QUALITY, AppConfig.DEFAULT_PICTURE_QUALITY) + "P");

        mBackRecordQualityTV.setText(AppConfig.getInstance(context)
                .getInt(AppConfig.BACK_RECORD_QUALITY, AppConfig.DEFAULT_RECORD_QUALITY) + "P");

        mBackRecordDurationTV.setText(AppConfig.getInstance(context)
                .getInt(AppConfig.BACK_RECORD_DURATION, AppConfig.DEFAULT_RECORD_DURATION) + "min");

        mBackMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAppConfig.putBoolean(AppConfig.BACK_MUTE, isChecked);
            }
        });

        mBackWaterMark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAppConfig.putBoolean(AppConfig.BACK_WATERMARK, isChecked);
            }
        });

        mBackPictureQualityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPictureQuality();
            }
        });

        mBackRecordQualityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecordQuality();
            }
        });

        mBackRecordDurationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecordDuration();
            }
        });
    }

    private void setPictureQuality() {
        final String[] selections = getResources().getStringArray(R.array.picture_quality_name);
        final int[] qualitys = getResources().getIntArray(R.array.picture_quality);
        int checkedItem = Arrays.binarySearch(qualitys, mAppConfig.getInt(AppConfig.BACK_PICTURE_QUALITY,
                AppConfig.DEFAULT_PICTURE_QUALITY));

        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(getString(R.string.picture_quality))
                .setSingleChoiceItems(selections, checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (DEBUG) {
                                    Log.d(TAG, "-----picture_quality----" + qualitys[which]);
                                }
                                mAppConfig.putInt(AppConfig.BACK_PICTURE_QUALITY, qualitys[which]);
                                mBackPictureQualityTV.setText(selections[which]);
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }

    private void setRecordQuality() {
        final String[] selections = getResources().getStringArray(R.array.record_quality_name);
        final int[] qualitys = getResources().getIntArray(R.array.record_quality);
        int checkedItem = Arrays.binarySearch(qualitys, mAppConfig.getInt(AppConfig.BACK_RECORD_QUALITY,
                AppConfig.DEFAULT_RECORD_QUALITY));

        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(getString(R.string.record_quality))
                .setSingleChoiceItems(selections, checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (DEBUG) {
                                    Log.d(TAG, "-----record_quality----" + qualitys[which]);
                                }
                                mAppConfig.putInt(AppConfig.BACK_RECORD_QUALITY, qualitys[which]);
                                mBackRecordQualityTV.setText(selections[which]);
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }

    private void setRecordDuration() {
        final String[] selections = getResources().getStringArray(R.array.record_duration_name);
        final int[] qualitys = getResources().getIntArray(R.array.record_duration);
        int checkedItem = Arrays.binarySearch(qualitys, mAppConfig.getInt(AppConfig.BACK_RECORD_DURATION,
                AppConfig.DEFAULT_RECORD_DURATION));

        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(getString(R.string.record_duration))
                .setSingleChoiceItems(selections, checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (DEBUG) {
                                    Log.d(TAG, "-----record_duration----" + qualitys[which]);
                                }
                                mAppConfig.putInt(AppConfig.BACK_RECORD_DURATION, qualitys[which]);
                                mBackRecordDurationTV.setText(selections[which]);
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }


}
