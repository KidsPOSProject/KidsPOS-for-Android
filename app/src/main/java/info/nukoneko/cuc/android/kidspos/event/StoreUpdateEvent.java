package info.nukoneko.cuc.android.kidspos.event;

import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.entity.Store;

public final class StoreUpdateEvent {
    @Nullable
    private final Store mStore;

    public StoreUpdateEvent(@Nullable Store store) {
        mStore = store;
    }

    @Nullable
    public Store getStore() {
        return mStore;
    }
}
