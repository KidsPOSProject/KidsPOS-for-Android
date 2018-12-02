package info.nukoneko.cuc.android.kidspos.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.Constants
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.event.StoreUpdateEvent
import info.nukoneko.cuc.android.kidspos.event.SumPriceUpdateEvent
import info.nukoneko.cuc.android.kidspos.util.BarcodePrefix
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket

class MainActivityViewModel(app: Application): AndroidViewModel(app) {
    private val sumPrice = MutableLiveData<Int>()
    fun getSumPrice(): LiveData<Int> = sumPrice
    private val currentStore = MutableLiveData<Store>()
    fun getCurrentStore(): LiveData<Store> = currentStore
    private val currentStaff = MutableLiveData<Staff>()
    fun getCurrentStaff(): LiveData<Staff> = currentStaff
    var listener: Listener? = null

    init {
        sumPrice.value = 0
    }

    private val app: KidsPOSApplication? = KidsPOSApplication.get(app)

    fun onClickClear(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onClear()
    }

    fun onClickAccount(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onAccount()
    }

    fun onResume() {
        val app = app ?: return

        currentStore.postValue(app.currentStore)
        currentStaff.postValue(app.currentStaff)

        requestUpdateTitle()

        if (!app.isPracticeModeEnabled) {
            GlobalScope.launch {
                if (!checkReachable(app.serverIp, app.serverPort).await()) {
                    listener?.onNotReachableServer()
                }
            }
        }
    }

    private fun requestUpdateTitle() {
        val app = app ?: return

        var actionBarTitle = app.getString(R.string.app_name)

        app.currentStore?.name?.also { actionBarTitle += " [$it]" }

        if (app.isPracticeModeEnabled) actionBarTitle += " [練習モード]"

        @Suppress("ConstantConditionIf")
        if (Constants.TEST_MODE) actionBarTitle += " [テストモード]"

        listener?.onChangeTitle(actionBarTitle)
    }

    fun onBarcodeInput(barcode: String, prefix: BarcodePrefix) {
        val app = app ?: return

        @Suppress("ConstantConditionIf")
        if (Constants.TEST_MODE) {
//            Toast.makeText(this, String.format("%s", barcode), Toast.LENGTH_SHORT).show()
            when (prefix) {
                BarcodePrefix.ITEM -> listener?.onAddNewItem(Item.create(barcode))
                BarcodePrefix.STAFF -> currentStaff.postValue(Staff.create(barcode))
                else -> {
                    listener?.onAddNewItem(Item.create(barcode))
                    currentStaff.postValue(Staff.create(barcode))
                }
            }
            return
        }

        // サーバから取得する
        when (prefix) {
            BarcodePrefix.ITEM -> app.itemManager.getItem(barcode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(app.defaultSubscribeScheduler())
                    .subscribe({ item -> listener?.onAddNewItem(item) }, {
//                        Toast.makeText(this@MainActivity,
//                                String.format("なにかがおかしいよ?\n%s", barcode),
//                                Toast.LENGTH_SHORT).show()
                    })
            BarcodePrefix.STAFF -> app.staffManager.getStaff(barcode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(app.defaultSubscribeScheduler())
                    .subscribe({ staff -> app.updateCurrentStaff(staff) }, {
//                        Toast.makeText(this@MainActivity,
//                                "当日に登録したスタッフの場合、別途登録が必要です",
//                                Toast.LENGTH_SHORT).show()
                        app.updateCurrentStaff(Staff.create(barcode))
                    })
            BarcodePrefix.SALE -> {}//Toast.makeText(this, "レシートの読取は今はできません", Toast.LENGTH_SHORT).show()
            BarcodePrefix.UNKNOWN -> {
            }
        }
    }

    fun onStoreUpdate(event: StoreUpdateEvent) {
        currentStore.postValue(event.store)
        requestUpdateTitle()
    }

    private fun checkReachable(serverIp: String, serverPort: String) = GlobalScope.async {
        try {
            val sock = Socket()
            sock.connect(InetSocketAddress(serverIp, Integer.parseInt(serverPort)), 2000)
            sock.close()
            return@async true
        } catch (e: Exception) {
            Logger.e(e, "CheckReachableTask")
            return@async false
        }
    }

    fun onSumPriceUpdate(event: SumPriceUpdateEvent) {
        sumPrice.postValue(event.currentValue)
    }

    interface Listener {
        fun onClear()

        fun onAccount()

        fun onChangeTitle(title: String)

        fun onAddNewItem(item: Item)

        fun onNotReachableServer()
    }
}
