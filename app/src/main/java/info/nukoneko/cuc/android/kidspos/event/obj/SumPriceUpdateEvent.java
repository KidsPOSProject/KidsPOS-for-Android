package info.nukoneko.cuc.android.kidspos.event.obj;


import info.nukoneko.cuc.android.kidspos.event.KPEvent;

public final class SumPriceUpdateEvent implements KPEvent {
    private final int mCurrentValue;

    public SumPriceUpdateEvent(int currentValue) {
        mCurrentValue = currentValue;
    }

    public int getCurrentValue() {
        return mCurrentValue;
    }
}
