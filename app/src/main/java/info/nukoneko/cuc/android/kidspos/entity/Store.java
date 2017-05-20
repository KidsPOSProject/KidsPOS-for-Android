package info.nukoneko.cuc.android.kidspos.entity;

import android.text.TextUtils;

public final class Store {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return id > -1 && !TextUtils.isEmpty(name);
    }
}
