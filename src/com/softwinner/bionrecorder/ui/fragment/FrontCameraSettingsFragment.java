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
import static com.softwinner.bionrecorder.R.id.frontMute;
import static com.softwinner.bionrecorder.R.id.frontPictureQualityRL;
import static com.softwinner.bionrecorder.R.id.frontPictureQualityTV;
import static com.softwinner.bionrecorder.R.id.frontRecordDurationRL;
import static com.softwinner.bionrecorder.R.id.frontRecordDurationTV;
import static com.softwinner.bionrecorder.R.id.frontRecordQualityRL;
import static com.softwinner.bionrecorder.R.id.frontRecordQualityTV;
import static com.softwinner.bionrecorder.R.id.frontWaterMark;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrontCameraSettingsFragment extends Fragment {
    private static final String TAG = "FrontCameraSettingsFragment";
    private static final boolean DEBUG = true;

    private AppConfig mAppConfig;

    // @BindView(R.id.frontMute)
    Switch mFrontMute;
    // @BindView(R.id.frontWaterMark)
    Switch mFrontWaterMark;
    // @BindView(R.id.frontRecordQualityTV)
    TextView mFrontRecordQualityTV;
    // @BindView(R.id.frontRecordQualityRL)
    View mFrontRecordQualityView;
    // @BindView(R.id.frontPictureQualityTV)
    TextView mFrontPictureQualityTV;
    // @BindView(R.id.frontPictureQualityRL)
    View mFrontPictureQualityView;
    // @BindView(R.id.frontRecordDurationTV)
    TextView mFrontRecordDurationTV;
    // @BindView(R.id.frontRecordDurationRL)
    View mFrontRecordDurationView;


    public FrontCameraSettingsFragment() {}

    public static FrontCameraSettingsFragment newInstance() {
        FrontCameraSettingsFragment fragment = new FrontCameraSettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front_cameral_settings, container, false);

        // ButterKnife.bind(this, view);
        mFrontMute = (Switch) view.findViewById(R.id.frontMute);
        mFrontWaterMark = (Switch) view.findViewById(R.id.frontWaterMark);
        mFrontRecordQualityTV = (TextView) view.findViewById(R.id.frontRecordQualityTV);
        mFrontRecordQualityView = view.findViewById(R.id.frontRecordQualityRL);
        mFrontPictureQualityTV = (TextView) view.findViewById(R.id.frontPictureQualityTV);
        mFrontPictureQualityView = view.findViewById(R.id.frontPictureQualityRL);
        mFrontRecordDurationTV = (TextView) view.findViewById(R.id.frontRecordDurationTV);
        mFrontRecordDurationView = view.findViewById(R.id.frontRecordDurationRL);

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

        mFrontMute.setChecked(mAppConfig.getBoolean(AppConfig.FRONT_MUTE, false));
        mFrontWaterMark.setChecked(mAppConfig.getBoolean(AppConfig.FRONT_WATERMARK, false));

        mFrontPictureQualityTV.setText(AppConfig.getInstance(context)
                .getInt(AppConfig.FRONT_PICTURE_QUALITY, AppConfig.DEFAULT_PICTURE_QUALITY) + "P");

        mFrontRecordQualityTV.setText(AppConfig.getInstance(context)
                .getInt(AppConfig.FRONT_RECORD_QUALITY, AppConfig.DEFAULT_RECORD_QUALITY) + "P");

        mFrontRecordDurationTV.setText(AppConfig.getInstance(context)
                .getInt(AppConfig.FRONT_RECORD_DURATION, AppConfig.DEFAULT_RECORD_DURATION) + "min");

        mFrontMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAppConfig.putBoolean(AppConfig.FRONT_MUTE, isChecked);
            }
        });

        mFrontWaterMark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAppConfig.putBoolean(AppConfig.FRONT_WATERMARK, isChecked);
            }
        });

        mFrontPictureQualityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPictureQuality();
            }
        });

        mFrontRecordQualityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecordQuality();
            }
        });

        mFrontRecordDurationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecordDuration();
            }
        });
    }

    private void setPictureQuality() {
        final String[] selections = getResources().getStringArray(R.array.picture_quality_name);
        final int[] qualitys = getResources().getIntArray(R.array.picture_quality);
        int checkedItem = Arrays.binarySearch(qualitys, mAppConfig.getInt(AppConfig.FRONT_PICTURE_QUALITY,
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
                                mAppConfig.putInt(AppConfig.FRONT_PICTURE_QUALITY, qualitys[which]);
                                mFrontPictureQualityTV.setText(selections[which]);
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }

    private void setRecordQuality() {
        final String[] selections = getResources().getStringArray(R.array.record_quality_name);
        final int[] qualitys = getResources().getIntArray(R.array.record_quality);
        int checkedItem = Arrays.binarySearch(qualitys, mAppConfig.getInt(AppConfig.FRONT_RECORD_QUALITY,
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
                                mAppConfig.putInt(AppConfig.FRONT_RECORD_QUALITY, qualitys[which]);
                                mFrontRecordQualityTV.setText(selections[which]);
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }

    private void setRecordDuration() {
        final String[] selections = getResources().getStringArray(R.array.record_duration_name);
        final int[] qualitys = getResources().getIntArray(R.array.record_duration);
        int checkedItem = Arrays.binarySearch(qualitys, mAppConfig.getInt(AppConfig.FRONT_RECORD_DURATION,
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
                                mAppConfig.putInt(AppConfig.FRONT_RECORD_DURATION, qualitys[which]);
                                mFrontRecordDurationTV.setText(selections[which]);
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }


}
