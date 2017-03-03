package info.nukoneko.cuc.android.kidspos.util;


import android.support.annotation.NonNull;
import android.util.Patterns;

public final class MiscUtil {
    private MiscUtil() {}

    public static boolean isIpAddressValid(@NonNull String ipAddress) {
        return Patterns.IP_ADDRESS.matcher(ipAddress).matches();
    }

    public static boolean isPortValid(String port) {
        try {
            final int p = Integer.parseInt(port);
            return p > 1024 && 18000 > p;
        } catch (ClassCastException ex) {
            return false;
        }
    }
}
