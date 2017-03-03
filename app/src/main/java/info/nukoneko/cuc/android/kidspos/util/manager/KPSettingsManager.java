package info.nukoneko.cuc.android.kidspos.util.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

public class KPSettingsManager {
    public final static String DEFAULT_IP = "192.168.0.220";
    public final static String DEFAULT_PORT = "9500";

    public final static String KEY_SERVER_IP = "settings_server_ip";
    public final static String KEY_SERVER_PORT = "settings_server_port";
    public final static String KEY_ENABLE_PRACTICE_MODE = "settings_enable_practice_mode";

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

    public String getServerPort() {
        try {
            return PreferenceManager.getDefaultSharedPreferences(mContext).getString(KEY_SERVER_PORT, "");
        } catch (ClassCastException ex) {
            setServerPort(DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    public void setServerPort(String port) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(KEY_SERVER_PORT, port).apply();
    }

    public boolean isPracticeModeEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_ENABLE_PRACTICE_MODE, true);
    }

    public void backToDefaultIpSetting() {
        setServerIp(DEFAULT_IP);
    }

    public void backToDefaultPortSetting() {
        setServerPort(DEFAULT_PORT);
    }

    public void backToDefaultIpPortSettings() {
        setServerIp(DEFAULT_IP);
        setServerPort(DEFAULT_PORT);
    }
}
