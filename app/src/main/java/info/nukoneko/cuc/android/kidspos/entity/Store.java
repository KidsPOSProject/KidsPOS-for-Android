package info.nukoneko.cuc.android.kidspos.entity;

import android.text.TextUtils;

public final class Store {
    private int id;
    private String name;

    @SuppressWarnings("unused")
    public Store() {
        // for Gson
    }

    public Store(String name) {
        this.id = 0;
        this.name = name;
    }

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
