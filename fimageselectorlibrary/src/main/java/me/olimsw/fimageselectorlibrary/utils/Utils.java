package me.olimsw.fimageselectorlibrary.utils;

import android.content.Context;

/**
 * Created by MuSiWen on 2016/5/10.
 */
public final class Utils {
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
