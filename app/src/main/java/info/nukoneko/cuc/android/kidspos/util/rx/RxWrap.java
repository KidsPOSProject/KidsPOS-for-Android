package info.nukoneko.cuc.android.kidspos.util.rx;

import android.content.Context;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class RxWrap {
    private RxWrap(){}

    private static <T> Observable<T> createBase(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> create(Observable<T> observable) {
        return createBase(observable);
    }

    public static <T> Observable<T> create(Observable<T> observable, Observable.Transformer<T, T> bindContext) {
        return createBase(observable).compose(bindContext);
    }
}
