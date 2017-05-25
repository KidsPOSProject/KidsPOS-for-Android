package info.nukoneko.cuc.android.kidspos.event;

public final class SumPriceUpdateEvent {
    private final int mCurrentValue;

    public SumPriceUpdateEvent(int currentValue) {
        mCurrentValue = currentValue;
    }

    public int getCurrentValue() {
        return mCurrentValue;
    }
}
