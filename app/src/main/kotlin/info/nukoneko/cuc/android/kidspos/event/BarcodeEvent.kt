package info.nukoneko.cuc.android.kidspos.event

import info.nukoneko.cuc.android.kidspos.domain.entity.Item
import info.nukoneko.cuc.android.kidspos.domain.entity.Sale
import info.nukoneko.cuc.android.kidspos.domain.entity.Staff

sealed class BarcodeEvent : Event {
    data class ReadReceiptSuccess(val receipt: Sale) : BarcodeEvent()
    data class ReadReceiptFailed(val error: Throwable) : BarcodeEvent()
    data class ReadItemSuccess(val item: Item) : BarcodeEvent()
    data class ReadItemFailed(val error: Throwable) : BarcodeEvent()
    data class ReadStaffSuccess(val staff: Staff) : BarcodeEvent()
    data class ReadStaffFailed(val error: Throwable) : BarcodeEvent()
}
