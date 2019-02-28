package info.nukoneko.cuc.android.kidspos.event

import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.util.Mode

sealed class SystemEvent<T : Any>(v: T? = null) : Event<T>(v) {
    data class SentSaleSuccess(val sale: Sale?) : SystemEvent<Sale>(sale)

    data class SelectShop(val store: Store?) : SystemEvent<Store>(store)

    @Deprecated("いらないでしょ")
    object TotalPriceUpdate : SystemEvent<Any>()

    data class ServerAddressChanged(val newServerAddress: String) : SystemEvent<String>(newServerAddress)

    data class RunningModeChanged(val newRunningMode: Mode) : SystemEvent<Mode>(newRunningMode)
}