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
import com.softwinner.bionrecorder.bean.ImageInfo;
import com.softwinner.bionrecorder.bean.VideoInfo;
import com.softwinner.bionrecorder.ui.activity.ImageActivity;
import com.softwinner.bionrecorder.ui.activity.VideoPlayerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class ImageFileAdapter extends RecyclerView.Adapter<ImageFileAdapter.ImageFileViewHolder>{
    private List<ImageInfo> mImageInfos = new ArrayList<>();
    private Context mContext;

    public ImageFileAdapter(Context context) {
        mContext = context;
    }

    public void updateData(List<ImageInfo> imageInfos) {
        mImageInfos = imageInfos;
        notifyDataSetChanged();
    }

    @Override
    public ImageFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image_file, null);

        return new ImageFileViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ImageFileViewHolder holder, int position) {
        final ImageInfo imageInfo = mImageInfos.get(position);

        holder.thumbIV.setImageBitmap(imageInfo.bitmap);
        holder.nameTV.setText(imageInfo.name);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("path", imageInfo.path);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mImageInfos.size();
    }

    static class ImageFileViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView thumbIV;
        TextView nameTV;

        ImageFileViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            thumbIV = (ImageView) itemView.findViewById(R.id.imageThumbIV);
            nameTV = (TextView) itemView.findViewById(R.id.imageNameTV);
        }


    }
}
