package info.nukoneko.cuc.android.kidspos.event.obj;

import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.event.KPEvent;

public class StaffUpdateEvent implements KPEvent {
    @Nullable
    private Staff mStaff;

    public StaffUpdateEvent(@Nullable Staff staff) {
        mStaff = staff;
    }

    @Nullable
    public Staff getStaff() {
        return mStaff;
    }
}
