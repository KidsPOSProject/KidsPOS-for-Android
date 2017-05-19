package info.nukoneko.cuc.android.kidspos.util;

import android.support.annotation.NonNull;

import info.nukoneko.cuc.kidspos4j.model.ModelItem;
import info.nukoneko.cuc.kidspos4j.model.ModelStaff;

public final class DummyModelCreator {
    public static ModelItem getFakeItem(@NonNull String barcode) {
        final ModelItem item = new ModelItem();
        item.setName("しょうひん");
        item.setPrice(300);
        item.setBarcode(barcode);
        return item;
    }

    public static ModelStaff getFakeStaff(@NonNull String barcode) {
        final ModelStaff staff = new ModelStaff();
        staff.setName("スタッフ");
        staff.setBarcode(barcode);
        return staff;
    }
}
