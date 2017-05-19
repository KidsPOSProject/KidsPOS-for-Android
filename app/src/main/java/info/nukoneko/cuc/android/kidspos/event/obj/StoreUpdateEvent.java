package info.nukoneko.cuc.android.kidspos.event.obj;

import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.entity.Store;
import info.nukoneko.cuc.android.kidspos.event.KPEvent;

public final class StoreUpdateEvent implements KPEvent {
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
