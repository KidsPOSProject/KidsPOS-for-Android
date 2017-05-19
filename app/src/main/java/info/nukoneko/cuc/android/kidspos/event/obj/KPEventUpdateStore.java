package info.nukoneko.cuc.android.kidspos.event.obj;

import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.event.KPEvent;
import info.nukoneko.cuc.kidspos4j.model.ModelStore;

public final class KPEventUpdateStore implements KPEvent {
    @Nullable
    private final ModelStore mStore;

    public KPEventUpdateStore(@Nullable ModelStore store) {
        mStore = store;
    }

    @Nullable
    public ModelStore getStore() {
        return mStore;
    }
}
