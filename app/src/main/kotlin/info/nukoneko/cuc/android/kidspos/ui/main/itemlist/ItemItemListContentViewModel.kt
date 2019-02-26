package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import info.nukoneko.cuc.android.kidspos.entity.Item

class ItemItemListContentViewModel(data: Item) : BaseObservable() {
    var data = data
        set(value) {
            field = value
            notifyChange()
        }
    @Bindable
    val name = data.name
    @Bindable
    val priceText = data.price.toString()
    @Bindable
    val barcode = data.barcode
}