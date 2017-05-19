package info.nukoneko.cuc.android.kidspos.util.rx;

import android.app.ProgressDialog;

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

    public static <T> Observable<T> create(Observable<T> observable,
                                           ProgressDialog progressDialog,
                                           Observable.Transformer<T, T> objectTransformer) {
        return createBase(observable.compose(objectTransformer))
                .doOnSubscribe(progressDialog::show)
                .doOnCompleted(progressDialog::dismiss);
    }

    public static <T> Observable<T> create(Observable<T> observable, Observable.Transformer<T, T> objectTransformer) {
        return observable
                .compose(objectTransformer)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
