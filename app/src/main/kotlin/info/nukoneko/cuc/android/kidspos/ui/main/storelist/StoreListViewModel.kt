package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.nukoneko.cuc.android.kidspos.api.RequestStatus
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.domain.entity.Store
import info.nukoneko.cuc.android.kidspos.domain.repository.StoreRepository
import info.nukoneko.cuc.android.kidspos.extensions.mutableLiveDataOf
import info.nukoneko.cuc.android.kidspos.extensions.postValue
import info.nukoneko.cuc.android.kidspos.ui.common.VMEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoreListViewModel(
    private val storeRepository: StoreRepository,
    private val config: GlobalConfig
) : ViewModel() {
    private val _data = mutableLiveDataOf<VMEvent<List<Store>>>()
    val data: LiveData<VMEvent<List<Store>>> get() = _data

    private val _presentErrorDialog = mutableLiveDataOf<VMEvent<String>>()
    val presentErrorDialog: LiveData<VMEvent<String>> get() = _presentErrorDialog

    private val _shouldDismiss = mutableLiveDataOf<VMEvent<Unit>>()
    val shouldDismiss: LiveData<VMEvent<Unit>> get() = _shouldDismiss

    private val progressVisibility = MutableLiveData<Int>()
    fun getProgressVisibility(): LiveData<Int> = progressVisibility

    private val recyclerViewVisibility = MutableLiveData<Int>()
    fun getRecyclerViewVisibility(): LiveData<Int> = recyclerViewVisibility

    private val errorButtonVisibility = MutableLiveData<Int>()
    fun getErrorButtonVisibility(): LiveData<Int> = errorButtonVisibility

    private var requestStatus: RequestStatus<List<Store>> = RequestStatus.IDLE()
        set(value) {
            field = value
            when (value) {
                is RequestStatus.IDLE -> {
                    progressVisibility.postValue(View.GONE)
                    recyclerViewVisibility.postValue(View.GONE)
                    errorButtonVisibility.postValue(View.GONE)
                }
                is RequestStatus.REQUESTING -> {
                    progressVisibility.postValue(View.VISIBLE)
                    recyclerViewVisibility.postValue(View.GONE)
                    errorButtonVisibility.postValue(View.GONE)
                }
                is RequestStatus.SUCCEEDED -> {
                    progressVisibility.postValue(View.GONE)
                    if (value.body.isNotEmpty()) {
                        recyclerViewVisibility.postValue(View.VISIBLE)
                    } else {
                        errorButtonVisibility.postValue(View.VISIBLE)
                    }
                    _data.postValue(value.body)
                }
                is RequestStatus.FAILED -> {
                    progressVisibility.postValue(View.GONE)
                    errorButtonVisibility.postValue(View.VISIBLE)
                    _presentErrorDialog.postValue(value.error.localizedMessage)
                }
            }
        }

    fun onResume() {
        fetchStores()
    }

    fun onSelect(store: Store) {
        config.currentStore = store
        dismiss()
    }

    fun onReload(@Suppress("UNUSED_PARAMETER") view: View?) {
        fetchStores()
    }

    fun onClose(@Suppress("UNUSED_PARAMETER") view: View?) {
        dismiss()
    }

    private fun dismiss() {
        _shouldDismiss.postValue(Unit)
    }

    private fun fetchStores() {
        if (requestStatus is RequestStatus.REQUESTING) {
            return
        }
        requestStatus = RequestStatus.REQUESTING()

        val errorHandler = CoroutineExceptionHandler { _, error ->
            requestStatus = RequestStatus.FAILED(error)
        }
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            requestStatus = RequestStatus.SUCCEEDED(storeRepository.fetchStoreList())
        }
    }
}
