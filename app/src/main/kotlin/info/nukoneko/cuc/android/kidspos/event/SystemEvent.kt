package info.nukoneko.cuc.android.kidspos.event

import info.nukoneko.cuc.android.kidspos.domain.entity.Sale
import info.nukoneko.cuc.android.kidspos.domain.entity.Store
import info.nukoneko.cuc.android.kidspos.util.Mode

sealed class SystemEvent : Event {
    data class SentSaleSuccess(val sale: info.nukoneko.cuc.android.kidspos.domain.entity.Sale?) : SystemEvent()

    data class SelectShop(val store: info.nukoneko.cuc.android.kidspos.domain.entity.Store?) : SystemEvent()

    data class ServerAddressChanged(val newServerAddress: String) : SystemEvent()

    data class RunningModeChanged(val newRunningMode: Mode) : SystemEvent()
}
