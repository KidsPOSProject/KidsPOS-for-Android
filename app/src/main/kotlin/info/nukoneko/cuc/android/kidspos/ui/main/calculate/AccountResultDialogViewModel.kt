package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View

class AccountResultDialogViewModel : ViewModel() {
    private val price = MutableLiveData<Int>()
    fun getPrice(): LiveData<Int> = price

    private val receiveMoney = MutableLiveData<Int>()
    fun getReceiveMoney(): LiveData<Int> = receiveMoney

    var listener: Listener? = null

    val chargeRiver: Int
        get() = (receiveMoney.value ?: 0) - (price.value ?: 0)

    init {
        price.value = 0
        receiveMoney.value = 0
    }

    fun setupValue(price: Int, receiveMoney: Int) {
        this.price.postValue(price)
        this.receiveMoney.postValue(receiveMoney)
    }

    fun onAccountClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onAccount()
    }

    fun onBackClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onBack()
    }

    interface Listener {
        fun onAccount()

        fun onBack()
    }
}
