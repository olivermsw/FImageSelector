package me.olimsw.fimageselectorlibrary;


import java.util.ArrayList;
import java.util.List;

import me.olimsw.fimageselectorlibrary.bean.PhotoInfo;

/**
 * Created by MuSiWen on 2016/5/6.
 */
public class MPhoto {
    public static MPhoto mPhoto = null;

    private List<PhotoInfo> selectedList;
    private int maxSelectCount = 9;

    public static MPhoto getInstance() {
        if (null == mPhoto)
            mPhoto = new MPhoto();
        return mPhoto;
    }

    public MPhoto() {
        selectedList = new ArrayList<>();
    }

    public List<PhotoInfo> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<PhotoInfo> selectedList) {
        this.selectedList = selectedList;
    }

    public int getMaxSelectCount() {
        return maxSelectCount;
    }

    public void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    public void clear() {
        selectedList.clear();
    }

    public void removePhotoInfo(String path) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getPhotoPath().equals(path)) {
                selectedList.remove(i);
                return;
            }
        }
    }
}
