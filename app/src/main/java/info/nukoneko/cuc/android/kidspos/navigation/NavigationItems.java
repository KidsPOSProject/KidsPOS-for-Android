package info.nukoneko.cuc.android.kidspos.navigation;

import android.support.annotation.StringRes;

import info.nukoneko.cuc.android.kidspos.R;

public enum NavigationItems {
//    SALES(R.string.drawer_sales),
//    ITEMS(R.string.drawer_items),
//    STAFF(R.string.drawer_employee),
    SETTING(R.string.drawer_setting);
//    UPDATE(R.string.drawer_update),
//    TEST_ADD_DUMMY(R.string.debug_drawer_add_item),
//    TEST_ADD_USER(R.string.debug_drawer_add_staff);

    public final @StringRes int val;

    NavigationItems(@StringRes int val){
        this.val = val;
    }

    public static NavigationItems getItem(int position){
        return NavigationItems.values()[position - 1];
    }
}
