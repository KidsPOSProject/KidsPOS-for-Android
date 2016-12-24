package info.nukoneko.cuc.android.kidspos.event;

/**
 * Created by atsumi on 2016/02/20.
 */
final public class EventAccountFinish {
    final private Object dmy;
    public EventAccountFinish(Object dmy){
        this.dmy = dmy;
    }

    public Object getDmy() {
        return dmy;
    }
}
