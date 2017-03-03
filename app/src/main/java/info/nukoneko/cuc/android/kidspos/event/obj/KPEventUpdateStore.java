package info.nukoneko.cuc.android.kidspos.event.obj;

import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.event.KPEvent;
import info.nukoneko.kidspos4j.model.ModelStore;

public final class KPEventUpdateStore implements KPEvent {
    @Nullable private final ModelStore store;
    public KPEventUpdateStore(@Nullable ModelStore store) {
        this.store = store;
    }

    @Nullable
    public ModelStore getStore() {
        return store;
    }
}
