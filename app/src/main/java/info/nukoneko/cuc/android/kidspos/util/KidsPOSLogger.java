package info.nukoneko.cuc.android.kidspos.util;

import android.support.annotation.NonNull;
import android.util.Log;

public final class KidsPOSLogger {
    public static void d(@NonNull LogFilter filter, @NonNull String message, Object... objects) {
        if (!filter.isShow()) return;
        Log.d(filter.name(), String.format(message, objects));
    }

    public static void d(@NonNull LogFilter filter, @NonNull String message) {
        if (!filter.isShow()) return;
        Log.d(filter.name(), message);
    }

    public static void w(@NonNull LogFilter filter, @NonNull String message, Object... objects) {
        if (!filter.isShow()) return;
        Log.w(filter.name(), String.format(message, objects));
    }

    public static void w(@NonNull LogFilter filter, @NonNull String message) {
        if (!filter.isShow()) return;
        Log.w(filter.name(), message);
    }

    public static void e(@NonNull LogFilter filter, @NonNull Throwable throwable) {
        if (!filter.isShow()) return;
        Log.e(filter.name(), throwable.getLocalizedMessage(), throwable);
    }
}
