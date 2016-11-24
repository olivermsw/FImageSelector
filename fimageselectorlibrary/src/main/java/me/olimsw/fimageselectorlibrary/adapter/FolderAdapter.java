package me.olimsw.fimageselectorlibrary.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.olimsw.fimageselectorlibrary.R;
import me.olimsw.fimageselectorlibrary.bean.PhotoFolderInfo;
import me.olimsw.fimageselectorlibrary.utils.RxBus;

/**
 * Created by MuSiWen on 2016/5/9.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private Activity activity;
    private List<PhotoFolderInfo> folderList = null;
    private int folderPosition = 0;

    public FolderAdapter(Activity activity) {
        this.activity = activity;
        folderList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.item_folder_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoFolderInfo photoFolderInfo = folderList.get(position);
        Glide.with(activity).load(photoFolderInfo.getPhotoList().get(0).getPhotoPath()).centerCrop().into(holder.iv_icon);
        holder.tv_title.setText(photoFolderInfo.getFolderName());
        if (folderPosition == position) {
            holder.iv_point.setVisibility(View.VISIBLE);
        } else {
            holder.iv_point.setVisibility(View.GONE);
        }
    }

    public int getFolderPosition() {
        return folderPosition;
    }

    public void setFolderPosition(int folderPosition) {
        this.folderPosition = folderPosition;
        notifyDataSetChanged();
    }

    public List<PhotoFolderInfo> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<PhotoFolderInfo> folderList) {
        this.folderList = folderList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_icon;
        private TextView tv_title;
        private ImageView iv_point;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.tv_icon);
            iv_point = (ImageView) itemView.findViewById(R.id.iv_point);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            RxBus.get().post("folderSelect",getAdapterPosition());
        }
    }
}
