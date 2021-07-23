package info.nukoneko.cuc.android.kidspos.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.nukoneko.cuc.android.kidspos.BuildConfig
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.repository.ItemRepository
import info.nukoneko.cuc.android.kidspos.domain.repository.SaleRepository
import info.nukoneko.cuc.android.kidspos.domain.repository.StaffRepository
import info.nukoneko.cuc.android.kidspos.domain.repository.StatusRepository
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

sealed class ConnectionStatus {
    object NoConnection : ConnectionStatus()
    object TryConnecting : ConnectionStatus()
    object Connected : ConnectionStatus()
    data class ConnectionFailed(val error: Throwable) : ConnectionStatus()
}

class MainViewModel(
    private val itemRepository: ItemRepository,
    private val staffRepository: StaffRepository,
    private val saleRepository: SaleRepository,
    private val statusRepository: StatusRepository,
    private val config: GlobalConfig,
    private val eventBus: EventBus
) : ViewModel() {
    var listener: Listener? = null

    private var connectionStatus: ConnectionStatus = ConnectionStatus.NoConnection
        set(value) {
            field = value
            when (value) {
                is ConnectionStatus.NoConnection -> {
                    // nothing
                }
                is ConnectionStatus.TryConnecting -> {
                    safetyShowMessage("接続中です...")
                }
                is ConnectionStatus.Connected -> {
                    safetyShowMessage("接続しました")
                }
                is ConnectionStatus.ConnectionFailed -> {
                    safetyShowMessage("接続に失敗しました. ${value.error.localizedMessage}")
                    listener?.onNotReachableServer()
                }
            }
        }

    fun onBarcodeInput(barcode: String, prefix: BarcodeKind) {
        when (prefix) {
            BarcodeKind.ITEM -> {
                val errorHandler = CoroutineExceptionHandler { _, error ->
                    eventBus.post(BarcodeEvent.ReadItemFailed(error))
                }
                viewModelScope.launch(Dispatchers.IO + errorHandler) {
                    eventBus.post(
                        BarcodeEvent.ReadItemSuccess(
                            itemRepository.fetchItem(
                                Barcode(
                                    barcode
                                )
                            )
                        )
                    )
                }
            }
            BarcodeKind.STAFF -> {
                val errorHandler = CoroutineExceptionHandler { _, error ->
                    eventBus.post(BarcodeEvent.ReadStaffFailed(error))
                }
                viewModelScope.launch(Dispatchers.IO + errorHandler) {
                    eventBus.post(
                        BarcodeEvent.ReadStaffSuccess(
                            staffRepository.fetchStaff(
                                Barcode(barcode)
                            )
                        )
                    )
                }
            }
            BarcodeKind.SALE -> {
                val errorHandler = CoroutineExceptionHandler { _, error ->
                    eventBus.post(BarcodeEvent.ReadReceiptFailed(error))
                }
                viewModelScope.launch(Dispatchers.IO + errorHandler) {
                    eventBus.post(
                        BarcodeEvent.ReadReceiptSuccess(
                            saleRepository.fetchSale(
                                Barcode(barcode)
                            )
                        )
                    )
                }
            }
            BarcodeKind.UNKNOWN -> {
                // nothing
            }
        }
    }

    fun onStart() {
        eventBus.register(this)
    }

    fun onStop() {
        eventBus.unregister(this)
    }

    fun onResume() {
        updateTitle()

        if (config.currentRunningMode == Mode.PRODUCTION) {
            if (connectionStatus is ConnectionStatus.TryConnecting) {
                return
            }
            connectionStatus = ConnectionStatus.TryConnecting
            val errorHandler = CoroutineExceptionHandler { _, error ->
                ConnectionStatus.ConnectionFailed(error)
            }
            viewModelScope.launch(Dispatchers.IO + errorHandler) {
                statusRepository.check()
                ConnectionStatus.Connected
            }
        }
    }

    private fun safetyShowMessage(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            listener?.onShouldShowMessage(message)
        }
    }

    private fun updateTitle() {
        val titleValues = mutableListOf<String>()
        config.currentStore?.name?.apply { titleValues.add("[$this]") }
        titleValues.add("[${config.currentRunningMode.modeName}モード]")
        if (BuildConfig.DEMO_MODE) {
            titleValues.add("[デモモード]")
        }
        listener?.onShouldChangeTitleSuffix(titleValues.joinToString(" "))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectShopEvent(@Suppress("UNUSED_PARAMETER") event: SystemEvent.SelectShop) {
        updateTitle()
    }

    interface Listener {
        fun onShouldChangeTitleSuffix(titleSuffix: String)

        fun onNotReachableServer()

        fun onShouldShowMessage(message: String)
    }
}
