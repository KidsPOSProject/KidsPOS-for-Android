package info.nukoneko.cuc.kidspos.event;

/**
 * Created on 2016/02/20.
 *
 * http://qiita.com/yyaammaa/items/57d8baa1e80346e67e47
 */
public class RxBusProvider {
    private static final RxBus BUS = new RxBus();

    private RxBusProvider() {
        // No instances.
    }

    public static RxBus getInstance() {
        return BUS;
    }
}
