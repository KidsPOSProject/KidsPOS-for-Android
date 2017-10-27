package info.nukoneko.cuc.android.kidspos.util;

public enum BarcodePrefix {
    STAFF("00"),
    ITEM("01"),
    SALE("02"),
    UNKNOWN("99");

    private final String prefix;

    BarcodePrefix(String prefix) {
        this.prefix = prefix;
    }

    public static BarcodePrefix prefixOf(String prefix) {
        for (BarcodePrefix barcodePrefix : BarcodePrefix.values()) {
            if (barcodePrefix.prefix.equals(prefix)) {
                return barcodePrefix;
            }
        }
        return UNKNOWN;
    }
}
