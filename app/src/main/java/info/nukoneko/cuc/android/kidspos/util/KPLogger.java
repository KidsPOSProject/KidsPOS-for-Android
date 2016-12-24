package info.nukoneko.cuc.android.kidspos.util;

import android.os.Debug;
import android.util.Log;

import info.nukoneko.cuc.android.kidspos.BuildConfig;

/**
 * created at 2015/06/13.
 */
public class KPLogger {
    private static final String TAG = "[KidsPOS]";
    private static final String TAG2 = "[KidsPOS %s]";

    private static String st(String tag){
        return String.format(TAG2, tag);
    }

    public static void d(Object msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.valueOf(msg));
        }
    }

    public static void d(String tag, Object msg) {
        if (BuildConfig.DEBUG) {
            Log.d(st(tag), String.valueOf(msg));
        }
    }

    public static void e(Object msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, String.valueOf(msg));
        }
    }

    public static void e(String tag, Object msg) {
        if (BuildConfig.DEBUG) {
            Log.e(st(tag), String.valueOf(msg));
        }
    }

    public static void i(Object msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.valueOf(msg));
        }
    }

    public static void i(String tag, Object msg) {
        if (BuildConfig.DEBUG) {
            Log.i(st(tag), String.valueOf(msg));
        }
    }

    public static void v(Object msg) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, String.valueOf(msg));
        }
    }

    public static void v(String tag, Object msg) {
        if (BuildConfig.DEBUG) {
            Log.v(st(tag), String.valueOf(msg));
        }
    }

    public static void w(Object msg) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, String.valueOf(msg));
        }
    }

    public static void w(String tag, Object msg) {
        if (BuildConfig.DEBUG) {
            Log.w(st(tag), String.valueOf(msg));
        }
    }

    public static void heap(){
        heap(TAG);
    }

    public static void heap(String tag) {
        if (BuildConfig.DEBUG){
            Object msg = "heap : Free=" + Long.toString(Debug.getNativeHeapFreeSize() / 1024) + "kb" +
                    ", Allocated=" + Long.toString(Debug.getNativeHeapAllocatedSize() / 1024) + "kb" +
                    ", Size=" + Long.toString(Debug.getNativeHeapSize() / 1024) + "kb";

            Log.v(st(tag), String.valueOf(msg));
        }
    }
}
