package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

class CalculatorDialogViewModel(application: Application) : AndroidViewModel(application) {
    private val totalPriceText = MutableLiveData<String>()
    fun getTotalPriceText(): LiveData<String> = totalPriceText
}