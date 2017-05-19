package info.nukoneko.cuc.android.kidspos.entity;

public final class Staff {
    private String barcode;
    private String name;

    /**
     * for fake
     * @param barcode barcode
     */
    public static Staff createFake(String barcode) {
        final Staff staff = new Staff();
        staff.barcode = barcode;
        staff.name = "FakeStaff";
        return staff;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }
}
