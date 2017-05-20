package info.nukoneko.cuc.android.kidspos.util;

public enum LogFilter {
    SERVER(true),
    BARCODE(true),
    EVENT(true);

    private final boolean mShow;
    LogFilter(boolean show) {
        mShow = show;
    }

    public boolean isShow() {
        return mShow;
    }
}
