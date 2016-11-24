package me.olimsw.fimageselectorlibrary.bean;

import android.text.TextUtils;

import java.io.Serializable;

public class PhotoInfo implements Serializable {

    private int photoId;
    private String photoPath;
    //private String thumbPath;
    private int width;
    private int height;
    private long creatTime;

    public PhotoInfo() {
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }
    //
    //public String getThumbPath() {
    //    return thumbPath;
    //}
    //
    //public void setThumbPath(String thumbPath) {
    //    this.thumbPath = thumbPath;
    //}


    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PhotoInfo)) {
            return false;
        }
        PhotoInfo info = (PhotoInfo) o;
        if (info == null) {
            return false;
        }

        return TextUtils.equals(info.getPhotoPath(), getPhotoPath());
    }

    @Override
    public String toString() {
        return "PhotoInfo{" +
                "photoId=" + photoId +
                ", photoPath='" + photoPath + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
