package info.nukoneko.cuc.android.kidspos.entity;

import java.util.Date;

public final class Sale {
    private int id;
    private String barcode;
    private Date createdAt;
    private int points;
    private int price;
    private String items;
    private int storeId;
    private int staffId;

    public int getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getPoints() {
        return points;
    }

    public int getPrice() {
        return price;
    }

    public String getItems() {
        return items;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getStaffId() {
        return staffId;
    }
}
