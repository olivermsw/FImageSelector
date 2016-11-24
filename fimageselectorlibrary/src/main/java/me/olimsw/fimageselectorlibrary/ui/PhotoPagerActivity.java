package me.olimsw.fimageselectorlibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.olimsw.fimageselectorlibrary.MPhoto;
import me.olimsw.fimageselectorlibrary.R;
import me.olimsw.fimageselectorlibrary.adapter.PhotoPagerAdapter;
import me.olimsw.fimageselectorlibrary.bean.PhotoInfo;
import me.olimsw.fimageselectorlibrary.utils.RxBus;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PhotoPagerActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "itemOnClick";
    private ViewPager vp_photo;
    private Button btn_back;
    private TextView tv_title;
    private Button btn_submit;
    private RelativeLayout rl_titlebar;
    private LinearLayout ll_select;
    private RelativeLayout rl_bottom;
    private int barHeight;
    private TranslateAnimation translateAnimation;
    private TranslateAnimation translateAnimation1;
    private AlphaAnimation alphaAnimationIn;
    private AlphaAnimation alphaAnimationOut;
    private ArrayList<PhotoInfo> photoList;
    private int position;
    private Observable photoPagerObservable;
    private Subscription photoPagersubscription;
    private boolean isShow = true;
    private ImageView iv_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pager);
        barHeight = getStatusBarHeight();
        if (getIntent().getBooleanExtra("preview", false)) {
            photoList = new ArrayList<>();
            photoList.addAll(MPhoto.getInstance().getSelectedList());
        } else {
            photoList = (ArrayList<PhotoInfo>) getIntent().getSerializableExtra("photoInfoList");
        }
        position = getIntent().getIntExtra("position", 0);
        initView();
        initEvent();
    }

    private void initEvent() {
        photoPagerObservable = RxBus.get().register(TAG, String.class);
        photoPagersubscription = photoPagerObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        if (isShow) {
                            hideBar();
                            hideBottomBar();
                        } else {
                            showBar();
                            showBottomBar();
                        }
                    }
                });
    }

    private void initView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        vp_photo = (ViewPager) findViewById(R.id.vp_photo);
        btn_back = (Button) findViewById(R.id.btn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        rl_titlebar = (RelativeLayout) findViewById(R.id.rl_titlebar);
        ll_select = (LinearLayout) findViewById(R.id.ll_select);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        iv_check = (ImageView) findViewById(R.id.iv_check_selected);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = vp_photo.getCurrentItem();
                if (MPhoto.getInstance().getSelectedList().contains(photoList.get(currentItem))) {
                    iv_check.setSelected(false);
                    MPhoto.getInstance().getSelectedList().remove(photoList.get(currentItem));
                } else {
                    iv_check.setSelected(true);
                    MPhoto.getInstance().getSelectedList().add(photoList.get(currentItem));
                }
                btn_submit.setText("完成(" + MPhoto.getInstance().getSelectedList().size() + "/" + MPhoto.getInstance().getMaxSelectCount() + ")");
            }
        });
        tv_title.setText("(" + (position + 1) + "/" + photoList.size() + ")");
        btn_submit.setText("完成(" + MPhoto.getInstance().getSelectedList().size() + "/" + MPhoto.getInstance().getMaxSelectCount() + ")");
        if (MPhoto.getInstance().getSelectedList().contains(photoList.get(position))) {
            iv_check.setSelected(true);
        } else {
            iv_check.setSelected(false);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rl_titlebar.getLayoutParams();
        layoutParams.setMargins(0, barHeight, 0, 0);
        vp_photo.setAdapter(new PhotoPagerAdapter(this, photoList));
        vp_photo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_title.setText("(" + (position + 1) + "/" + photoList.size() + ")");
                if (MPhoto.getInstance().getSelectedList().contains(photoList.get(position))) {
                    iv_check.setSelected(true);
                } else {
                    iv_check.setSelected(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp_photo.setCurrentItem(position);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_back:
//                finish();
//                break;
//            case R.id.btn_submit:
//                finish();
//                break;
//            case R.id.ll_select:
//                int currentItem = vp_photo.getCurrentItem();
//                if (MPhoto.getInstance().getSelectedList().contains(photoList.get(currentItem))) {
//                    iv_check.setSelected(false);
//                    MPhoto.getInstance().getSelectedList().remove(photoList.get(currentItem));
//                } else {
//                    iv_check.setSelected(true);
//                    MPhoto.getInstance().getSelectedList().add(photoList.get(currentItem));
//                }
//                btn_submit.setText("完成(" + MPhoto.getInstance().getSelectedList().size() + "/" + MPhoto.getInstance().getMaxSelectCount() + ")");
//                break;
//        }
    }

    private void hideBar() {
        translateAnimation = new TranslateAnimation(rl_titlebar.getX(), rl_titlebar.getX(), 0, -rl_titlebar.getHeight());
        translateAnimation.setDuration(300);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        rl_titlebar.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rl_titlebar.setVisibility(View.GONE);
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                translateAnimation1 = null;
                isShow = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        translateAnimation1 = new TranslateAnimation(rl_titlebar.getX(), rl_titlebar.getX(), -rl_titlebar.getHeight(), 0);
        translateAnimation1.setDuration(300);
        translateAnimation1.setInterpolator(new AccelerateInterpolator());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_titlebar.startAnimation(translateAnimation1);
            }
        }, 150);
        translateAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rl_titlebar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                translateAnimation1 = null;
                isShow = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void hideBottomBar() {
        alphaAnimationIn = new AlphaAnimation(1f, 0f);
        alphaAnimationIn.setDuration(300);
        alphaAnimationIn.setInterpolator(new AccelerateInterpolator());
        rl_bottom.startAnimation(alphaAnimationIn);
        alphaAnimationIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rl_bottom.setVisibility(View.GONE);
                alphaAnimationIn = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showBottomBar() {
        alphaAnimationOut = new AlphaAnimation(0f, 1f);
        alphaAnimationOut.setDuration(300);
        alphaAnimationOut.setInterpolator(new AccelerateInterpolator());
        rl_bottom.startAnimation(alphaAnimationOut);
        alphaAnimationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rl_bottom.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                alphaAnimationOut = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!photoPagersubscription.isUnsubscribed()) {
            photoPagersubscription.unsubscribe();
        }
        RxBus.get().unregister(TAG, photoPagerObservable);
    }
}
