package info.nukoneko.cuc.android.kidspos.event;

public final class KPEventBusProvider {
    private static final KPEventBus BUS = new KPEventBus();

    private KPEventBusProvider() {}

    public static KPEventBus getInstance() {
        return BUS;
    }
}
