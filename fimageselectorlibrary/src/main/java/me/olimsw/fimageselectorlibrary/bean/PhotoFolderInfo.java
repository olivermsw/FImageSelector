package me.olimsw.fimageselectorlibrary.bean;

import java.util.List;

/**
 * Created by MuSiWen on 2016/4/28.
 */
public class PhotoFolderInfo {
    private int folderId;
    private String folderName;
    private PhotoInfo coverPhoto;
    private List<PhotoInfo> photoList;

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public PhotoInfo getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(PhotoInfo coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public List<PhotoInfo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<PhotoInfo> photoList) {
        this.photoList = photoList;
    }

    @Override
    public String toString() {
        return "PhotoFolderInfo{" +
                "folderId=" + folderId +
                ", folderName='" + folderName + '\'' +
                ", coverPhoto=" + coverPhoto +
                ", photoList=" + photoList +
                '}';
    }
}
