package info.nukoneko.cuc.android.kidspos.event;

import com.squareup.otto.Bus;

public final class EventBusHolder  {
    public static final Bus EVENT_BUS = new CustomBus();
}