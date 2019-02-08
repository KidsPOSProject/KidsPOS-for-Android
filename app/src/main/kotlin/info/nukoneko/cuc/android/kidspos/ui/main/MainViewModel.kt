package info.nukoneko.cuc.android.kidspos.ui.main

import android.arch.lifecycle.ViewModel
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.NetworkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val api: APIService,
                    private val config: GlobalConfig,
                    private val eventBus: EventBus) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    var listener: Listener? = null

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
                BarcodeKind.SALE -> eventBus.post(BarcodeEvent.ReadReceiptFailed)
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

        if (!config.isPracticeModeEnabled) {
            launch {
                val reachable = NetworkUtil.checkReachability(config.serverUrl, config.serverPort)
                if (!reachable) {
                    listener?.onNotReachableServer()
                }
            }
        }
    }

    private fun onReadItemSuccess(item: Item) {
        eventBus.post(BarcodeEvent.ReadItemSuccess.apply {
            value = item
        })
    }

    private fun onReadItemFailure(e: Throwable) {
        eventBus.post(BarcodeEvent.ReadItemFailed.apply {
            value = e
        })
    }

    private fun onReadStaffSuccess(staff: Staff) {
        eventBus.post(BarcodeEvent.ReadStaffSuccess.apply {
            value = staff
        })
    }

    private fun onReadStaffFailure(e: Throwable) {
        eventBus.post(BarcodeEvent.ReadStaffFailed.apply {
            value = e
        })
    }

    private fun updateTitle() {
        var titleSuffix = ""
        config.currentStore?.name?.also { titleSuffix += " [$it]" }

        if (config.isPracticeModeEnabled) titleSuffix += " [練習モード]"

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
    fun onSelectShopEvent(event: SystemEvent.SelectShop) {
        updateTitle()
    }

    interface Listener {
        fun onShouldChangeTitleSuffix(titleSuffix: String)

        fun onNotReachableServer()
    }
}