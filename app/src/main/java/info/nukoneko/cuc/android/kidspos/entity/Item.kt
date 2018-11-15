package info.nukoneko.cuc.android.kidspos.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(val id: Int, val barcode: String, val name: String, val price: Int, val storeId: Int, val genreId: Int) : Parcelable {
    companion object {
        fun createTestObject(barcode: String): Item {
            return Item(-1, barcode, "DummyITem", 100, -1, -1)
        }
    }
}
