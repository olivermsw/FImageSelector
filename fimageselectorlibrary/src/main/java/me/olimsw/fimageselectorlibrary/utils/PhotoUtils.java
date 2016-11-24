package me.olimsw.fimageselectorlibrary.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.olimsw.fimageselectorlibrary.bean.PhotoFolderInfo;
import me.olimsw.fimageselectorlibrary.bean.PhotoInfo;
import me.olimsw.fimageselectorlibrary.common.MPhotoConstants;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by MuSiWen on 2016/5/6.
 */
public final class PhotoUtils {
    /**
     * @param context
     * @param selectPhotoMap
     * @return
     */

    public static Observable<List<PhotoFolderInfo>> getAllPhotoFolder(@NonNull final Context context, @NonNull final List<PhotoInfo> selectPhotoMap) {
        Observable<List<PhotoFolderInfo>> observable = Observable.create(new Observable.OnSubscribe<List<PhotoFolderInfo>>() {
            @Override
            public void call(Subscriber<? super List<PhotoFolderInfo>> subscriber) {
                List<PhotoFolderInfo> allFolderList = new ArrayList<>();
                final String[] projectionPhotos = {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.ORIENTATION,
                        MediaStore.Images.Thumbnails.DATA
                };
                final ArrayList<PhotoFolderInfo> allPhotoFolderList = new ArrayList<>();
                HashMap<Integer, PhotoFolderInfo> bucketMap = new HashMap<>();
                Cursor cursor = null;
                //所有图片
                PhotoFolderInfo allPhotoFolderInfo = new PhotoFolderInfo();
                allPhotoFolderInfo.setFolderId(0);
                allPhotoFolderInfo.setFolderName(MPhotoConstants.ALL_PHOTOS);
                allPhotoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
                allPhotoFolderList.add(0, allPhotoFolderInfo);
                List<String> selectedList = new ArrayList<>();
                List<String> filterList = new ArrayList<>();
                try {
                    cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            , projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
                    if (cursor != null) {
                        int bucketNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                        final int bucketIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                        while (cursor.moveToNext()) {
                            int bucketId = cursor.getInt(bucketIdColumn);
                            String bucketName = cursor.getString(bucketNameColumn);
                            final int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            final int imageIdColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                            final int dateTakenColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                            //int thumbImageColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                            final int imageId = cursor.getInt(imageIdColumn);
                            final String path = cursor.getString(dataColumn);
                            final Long dateTaken = cursor.getLong(dateTakenColumn);
                            //final String thumb = cursor.getString(thumbImageColumn);
//                            Date date = new Date();
//                            date.setTime(dateTaken);
//                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                            Log.w("msw", df.format(date));
                            File file = new File(path);
                            if ((filterList == null || !filterList.contains(path)) && file.exists() && file.length() > 0) {
                                final PhotoInfo photoInfo = new PhotoInfo();
                                photoInfo.setPhotoId(imageId);
                                photoInfo.setPhotoPath(path);
                                photoInfo.setCreatTime(dateTaken);
                                //photoInfo.setThumbPath(thumb);
                                if (allPhotoFolderInfo.getCoverPhoto() == null) {
                                    allPhotoFolderInfo.setCoverPhoto(photoInfo);
                                }
                                //添加到所有图片
                                allPhotoFolderInfo.getPhotoList().add(photoInfo);

                                //通过bucketId获取文件夹
                                PhotoFolderInfo photoFolderInfo = bucketMap.get(bucketId);

                                if (photoFolderInfo == null) {
                                    photoFolderInfo = new PhotoFolderInfo();
                                    photoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
                                    photoFolderInfo.setFolderId(bucketId);
                                    photoFolderInfo.setFolderName(bucketName);
                                    photoFolderInfo.setCoverPhoto(photoInfo);
                                    bucketMap.put(bucketId, photoFolderInfo);
                                    allPhotoFolderList.add(photoFolderInfo);
                                }
                                photoFolderInfo.getPhotoList().add(photoInfo);
                                if (selectedList != null && selectedList.size() > 0 && selectedList.contains(path)) {
                                    selectPhotoMap.add(photoInfo);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    MLogUtils.w(ex.toString());
                    subscriber.onError(ex);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                allFolderList.addAll(allPhotoFolderList);
                if (selectedList != null) {
                    selectedList.clear();
                }
                subscriber.onNext(allFolderList);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
        return observable;
    }


}
