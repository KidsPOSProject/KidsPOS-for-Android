package info.nukoneko.cuc.android.kidspos.event

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff

sealed class BarcodeEvent<T : Any>(v: T? = null) : Event<T>(v) {
    data class ReadReceiptFailed(val error: Throwable) : BarcodeEvent<Throwable>(error)
    data class ReadItemSuccess(val item: Item) : BarcodeEvent<Item>(item)
    data class ReadItemFailed(val error: Throwable) : BarcodeEvent<Throwable>(error)
    data class ReadStaffSuccess(val staff: Staff) : BarcodeEvent<Staff>(staff)
    data class ReadStaffFailed(val error: Throwable) : BarcodeEvent<Throwable>(error)
}