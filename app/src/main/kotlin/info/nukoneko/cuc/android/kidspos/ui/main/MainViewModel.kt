@file:Suppress("KotlinConstantConditions", "unused")

package info.nukoneko.cuc.android.kidspos.ui.main

import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.coroutines.CoroutineContext

class MainViewModel(
    private val api: APIService,
    private val config: GlobalConfig,
    private val eventBus: EventBus
) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    var listener: Listener? = null

    enum class ConnectionStatus {
        CONNECTING, CONNECTED, NOT_CONNECTED
    }

    private var status: ConnectionStatus = ConnectionStatus.NOT_CONNECTED

    fun onBarcodeInput(barcode: String) {
        Logger.i("MainViewModel.onBarcodeInput: barcode=$barcode,  demoMode=${ProjectSettings.DEMO_MODE}")

        if (ProjectSettings.DEMO_MODE) {
            onReadItemSuccess(Item.create(barcode))
        } else {
            // サーバから取得する
            Logger.d("Requesting item from server: $barcode")
            launch {
                try {
                    val item = requestGetItem(barcode)
                    Logger.i("Item received from server: ${item.name}")
                    onReadItemSuccess(item)
                } catch (e: Throwable) {
                    Logger.e(e, "Failed to get item from server: $barcode")
                    onReadItemFailure(e)
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
                    api.getStatus()
                    withContext(Dispatchers.Main) {
                        status = ConnectionStatus.CONNECTED
                        safetyShowMessage("接続しました")
                    }
                } catch (e: Throwable) {
                    val errorMessage = when (e) {
                        is java.net.UnknownHostException ->
                            "サーバーが見つかりません。ネットワーク設定を確認してください"

                        is java.net.ConnectException ->
                            "サーバーに接続できません。サーバーが起動しているか確認してください"

                        is java.net.SocketTimeoutException ->
                            "接続がタイムアウトしました。ネットワーク環境を確認してください"

                        is javax.net.ssl.SSLException ->
                            "SSL証明書エラーです。サーバー設定を確認してください"

                        else ->
                            "接続に失敗しました: ${e.javaClass.simpleName} - ${e.message ?: "不明なエラー"}"
                    }
                    withContext(Dispatchers.Main) {
                        status = ConnectionStatus.NOT_CONNECTED
                        listener?.onNotReachableServer(errorMessage)
                    }
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

    private fun safetyShowMessage(message: String) {
        launch(Dispatchers.Main) {
            listener?.onShouldShowMessage(message)
        }
    }

    private fun updateTitle() {
        var titleSuffix = ""
        config.currentStore?.name?.also { titleSuffix += " [$it]" }

        titleSuffix += " [${config.currentRunningMode.modeName}モード]"

        if (ProjectSettings.DEMO_MODE) titleSuffix += " [テストモード]"

        listener?.onShouldChangeTitleSuffix(titleSuffix)
    }

    private suspend fun requestGetItem(barcode: String) = withContext(Dispatchers.IO) {
        api.getItem(barcode)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectShopEvent(@Suppress("UNUSED_PARAMETER") event: SystemEvent.SelectShop) {
        updateTitle()
    }

    interface Listener {
        fun onShouldChangeTitleSuffix(titleSuffix: String)

        fun onNotReachableServer(errorMessage: String)

        fun onShouldShowMessage(message: String)
    }
}
