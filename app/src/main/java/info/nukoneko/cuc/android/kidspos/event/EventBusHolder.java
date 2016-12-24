package info.nukoneko.cuc.android.kidspos.event;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by atsumi on 2016/01/09.
 */
public final class EventBusHolder  {
    private final Handler mainThread = new Handler(Looper.getMainLooper());
    public static final Bus EVENT_BUS = new CustomBus();
}