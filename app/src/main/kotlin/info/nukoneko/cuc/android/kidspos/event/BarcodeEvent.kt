package info.nukoneko.cuc.android.kidspos.event

import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff

sealed class BarcodeEvent : Event {
    data class ReadReceiptFailed(val error: Throwable) : BarcodeEvent()
    data class ReadItemSuccess(val item: Item) : BarcodeEvent()
    data class ReadItemFailed(val error: Throwable) : BarcodeEvent()
    data class ReadStaffSuccess(val staff: Staff) : BarcodeEvent()
    data class ReadStaffFailed(val error: Throwable) : BarcodeEvent()
}