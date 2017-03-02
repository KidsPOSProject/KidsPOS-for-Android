package info.nukoneko.cuc.android.kidspos.util.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

public class KPSettingsManager {
    private final static String KEY_SERVER_IP = "settings_server_ip";
    private final static String KEY_ENABLE_PRACTICE_MODE = "settings_enable_practice_mode";
    private final static String KEY_ENABLE_DEBUG_MODE = "settings_enable_debug_mode";

    private final Context mContext;

    public KPSettingsManager(@NonNull Context context) {
        this.mContext = context;
    }

    @NonNull
    public String getServerIP() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString(KEY_SERVER_IP, "");
    }

    public void setServerIp(@NonNull String ipAddress) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(KEY_SERVER_IP, ipAddress).apply();
    }

    public boolean isPracticeModeEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_ENABLE_PRACTICE_MODE, true);
    }

    public boolean isDebugModeEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_ENABLE_DEBUG_MODE, false);
    }
}
