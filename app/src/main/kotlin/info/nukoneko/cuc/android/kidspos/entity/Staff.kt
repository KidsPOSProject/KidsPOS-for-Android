package info.nukoneko.cuc.android.kidspos.entity

data class Staff(val barcode: String, val name: String) {
    companion object {
        fun create(barcode: String): Staff {
            return Staff(barcode, "Dummy")
        }
    }
}