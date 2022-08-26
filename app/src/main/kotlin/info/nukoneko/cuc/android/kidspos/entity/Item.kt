package info.nukoneko.cuc.android.kidspos.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Item(
    val id: Int,
    val barcode: String,
    val name: String,
    val price: Int,
    val storeId: Int,
    val genreId: Int
) : Parcelable {
    companion object {
        fun create(barcode: String): Item {
            return Item(-1, barcode, "Dummy", 100, -1, -1)
        }
    }
}
