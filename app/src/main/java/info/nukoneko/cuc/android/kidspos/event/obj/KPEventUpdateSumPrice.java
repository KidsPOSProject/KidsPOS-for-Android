package info.nukoneko.cuc.android.kidspos.event.obj;


import info.nukoneko.cuc.android.kidspos.event.KPEvent;

public final class KPEventUpdateSumPrice implements KPEvent{
    private final int currentValue;
    public KPEventUpdateSumPrice(int currentValue) {
        this.currentValue = currentValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }
}
