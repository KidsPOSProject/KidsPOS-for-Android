package info.nukoneko.cuc.android.kidspos.ui.main

import androidx.lifecycle.ViewModel
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val api: APIService,
                    private val config: GlobalConfig,
                    private val eventBus: EventBus) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    var listener: Listener? = null

    enum class ConnectionStatus {
        CONNECTING, CONNECTED, NOT_CONNECTED
    }

    private var status: ConnectionStatus = ConnectionStatus.NOT_CONNECTED

    fun onBarcodeInput(barcode: String, prefix: BarcodeKind) {
        @Suppress("ConstantConditionIf")
        if (ProjectSettings.DEMO_MODE) {
            when (prefix) {
                BarcodeKind.ITEM -> {
                    onReadItemSuccess(Item.create(barcode))
                }
                BarcodeKind.STAFF -> {
                    onReadStaffSuccess(Staff.create(barcode))
                }
                else -> {
                    onReadItemSuccess(Item.create(barcode))
                    onReadStaffSuccess(Staff.create(barcode))
                }
            }
        } else {
            // サーバから取得する
            when (prefix) {
                BarcodeKind.ITEM -> {
                    launch {
                        try {
                            val item = requestGetItem(barcode)
                            onReadItemSuccess(item)
                        } catch (e: Throwable) {
                            onReadItemFailure(e)
                        }
                    }
                }
                BarcodeKind.STAFF -> {
                    launch {
                        try {
                            val staff = requestGetStaff(barcode)
                            onReadStaffSuccess(staff)
                        } catch (e: Throwable) {
                            onReadStaffFailure(e)
                        }
                    }
                }
                BarcodeKind.SALE -> eventBus.post(BarcodeEvent.ReadReceiptFailed(IOException("まだ対応していない")))
                BarcodeKind.UNKNOWN -> {
                }
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
            if (status != ConnectionStatus.NOT_CONNECTED) {
                return
            }
            safetyShowMessage("接続中です...")
            status = ConnectionStatus.CONNECTING
            launch(Dispatchers.IO) {
                try {
                    api.getStatus().await()
                    status = ConnectionStatus.CONNECTED
                    safetyShowMessage("接続しました")
                } catch (e: Throwable) {
                    launch(Dispatchers.Main) {
                        listener?.onNotReachableServer()
                        status = ConnectionStatus.NOT_CONNECTED
                    }
                    safetyShowMessage("接続に失敗しました")
                }
            }
        }
    }

    private fun onReadItemSuccess(item: Item) {
        eventBus.post(BarcodeEvent.ReadItemSuccess(item))
    }

    private fun onReadItemFailure(e: Throwable) {
        eventBus.post(BarcodeEvent.ReadItemFailed(e))
    }

    private fun onReadStaffSuccess(staff: Staff) {
        eventBus.post(BarcodeEvent.ReadStaffSuccess(staff))
    }

    private fun onReadStaffFailure(e: Throwable) {
        eventBus.post(BarcodeEvent.ReadStaffFailed(e))
    }

    private fun safetyShowMessage(message: String) {
        launch(Dispatchers.Main) {
            listener?.onShouldShowMessage(message)
        }
    }

    private fun updateTitle() {
        var titleSuffix = ""
        config.currentStore?.name?.also { titleSuffix += " [$it]" }

        titleSuffix += " [${config.currentRunningMode.modeName}モード]"

        @Suppress("ConstantConditionIf")
        if (ProjectSettings.DEMO_MODE) titleSuffix += " [テストモード]"

        listener?.onShouldChangeTitleSuffix(titleSuffix)
    }

    private suspend fun requestGetItem(barcode: String) = withContext(Dispatchers.IO) {
        api.getItem(barcode).await()
    }

    private suspend fun requestGetStaff(barcode: String) = withContext(Dispatchers.IO) {
        api.getStaff(barcode).await()
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