package info.nukoneko.cuc.android.kidspos.event.obj;

import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.event.KPEvent;
import info.nukoneko.kidspos4j.model.ModelStaff;

public class KPEventUpdateStaff implements KPEvent {
    @Nullable ModelStaff staff;
    public KPEventUpdateStaff(ModelStaff staff) {
        this.staff = staff;
    }

    @Nullable
    public ModelStaff getStaff() {
        return staff;
    }
}
