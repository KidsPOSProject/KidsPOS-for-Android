package info.nukoneko.cuc.kidspos.event;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import info.nukoneko.cuc.kidspos.util.KPLogger;

/**
 * Created by atsumi on 2016/01/09.
 */
public final class EventBusHolder  {
    private final Handler mainThread = new Handler(Looper.getMainLooper());
    public static final Bus EVENT_BUS = new CustomBus();
}