package com.softwinner.bionrecorder.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.bean.ImageInfo;
import com.softwinner.bionrecorder.bean.VideoInfo;
import com.softwinner.bionrecorder.data.ThumbWorker;
import com.softwinner.bionrecorder.ui.adapter.ImageFileAdapter;
import com.softwinner.bionrecorder.ui.adapter.VideoFileAdapter;
import com.softwinner.bionrecorder.widget.BionRecyclerView;
import com.softwinner.bionrecorder.widget.DividerGridItemDecoration;

import java.util.ArrayList;

// import butterknife.BindView;
// import butterknife.ButterKnife;

import static com.softwinner.bionrecorder.ui.fragment.VideoFileFragment.TYPE_BACK;
import static com.softwinner.bionrecorder.ui.fragment.VideoFileFragment.TYPE_FRONT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFileFragment extends Fragment implements ThumbWorker.OnLoadImageFileListener {
    private static final String TAG = "ImageFileFragment";

    private ImageFileAdapter mAdapter;

    // @BindView(R.id.pictureRV)
    BionRecyclerView mPictureRV;


    public ImageFileFragment() {}

    public static ImageFileFragment newInstance() {
        ImageFileFragment fragment = new ImageFileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_file, container, false);

        // ButterKnife.bind(this, view);
        mPictureRV = (BionRecyclerView) view.findViewById(R.id.pictureRV);

        init();

        return view;
    }

    private void init() {
        mAdapter = new ImageFileAdapter(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mPictureRV.setLayoutManager(layoutManager);
        mPictureRV.setAdapter(mAdapter);
        mPictureRV.addItemDecoration(new DividerGridItemDecoration(getActivity()));

        ThumbWorker.getImageInfos(getActivity(), this);
    }

    @Override
    public void onSuccess(ArrayList<ImageInfo> imageInfos) {
        mAdapter.updateData(imageInfos);
    }
}
