package me.olimsw.fimageselectorlibrary.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.olimsw.fimageselectorlibrary.MPhoto;
import me.olimsw.fimageselectorlibrary.R;
import me.olimsw.fimageselectorlibrary.bean.PhotoInfo;
import me.olimsw.fimageselectorlibrary.ui.PhotoPagerActivity;
import me.olimsw.fimageselectorlibrary.ui.PhotoSelectActivity;
import me.olimsw.fimageselectorlibrary.utils.RxBus;

/**
 * Created by MuSiWen on 2016/5/6.
 */
public class SelectPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<PhotoInfo> photoList = null;
    private Activity context;


    public SelectPhotoAdapter(Activity context) {
        this.context = context;
        photoList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (1 == viewType)
            return new SelectPhotoViewHolder(View.inflate(context, R.layout.item_select_photo, null));
        return new TakePhotoViewHolder(View.inflate(context, R.layout.item_take_photo, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SelectPhotoViewHolder) {
            position = position - 1;
            SelectPhotoViewHolder selectPhotoViewHolder = (SelectPhotoViewHolder) holder;
            PhotoInfo photoInfo = photoList.get(position);
            Glide.with(context).load(new File(photoInfo.getPhotoPath())).centerCrop().into(selectPhotoViewHolder.iv_photo);
            if (MPhoto.getInstance().getSelectedList().contains(photoInfo)) {
                selectPhotoViewHolder.iv_select.setSelected(true);
                selectPhotoViewHolder.v_shadow.setVisibility(View.VISIBLE);
            } else {
                selectPhotoViewHolder.iv_select.setSelected(false);
                selectPhotoViewHolder.v_shadow.setVisibility(View.GONE);
            }
        } else {

        }
    }

    public String getTimeByPosition(int postition) {
        long time;
        if (postition >= 3) {
            time = photoList.get(postition + 1).getCreatTime();
        } else {
            time = photoList.get(0).getCreatTime();
        }
        Date date = new Date();
        date.setTime(time);
        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd");
        return format.format(date);
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return photoList.size() + 1;
    }

    public List<PhotoInfo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<PhotoInfo> photoList) {
        this.photoList = (ArrayList<PhotoInfo>) photoList;
        notifyDataSetChanged();
    }

    public void addPhotoFirst(PhotoInfo info) {
        this.photoList.add(0, info);
        notifyDataSetChanged();
    }

    class TakePhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TakePhotoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (context instanceof PhotoSelectActivity) {
                ((PhotoSelectActivity) context).takePhoto();
            }
        }
    }

    class SelectPhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_photo;
        private View v_shadow;
        private ImageView iv_select;
        private Button btn_select;

        public SelectPhotoViewHolder(View itemView) {
            super(itemView);
            iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
            iv_select = (ImageView) itemView.findViewById(R.id.iv_select);
            btn_select = (Button) itemView.findViewById(R.id.btn_select);
            v_shadow = itemView.findViewById(R.id.v_shadow);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoPagerActivity.class);
                    intent.putExtra("photoInfoList", photoList);
                    intent.putExtra("position", getAdapterPosition() - 1);
                    context.startActivity(intent);
                }
            });
            btn_select.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btn_select){
                PhotoInfo photoInfo = getPhotoList().get(getAdapterPosition() - 1);
                if (MPhoto.getInstance().getSelectedList().contains(photoInfo)) {
                    MPhoto.getInstance().getSelectedList().remove(photoInfo);
                    iv_select.setSelected(false);
                    v_shadow.setVisibility(View.GONE);
                } else {
                    if ((MPhoto.getInstance().getSelectedList().size() + 1) <= MPhoto.getInstance().getMaxSelectCount()) {
                        MPhoto.getInstance().getSelectedList().add(photoInfo);
                        iv_select.setSelected(true);
                        v_shadow.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(context, "你最多只能选择" + MPhoto.getInstance().getMaxSelectCount() + "张图片", Toast.LENGTH_SHORT).show();
                    }
                }
                RxBus.get().post("PhotoSelectActivity", MPhoto.getInstance().getSelectedList().size() + "");
            }else if(v.getId()==R.id.iv_photo){

            }else if(v.getId()==R.id.v_shadow){
                Intent intent = new Intent(context, PhotoPagerActivity.class);
                context.startActivity(intent);
            }
//            switch (v.getId()) {
//                case R.id.btn_select:
//                    PhotoInfo photoInfo = getPhotoList().get(getAdapterPosition() - 1);
//                    if (MPhoto.getInstance().getSelectedList().contains(photoInfo)) {
//                        MPhoto.getInstance().getSelectedList().remove(photoInfo);
//                        iv_select.setSelected(false);
//                        v_shadow.setVisibility(View.GONE);
//                    } else {
//                        if ((MPhoto.getInstance().getSelectedList().size() + 1) <= MPhoto.getInstance().getMaxSelectCount()) {
//                            MPhoto.getInstance().getSelectedList().add(photoInfo);
//                            iv_select.setSelected(true);
//                            v_shadow.setVisibility(View.VISIBLE);
//                        } else {
//                            Toast.makeText(context, "你最多只能选择" + MPhoto.getInstance().getMaxSelectCount() + "张图片", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    RxBus.get().post("PhotoSelectActivity", MPhoto.getInstance().getSelectedList().size() + "");
//
//                    break;
//                case R.id.iv_photo:
//
//
//                    break;
//                case R.id.v_shadow:
//                    Intent intent = new Intent(context, PhotoPagerActivity.class);
//                    context.startActivity(intent);
//                    break;
//            }
        }
    }
}
