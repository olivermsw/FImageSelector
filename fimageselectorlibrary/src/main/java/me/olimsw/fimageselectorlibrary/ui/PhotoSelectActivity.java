package me.olimsw.fimageselectorlibrary.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import me.olimsw.fimageselectorlibrary.MPhoto;
import me.olimsw.fimageselectorlibrary.R;
import me.olimsw.fimageselectorlibrary.adapter.FolderAdapter;
import me.olimsw.fimageselectorlibrary.adapter.SelectPhotoAdapter;
import me.olimsw.fimageselectorlibrary.bean.PhotoFolderInfo;
import me.olimsw.fimageselectorlibrary.bean.PhotoInfo;
import me.olimsw.fimageselectorlibrary.common.MPhotoConstants;
import me.olimsw.fimageselectorlibrary.utils.PhotoUtils;
import me.olimsw.fimageselectorlibrary.utils.RxBus;
import me.olimsw.fimageselectorlibrary.utils.Utils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class PhotoSelectActivity extends Activity implements View.OnClickListener {

    private static final int PHOTO_REQUEST_TAKEPHOTO = 666;
    private Button btn_back;
    private TextView tv_title;
    private Button btn_submit;
    private RecyclerView rv_photo;
    private PhotoFolderInfo photoFolderInfo;
    private List<PhotoFolderInfo> folderList;
    private Subscription photoSelectSubscription;
    private SelectPhotoAdapter adapter;
    private Observable photoSelectObservable;
    private Subscription getAllPhotoFolderSubscription;
    private PopupWindow popupWindow;
    private PopupWindow folderPopupWindow;
    private RelativeLayout rl_titlebar;
    private TextView tv_date;
    private TextView tv_photofloder;
    private TextView tv_preview;
    private RecyclerView rv_folder;
    private FolderAdapter folderAdapter;
    private Observable<Integer> folderSelectObservable;
    private Subscription folderSelectSubscription;
    private RelativeLayout rl_bottombar;
    private String photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        initView();
        initListener();
        initValue();
        initFolderPopupWindow();
    }

    private void initListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(888);
                finish();
            }
        });
        rv_photo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupWindow.dismiss();
                        }
                    }, 500);
                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    popupWindow.showAsDropDown(rl_titlebar);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View childAt = rv_photo.getChildAt(0);
                GridLayoutManager layoutManager = (GridLayoutManager) rv_photo.getLayoutManager();
                int position = layoutManager.getPosition(childAt);
                String time = adapter.getTimeByPosition(position);
                tv_date.setText(time);
            }
        });
    }


    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        rv_photo = (RecyclerView) findViewById(R.id.rv_photo);
        rl_titlebar = (RelativeLayout) findViewById(R.id.rl_titlebar);
        rv_photo.setLayoutManager(new GridLayoutManager(this, 3));
        rv_photo.addItemDecoration(new SpacesItemDecoration(8));
        adapter = new SelectPhotoAdapter(this);
        rv_photo.setAdapter(adapter);
        btn_submit.setText("完成(" + MPhoto.getInstance().getSelectedList().size() + "/" + MPhoto.getInstance().getMaxSelectCount() + ")");
        initPopupWindow();
        tv_photofloder = (TextView) findViewById(R.id.tv_photofloder);
        tv_photofloder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFolderPopupWindow();
            }
        });
        tv_preview = (TextView) findViewById(R.id.tv_preview);
        tv_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MPhoto.getInstance().getSelectedList().size() != 0) {
                    Intent intent = new Intent(getApplicationContext(), PhotoPagerActivity.class);
                    intent.putExtra("preview", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "请选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rl_bottombar = (RelativeLayout) findViewById(R.id.rl_bottombar);
    }

    private void initValue() {
        getAllPhotoFolderSubscription = PhotoUtils.getAllPhotoFolder(this, new ArrayList<PhotoInfo>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<PhotoFolderInfo>>() {
                    @Override
                    public void call(List<PhotoFolderInfo> photoFolderInfos) {
                        folderList = new ArrayList<>();
                        folderList.addAll(photoFolderInfos);
                        folderAdapter = new FolderAdapter(PhotoSelectActivity.this);
                        rv_folder.setAdapter(folderAdapter);
                        folderAdapter.setFolderList(folderList);
                        photoFolderInfo = photoFolderInfos.get(0);
                        adapter.setPhotoList(photoFolderInfo.getPhotoList());
                        tv_photofloder.setText(photoFolderInfo.getFolderName());

                    }
                });
        photoSelectObservable = RxBus.get().register("PhotoSelectActivity", String.class);
        photoSelectSubscription = photoSelectObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        btn_submit.setText("完成(" + s + "/" + MPhoto.getInstance().getMaxSelectCount() + ")");
                        tv_preview.setText("预览(" + s + ")");
                    }
                });

        folderSelectObservable = RxBus.get().register("folderSelect", Integer.class);
        folderSelectSubscription = folderSelectObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        folderPopupWindow.dismiss();
                        photoFolderInfo = folderList.get(integer);
                        adapter.setPhotoList(photoFolderInfo.getPhotoList());
                        tv_photofloder.setText(photoFolderInfo.getFolderName());
                    }
                });

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            if (position % 3 == 0) {
                outRect.left = 0;
                outRect.right = space;
            } else if ((position + 1) % 3 == 0) {
                outRect.left = space;
                outRect.right = 0;
            } else {
                outRect.left = space / 2;
                outRect.right = space / 2;
            }
            outRect.bottom = space;
            outRect.top = space / 2;
        }
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_back:
//                finish();
//                break;
//            case R.id.btn_submit:
//                setResult(888);
//                finish();
//                break;
//            case R.id.tv_photofloder:
//                showFolderPopupWindow();
//                break;
//            case R.id.tv_preview:
//                if (MPhoto.getInstance().getSelectedList().size() != 0) {
//                    Intent intent = new Intent(this, PhotoPagerActivity.class);
//                    intent.putExtra("preview", true);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!photoSelectSubscription.isUnsubscribed()) {
            photoSelectSubscription.unsubscribe();
        }
        if (!getAllPhotoFolderSubscription.isUnsubscribed()) {
            getAllPhotoFolderSubscription.unsubscribe();
        }
        if (!folderSelectSubscription.isUnsubscribed()) {
            folderSelectSubscription.unsubscribe();
        }
        RxBus.get().unregister("PhotoSelectActivity", photoSelectObservable);
        RxBus.get().unregister("folderSelect", folderSelectObservable);
    }

    private void initPopupWindow() {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popup_date_layout, null);
        // 设置按钮的点击事件

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tv_date = (TextView) contentView.findViewById(R.id.tv_date);
        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(false);
        popupWindow.setTouchable(false);
    }

    private void showFolderPopupWindow() {
        if (!folderPopupWindow.isShowing()) {
            folderAdapter.setFolderPosition(folderList.indexOf(photoFolderInfo));
            folderPopupWindow.showAsDropDown(rl_bottombar);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        btn_submit.setText("完成(" + MPhoto.getInstance().getSelectedList().size() + "/" + MPhoto.getInstance().getMaxSelectCount() + ")");
        tv_preview.setText("预览(" + MPhoto.getInstance().getSelectedList().size() + ")");
    }

    private void initFolderPopupWindow() {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popup_folder_layout, null);
        // 设置按钮的点击事件

        folderPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDisplayMetrics().heightPixels - Utils.dip2px(this.getApplicationContext(), 175), true);
        rv_folder = (RecyclerView) contentView.findViewById(R.id.rv_folder);
        rv_folder.setLayoutManager(new LinearLayoutManager(this));
        folderAdapter = new FolderAdapter(this);
        rv_folder.setAdapter(folderAdapter);
        folderPopupWindow.setTouchable(true);
        folderPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        folderPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        folderPopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
    }

    public void takePhoto() {
        if (MPhoto.getInstance().getMaxSelectCount() == MPhoto.getInstance().getSelectedList().size()) {
            Toast.makeText(this, "选择图片最多不能超过" + MPhoto.getInstance().getMaxSelectCount() + "张", Toast.LENGTH_SHORT).show();
        } else {
            String photoDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MPhotoConstants.PHOTO_PATH;
            File fos = new File(photoDir);
            fos.mkdirs();
            String FileName = getPhotoFileName();
            photoFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MPhotoConstants.PHOTO_PATH + File.separator + FileName;
            Uri u = Uri.fromFile(new File(fos, FileName));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
            startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
        }
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_TAKEPHOTO) {
            if (resultCode == RESULT_OK && photoFile != null) {
                File img = new File(photoFile);
                if (img.exists()) {
                    final PhotoInfo info = new PhotoInfo();
                    info.setPhotoId(getRandom(10000, 99999));
                    info.setPhotoPath(photoFile);
                    info.setCreatTime(img.lastModified());
                    MPhoto.getInstance().getSelectedList().add(info);
                    adapter.addPhotoFirst(info);
                    btn_submit.setText("完成(" + MPhoto.getInstance().getSelectedList().size() + "/" + MPhoto.getInstance().getMaxSelectCount() + ")");
                    tv_preview.setText("预览(" + MPhoto.getInstance().getSelectedList().size() + ")");
                    MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MPhotoConstants.PHOTO_PATH}, null, null);
                    finish();
                } else {
                    Toast.makeText(this, "照片保存失败！", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "照片保存失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int getRandom(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }
}
