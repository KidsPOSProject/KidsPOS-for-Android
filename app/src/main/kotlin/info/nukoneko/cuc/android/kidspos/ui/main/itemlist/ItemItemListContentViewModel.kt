package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import androidx.databinding.BaseObservable
import info.nukoneko.cuc.android.kidspos.entity.Item

class ItemItemListContentViewModel(data: Item) : BaseObservable() {
    var data = data
        set(value) {
            field = value
            notifyChange()
        }
    val name = data.name
    val priceText = data.price.toString()
    val barcode = data.barcode
}