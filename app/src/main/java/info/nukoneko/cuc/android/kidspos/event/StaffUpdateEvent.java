package info.nukoneko.cuc.android.kidspos.event;

import android.support.annotation.Nullable;

import info.nukoneko.cuc.android.kidspos.entity.Staff;

public class StaffUpdateEvent{
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
