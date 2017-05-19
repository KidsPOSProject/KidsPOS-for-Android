package info.nukoneko.cuc.android.kidspos.event.obj;


import info.nukoneko.cuc.android.kidspos.event.KPEvent;

public final class KPEventUpdateSumPrice implements KPEvent {
    private final int mCurrentValue;

    public KPEventUpdateSumPrice(int currentValue) {
        mCurrentValue = currentValue;
    }

    public int getCurrentValue() {
        return mCurrentValue;
    }
}
