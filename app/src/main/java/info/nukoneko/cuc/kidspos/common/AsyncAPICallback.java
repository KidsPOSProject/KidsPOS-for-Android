package info.nukoneko.cuc.kidspos.common;

/**
 * Created by atsumi on 2016/02/27.
 */
public interface AsyncAPICallback<T> {
    T doFunc(Object... params);
    void onResult(T result);
}
