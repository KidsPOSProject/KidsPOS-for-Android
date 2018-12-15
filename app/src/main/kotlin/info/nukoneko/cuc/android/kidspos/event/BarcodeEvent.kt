package info.nukoneko.cuc.android.kidspos.event

sealed class BarcodeEvent : Event() {
    object ReadReceiptFailed : BarcodeEvent()
    object ReadItemSuccess : BarcodeEvent()
    object ReadItemFailed : BarcodeEvent()
    object ReadStaffSuccess : BarcodeEvent()
    object ReadStaffFailed : BarcodeEvent()
}