package info.nukoneko.cuc.android.kidspos.api.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;

import info.nukoneko.cuc.android.kidspos.util.MiscUtil;

public final class SettingsManager {
    public final static String DEFAULT_IP = "192.168.0.220";
    public final static String DEFAULT_PORT = "9500";
    public final static String KEY_SERVER_IP = "settings_server_ip";
    public final static String KEY_SERVER_PORT = "settings_server_port";
    public final static String KEY_ENABLE_PRACTICE_MODE = "settings_enable_practice_mode";

    @NonNull
    private Context mContext;

    public SettingsManager(@NonNull Context context) {
        mContext = context;

        if (TextUtils.isEmpty(getServerIP()) || !MiscUtil.isIpAddressValid(getServerIP())) {
            setDefaultIpAddress();
        }
        if (TextUtils.isEmpty(getServerPort()) || !MiscUtil.isPortValid(getServerPort())) {
            setDefaultPortNumber();
        }
    }

    protected final SharedPreferences getDefaultSharedPreference() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    protected final SharedPreferences getSharedPreference(String preferenceName) {
        return mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    @NonNull
    public String getServerIP() {
        return getDefaultSharedPreference().getString(KEY_SERVER_IP, DEFAULT_IP);
    }

    private void setServerIp(@NonNull String ipAddress) {
        getDefaultSharedPreference().edit().putString(KEY_SERVER_IP, ipAddress).apply();
    }

    public String getServerPort() {
        try {
            return getDefaultSharedPreference().getString(KEY_SERVER_PORT, DEFAULT_PORT);
        } catch (ClassCastException ex) {
            setServerPort(DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    private void setServerPort(String port) {
        getDefaultSharedPreference().edit().putString(KEY_SERVER_PORT, port).apply();
    }

    public boolean isPracticeModeEnabled() {
        return getDefaultSharedPreference().getBoolean(KEY_ENABLE_PRACTICE_MODE, true);
    }

    private void setDefaultIpAddress() {
        setServerIp(DEFAULT_IP);
    }

    private void setDefaultPortNumber() {
        setServerPort(DEFAULT_PORT);
    }
}
