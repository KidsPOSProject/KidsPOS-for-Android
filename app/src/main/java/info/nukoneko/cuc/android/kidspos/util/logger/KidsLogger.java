package info.nukoneko.cuc.android.kidspos.util.logger;

import com.orhanobut.logger.Logger;

public final class KidsLogger {
    public static void d(LogFilter filter, Object message) {
        if (!filter.isEnabled) return;
        Logger.t(filter.name()).d(message);
    }

    public static void d(LogFilter filter, String message, Object... args) {
        if (!filter.isEnabled) return;
        Logger.t(filter.name()).d(message, args);
    }

    public static void i(LogFilter filter, String message) {
        if (!filter.isEnabled) return;
        Logger.t(filter.name()).i(message);
    }

    public static void i(LogFilter filter, String message, Object... args) {
        if (!filter.isEnabled) return;
        Logger.t(filter.name()).i(message, args);
    }
}
