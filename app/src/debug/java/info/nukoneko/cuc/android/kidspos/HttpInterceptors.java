package info.nukoneko.cuc.android.kidspos;

import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

public final class HttpInterceptors {
    public static Interceptor getInterceptor() {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
}
