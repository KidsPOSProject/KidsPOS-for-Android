package info.nukoneko.cuc.kidspos.event;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import info.nukoneko.cuc.kidspos.util.KPLogger;

/**
 * Created by atsumi on 2016/02/20.
 */
public class CustomBus extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());
    @Override
    public void post(Object event) {
        KPLogger.i("Post-Event!!",  event.getClass());
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(() -> post(event));
        }
    }
}
