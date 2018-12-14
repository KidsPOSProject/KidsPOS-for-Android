package info.nukoneko.cuc.android.kidspos.event

sealed class SystemEvent: Event() {
    object SentSaleSuccess: SystemEvent()

    @Deprecated("いらないでしょ")
    object TotalPriceUpdate: SystemEvent()
}