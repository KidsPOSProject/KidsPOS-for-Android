package info.nukoneko.cuc.android.kidspos.entity;

public final class Staff {
    private String barcode;
    private String name;

    @SuppressWarnings("unused")
    public Staff() {
        // for Gson
    }

    /**
     * for fake
     *
     * @param barcode barcode
     */
    public Staff(String barcode) {
        this.barcode = barcode;
        this.name = "FakeStaff";
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }
}
