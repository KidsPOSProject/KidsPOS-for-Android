package info.nukoneko.cuc.kidspos.event;

/**
 * Created by atsumi on 2016/02/20.
 */
final public class EventItemAdapterChange {
    private final int sum;
    private final int itemCount;

    public EventItemAdapterChange(int sum, int itemCount){
        this.sum = sum;
        this.itemCount = itemCount;
    }

    public int getSum() {
        return sum;
    }

    public int getItemCount() {
        return itemCount;
    }
}
