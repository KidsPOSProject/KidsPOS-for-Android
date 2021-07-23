package info.nukoneko.cuc.android.kidspos.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val id: ItemId,
    val barcode: Barcode,
    val name: String,
    val price: Price,
    val storeId: StoreId
) : Parcelable
