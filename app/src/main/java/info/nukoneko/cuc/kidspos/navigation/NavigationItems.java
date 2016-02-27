package info.nukoneko.cuc.kidspos.navigation;

import android.support.annotation.StringRes;

import info.nukoneko.cuc.kidspos.R;

/**
 * Created by atsumi on 2016/02/20.
 */
public enum NavigationItems {
    SALES(R.string.drawer_sales),
    ITEMS(R.string.drawer_items),
    STAFF(R.string.drawer_employee),
    SETTING(R.string.drawer_setting),
    UPDATE(R.string.drawer_update),
    TEST_ADD_DUMMY(R.string.debug_drawer_add_item);

    public final @StringRes int val;

    NavigationItems(@StringRes int val){
        this.val = val;
    }

    public static NavigationItems getItem(int position){
        return NavigationItems.values()[position - 1];
    }
}
