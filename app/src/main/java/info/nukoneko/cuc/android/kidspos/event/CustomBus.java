package info.nukoneko.cuc.android.kidspos.event;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class CustomBus extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());
    @Override
    public void post(Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(() -> post(event));
        }
    }
}
