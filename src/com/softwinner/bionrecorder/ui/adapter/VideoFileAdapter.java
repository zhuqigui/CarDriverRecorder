package com.softwinner.bionrecorder.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwinner.bionrecorder.R;
import com.softwinner.bionrecorder.bean.VideoInfo;
import com.softwinner.bionrecorder.ui.activity.VideoPlayerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class VideoFileAdapter extends RecyclerView.Adapter<VideoFileAdapter.VideoFileViewHolder>{
    private List<VideoInfo> mVideoInfos = new ArrayList<>();
    private Context mContext;

    public VideoFileAdapter(Context context) {
        mContext = context;
    }

    public void updateData(List<VideoInfo> videoInfos) {
        mVideoInfos = videoInfos;
        notifyDataSetChanged();
    }

    @Override
    public VideoFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_file, null);

        return new VideoFileViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VideoFileViewHolder holder, int position) {
        final VideoInfo videoInfo = mVideoInfos.get(position);

        holder.thumbIV.setImageBitmap(videoInfo.bitmap);
        holder.durationTV.setText(String.valueOf(videoInfo.duration));
        holder.nameTV.setText(videoInfo.name);
        holder.sizeTV.setText(String.valueOf(videoInfo.size));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("path", videoInfo.path);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mVideoInfos.size();
    }

    static class VideoFileViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView thumbIV;
        TextView nameTV;
        TextView sizeTV;
        TextView durationTV;

        VideoFileViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            thumbIV = (ImageView) itemView.findViewById(R.id.videoThumbIV);
            nameTV = (TextView) itemView.findViewById(R.id.videoNameTV);
            sizeTV = (TextView) itemView.findViewById(R.id.videoSizeTV);
            durationTV = (TextView) itemView.findViewById(R.id.videoDurationTV);
        }


    }
}
