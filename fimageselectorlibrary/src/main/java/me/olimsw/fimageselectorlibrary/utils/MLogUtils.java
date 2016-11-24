package me.olimsw.fimageselectorlibrary.utils;

import android.util.Log;

import me.olimsw.fimageselectorlibrary.common.MPhotoConstants;


/**
 * Created by MuSiWen on 2016/5/6.
 */
public final class MLogUtils {
    public static boolean isDebug = false;

    public static void w(String message) {
        if (isDebug)
            Log.w(MPhotoConstants.NAME, message);
    }

    public static void e(String message) {
        if (isDebug)
            Log.e(MPhotoConstants.NAME, message);
    }

    public static void d(String message) {
        if (isDebug)
            Log.d(MPhotoConstants.NAME, message);
    }

    public static void i(String message) {
        if (isDebug)
            Log.i(MPhotoConstants.NAME, message);
    }


}
