package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class CalculatorDialogViewModel(
        private val api: APIService,
        private val config: GlobalConfig,
        private val event: EventBus) : ViewModel() {
    private val totalPriceText = MutableLiveData<String>()
    fun getTotalPriceText(): LiveData<String> = totalPriceText

    private val compositeDisposable = CompositeDisposable()

    var listener: Listener? = null

    fun sendToServer(items: List<Item>, totalPrice: Int, deposit: Int) {
        var sum = StringBuilder()
        for ((id) in items) {
            sum.append(id.toString()).append(",")
        }
        sum = StringBuilder(sum.substring(0, sum.length - 1))
        val staff = config.currentStaff
        val store = config.currentStore
        val staffBarcode = staff?.barcode ?: ""
        val storeId = store?.id ?: 0

        if (config.isPracticeModeEnabled) {
            listener?.showMessage("練習モードのためレシートは出ません")
            listener?.onDismiss()
        } else {
//            val progressDialog = ProgressDialog(context)
//            progressDialog.setTitle("送信しています")
//            progressDialog.show()

            val disposable = api.createSale(deposit, items.size, totalPrice, sum.toString(), storeId, staffBarcode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(config.getDefaultSubscribeScheduler())
                    .subscribe({ (_, barcode, createdAt, points, price, items, storeId1, staffId) ->
                        //                        progressDialog.dismiss()
                        event.post(SystemEvent.SentSaleSuccess)
                        listener?.onDismiss()
                    }, { throwable ->
                        throwable.printStackTrace()
//                        progressDialog.dismiss()
                        listener?.onDismiss()
                    })
            compositeDisposable.add(disposable)
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    interface Listener {
        fun showMessage(message: String)

        fun onDismiss()
    }
}