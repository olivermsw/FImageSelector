package me.olimsw.fimageselectorlibrary.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.olimsw.fimageselectorlibrary.R;
import me.olimsw.fimageselectorlibrary.bean.PhotoInfo;
import me.olimsw.fimageselectorlibrary.ui.PhotoPagerActivity;
import me.olimsw.fimageselectorlibrary.utils.RxBus;

/**
 * Created by MuSiWen on 2016/5/9.
 */
public class PhotoPagerAdapter extends PagerAdapter {
    private Activity activity;
    private List<PhotoInfo> photoList = null;

    public PhotoPagerAdapter(Activity activity, List<PhotoInfo> photoList) {
        this.activity = activity;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(activity, R.layout.item_photo_detal, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_photo);
        Glide.with(activity).load(photoList.get(position).getPhotoPath()).fitCenter().into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.get().post(PhotoPagerActivity.TAG, "");
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
