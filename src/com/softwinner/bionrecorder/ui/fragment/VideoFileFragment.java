package com.softwinner.bionrecorder.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.bean.ImageInfo;
import com.softwinner.bionrecorder.bean.VideoInfo;
import com.softwinner.bionrecorder.data.ThumbWorker;
import com.softwinner.bionrecorder.ui.adapter.VideoFileAdapter;
import com.softwinner.bionrecorder.widget.BionRecyclerView;
import com.softwinner.bionrecorder.widget.DividerGridItemDecoration;

import java.util.ArrayList;

// import butterknife.BindView;
// import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFileFragment extends Fragment implements ThumbWorker.OnLoadVideoFileListener {
    private static final String TAG = "VideoFileFragment";
    private static final String ARG_TYPE = "file_type";
    public static final String TYPE_FRONT = "type_front";
    public static final String TYPE_BACK = "type_back";
    public static final String TYPE_LOCK = "type_lock";

    private String mType = TYPE_FRONT;
    private VideoFileAdapter mAdapter;

    // @BindView(R.id.videoRV)
    BionRecyclerView mVideoRV;


    public VideoFileFragment() {}

    public static VideoFileFragment newInstance(String type) {
        VideoFileFragment fragment = new VideoFileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_file, container, false);

        // ButterKnife.bind(this, view);
        mVideoRV = (BionRecyclerView) view.findViewById(R.id.videoRV);

        init();

        return view;
    }

    private void init() {
        mAdapter = new VideoFileAdapter(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mVideoRV.setLayoutManager(layoutManager);
        mVideoRV.setAdapter(mAdapter);
        mVideoRV.addItemDecoration(new DividerGridItemDecoration(getActivity()));

        if (mType.equals(TYPE_FRONT)) {
            ThumbWorker.getFrontVideoInfos(getActivity(), this);
        } else if (mType.equals(TYPE_BACK)) {
            ThumbWorker.getBackVideoInfos(getActivity(), this);
        } else if (mType.equals(TYPE_LOCK)) {
            ThumbWorker.getLockVideoInfos(getActivity(), this);
        }
    }

    @Override
    public void onSuccess(ArrayList<VideoInfo> videoInfos) {
        mAdapter.updateData(videoInfos);
    }
}
