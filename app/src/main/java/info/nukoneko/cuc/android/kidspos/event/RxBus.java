package info.nukoneko.cuc.android.kidspos.event;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created on 2016/02/20.
 *
 * http://qiita.com/yyaammaa/items/57d8baa1e80346e67e47
 */
public class RxBus {
    private final Subject<Object, Object> mBus =
            new SerializedSubject<>(PublishSubject.create());

    public RxBus() {
    }

    public void send(Object o) {
        mBus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }
}
