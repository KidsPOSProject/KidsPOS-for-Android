package info.nukoneko.cuc.android.kidspos.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel(private val api: APIService,
                    private val config: GlobalConfig,
                    private val eventBus: EventBus) : ViewModel() {
    var listener: Listener? = null

    fun onBarcodeInput(barcode: String, prefix: BarcodeKind) {
        @Suppress("ConstantConditionIf")
        if (ProjectSettings.TEST_MODE) {
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
                BarcodeKind.ITEM -> api.getItem(barcode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(config.getDefaultSubscribeScheduler())
                        .subscribe({ item ->
                            onReadItemSuccess(item)
                        }, { e ->
                            onReadItemFailure(e)
                        })
                BarcodeKind.STAFF -> api.getStaff(barcode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(config.getDefaultSubscribeScheduler())
                        .subscribe({ staff ->
                            onReadStaffSuccess(staff)
                        }, { e ->
                            onReadStaffFailure(e)
                        })
                BarcodeKind.SALE -> eventBus.post(BarcodeEvent.ReadReceiptFailed)
                BarcodeKind.UNKNOWN -> {
                }
            }
        }
    }

    fun onResume() {
        updateTitle()

        if (!config.isPracticeModeEnabled) {
            GlobalScope.launch(Dispatchers.IO) {
                if (!checkReachability(config.serverUrl, config.serverPort)) {
                    launch(Dispatchers.Main) {
                        listener?.onNotReachableServer()
                    }
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
        if (ProjectSettings.TEST_MODE) titleSuffix += " [テストモード]"

        listener?.onShouldChangeTitleSuffix(titleSuffix)
    }

    private suspend fun checkReachability(hostName: String, serverPort: Int) = suspendCoroutine<Boolean> {
        try {
            val sock = Socket()
            sock.connect(InetSocketAddress(hostName, serverPort), 2000)
            sock.close()
            it.resume(true)
        } catch (e: Throwable) {
            it.resume(false)
        }
    }

    interface Listener {
        fun onShouldChangeTitleSuffix(titleSuffix: String)

        fun onNotReachableServer()
    }
}