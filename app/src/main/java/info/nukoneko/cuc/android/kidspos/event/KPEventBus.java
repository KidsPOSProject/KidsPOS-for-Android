package info.nukoneko.cuc.android.kidspos.event;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public final class KPEventBus {
    private final Subject<KPEvent, KPEvent> mBus =
            new SerializedSubject<>(PublishSubject.create());

    KPEventBus() {

    }

    public void send(KPEvent event) {
        mBus.onNext(event);
    }

    public Observable<KPEvent> toObservable() {
        return mBus;
    }
}
