package info.nukoneko.cuc.android.kidspos.entity;

import android.os.Parcel;
import android.os.Parcelable;

public final class Item implements Parcelable {
    private int id;
    private String barcode;
    private String name;
    private int price;
    private int storeId;
    private int genreId;

    @SuppressWarnings("unused")
    public Item() {
        // for Gson
    }

    /**
     * for fake
     *
     * @param barcode barcode
     */
    public Item(String barcode) {
        this.barcode = barcode;
        this.name = "FakeItem";
        this.price = 300;
    }

    public int getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getGenreId() {
        return genreId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(barcode);
        parcel.writeString(name);
        parcel.writeInt(price);
        parcel.writeInt(storeId);
        parcel.writeInt(genreId);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        id = in.readInt();
        barcode = in.readString();
        name = in.readString();
        price = in.readInt();
        storeId = in.readInt();
        genreId = in.readInt();
    }
}
