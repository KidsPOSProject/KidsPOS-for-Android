package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.util.Mode
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ItemListViewModel(
    private val config: GlobalConfig,
    private val eventBus: EventBus
) : ViewModel() {

    private val currentPrice = MutableLiveData<String>()
    fun getCurrentPriceText(): LiveData<String> = currentPrice

    private val accountButtonEnabled = MutableLiveData<Boolean>()
    fun getAccountButtonEnabled(): LiveData<Boolean> = accountButtonEnabled

    private val addItemButtonVisibility = MutableLiveData<Int>()
    fun getAddItemButtonVisibility(): LiveData<Int> = addItemButtonVisibility

    var listener: Listener? = null

    private val data: MutableList<Item> = mutableListOf()

    private fun currentTotal(): Int = data.sumOf { it.price }

    init {
        updateViews()
    }

    fun onClickClear(@Suppress("UNUSED_PARAMETER") view: View?) {
        data.clear()
        listener?.onDataClear()
        updateViews()
    }

    fun onClickAccount(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onStartAccount(data)
    }

    fun onClickAddItem(@Suppress("UNUSED_PARAMETER") view: View?) {
        // ダミー商品を追加
        val dummyItems = listOf(
            Item(1, "123456789", "おもちゃ", 100, 1, 1),
            Item(2, "223456789", "お菓子", 50, 1, 1),
            Item(3, "323456789", "本", 200, 1, 1),
            Item(4, "423456789", "文房具", 150, 1, 1),
            Item(5, "523456789", "ゲーム", 300, 1, 1)
        )
        val randomItem = dummyItems.random()
        data.add(randomItem)
        listener?.onDataAdded(randomItem)
        updateViews()
    }

    fun onResume() {
        updateViews()
    }

    fun onStart() {
        eventBus.register(this)
    }

    fun onStop() {
        eventBus.unregister(this)
    }

    private fun updateViews() {
        currentPrice.postValue("${currentTotal()} リバー")
        accountButtonEnabled.postValue(data.isNotEmpty())
        // 練習モードのときのみ商品追加ボタンを表示
        addItemButtonVisibility.postValue(
            if (config.currentRunningMode == Mode.PRACTICE) View.VISIBLE else View.GONE
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReadItemSuccessEvent(event: BarcodeEvent.ReadItemSuccess) {
        val item = event.item
        data.add(item)
        listener?.onDataAdded(item)
        updateViews()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReadItemFailedEvent(@Suppress("UNUSED_PARAMETER") event: BarcodeEvent.ReadItemFailed) {
        listener?.onShouldShowMessage(R.string.request_item_failed)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReadReceiptFailedEvent(@Suppress("UNUSED_PARAMETER") event: BarcodeEvent.ReadReceiptFailed) {
        listener?.onShouldShowMessage(R.string.read_receipt_failed)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSentSaleSuccessEvent(@Suppress("UNUSED_PARAMETER") event: SystemEvent.SentSaleSuccess) {
        data.clear()
        listener?.onDataClear()
        updateViews()
    }

    interface Listener {
        fun onDataClear()

        fun onDataAdded(data: Item)

        fun onStartAccount(data: List<Item>)

        fun onShouldShowMessage(message: String)

        fun onShouldShowMessage(@StringRes messageId: Int)
    }
}
