package info.nukoneko.cuc.android.kidspos.util;


import android.support.annotation.NonNull;

import info.nukoneko.kidspos4j.model.ModelItem;

public final class KPPracticeTool {
    public static ModelItem findModelItem(@NonNull String barcode) {
        final ModelItem item = new ModelItem();
        item.setName("しょうひん");
        item.setPrice(300);
        item.setBarcode(barcode);
        return item;
    }
}
