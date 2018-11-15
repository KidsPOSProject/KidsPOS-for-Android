package info.nukoneko.cuc.android.kidspos.entity

data class Staff(val barcode: String, val name: String) {
    companion object {
        fun createTestObject(barcode: String): Staff {
            return Staff(barcode, "DummyITem")
        }
    }
}