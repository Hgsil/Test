package com.hgsil.android.gank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hgsil.android.gank.BitmapUtils.MyBitmapUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<Pictures> mPictures;
    private Pictures mPicture;
    private Context mContext;
    private boolean havePermission;
    private MyBitmapUtils mBitmapUtils;
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView kind;
        TextView createAt;
        TextView who;
        TextView publishedAt;
        ImageView picture;
        Pictures onePicture;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView)itemView.findViewById(R.id.picture_id);
            kind = (TextView)itemView.findViewById(R.id.picture_kind);
            createAt = (TextView)itemView.findViewById(R.id.picture_createAt);
            who = (TextView)itemView.findViewById(R.id.picture_who);
            publishedAt = (TextView)itemView.findViewById(R.id.picture_publishedAt);
            picture = (ImageView)itemView.findViewById(R.id.picture_picture);
            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"长按下载图片",Toast.LENGTH_SHORT).show();
                }
            });
            picture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (havePermission)
                    mBitmapUtils.downLoad(picture,onePicture.getUrl());
                    else {
                        Toast.makeText(mContext,"无权限，无法下载",Toast.LENGTH_SHORT).show();
                    }
                    return false;
                    }


            });

        }


    }

    public MyAdapter(List<Pictures> picturesList,Context context){
        mPictures = new ArrayList<>();
        mPictures.addAll(picturesList);
        mContext = context;
        mBitmapUtils = new MyBitmapUtils(mContext);


    }

    public void setHavePermission(boolean havePermission) {
        this.havePermission = havePermission;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        mPicture = mPictures.get(position);
        holder.onePicture = mPicture;
        holder.publishedAt.setText(mPicture.getPublishedAt());
        holder.createAt.setText(mPicture.getCreatedAt());
        holder.id.setText(mPicture.getId());
        holder.who.setText(mPicture.getWho());
        mBitmapUtils.disPlay(holder.picture,mPicture.getUrl());
    }


    @Override
    public int getItemCount() {
        return mPictures.size();
    }
    public void addPictures(List<Pictures> picturesList){
        mPictures.addAll(picturesList);
        notifyDataSetChanged();
    }

}
