package info.nukoneko.cuc.android.kidspos.util.logger;

public enum LogFilter {
    BARCODE(false),
    SERVER(false);

    boolean isEnabled;

    LogFilter(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
