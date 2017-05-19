package info.nukoneko.cuc.android.kidspos.event.obj;

import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.event.KPEvent;
import info.nukoneko.cuc.kidspos4j.model.ModelStaff;

public class KPEventUpdateStaff implements KPEvent {
    @Nullable
    private ModelStaff mStaff;

    public KPEventUpdateStaff(@Nullable ModelStaff staff) {
        mStaff = staff;
    }

    @Nullable
    public ModelStaff getStaff() {
        return mStaff;
    }
}
