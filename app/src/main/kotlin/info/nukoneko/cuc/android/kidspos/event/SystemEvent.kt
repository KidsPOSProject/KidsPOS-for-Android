package info.nukoneko.cuc.android.kidspos.event

sealed class SystemEvent : Event() {
    object SentSaleSuccess : SystemEvent()

    object SelectShop: SystemEvent()

    @Deprecated("いらないでしょ")
    object TotalPriceUpdate : SystemEvent()

    object HostChanged : SystemEvent()
}