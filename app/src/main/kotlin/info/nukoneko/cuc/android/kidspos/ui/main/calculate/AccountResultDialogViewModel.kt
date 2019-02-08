package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View

class AccountResultDialogViewModel : ViewModel() {
    private val priceText = MutableLiveData<String>()
    fun getPriceText(): LiveData<String> = priceText

    private val depositText = MutableLiveData<String>()
    fun getDepositText(): LiveData<String> = depositText

    private val chargeText = MutableLiveData<String>()
    fun getChargeText(): LiveData<String> = chargeText

    var listener: Listener? = null

    fun setup(price: Int, deposit: Int) {
        priceText.value = "$price リバー"
        depositText.value = "$deposit リバー"
        chargeText.value = "${deposit - price} リバー"
    }

    fun onOkClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onOk()
    }

    fun onCancelClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onCancel()
    }

    interface Listener {
        fun onOk()

        fun onCancel()
    }
}