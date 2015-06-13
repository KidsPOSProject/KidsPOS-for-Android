package info.nukoneko.cuc.kidspos;

import android.app.Application;
import android.content.Context;

import info.nukoneko.cuc.kidspos.util.KPLogger;

/**
 * created at 2015/06/13.
 */
public class AppController extends Application {
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException (thread, e);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized AppController get(){
        return mInstance;
    }

    public void handleUncaughtException (Thread thread, Throwable e)
    {
        KPLogger.d(e.getLocalizedMessage());
        System.exit(1);
    }
}
