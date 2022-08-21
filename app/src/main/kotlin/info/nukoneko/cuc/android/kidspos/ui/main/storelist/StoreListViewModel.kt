package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.RequestStatus
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class StoreListViewModel(
    private val api: APIService,
    private val config: GlobalConfig
) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val data = MutableLiveData<List<Store>>()
    fun getData(): LiveData<List<Store>> = data

    private val progressVisibility = MutableLiveData<Int>()
    fun getProgressVisibility(): LiveData<Int> = progressVisibility

    private val recyclerViewVisibility = MutableLiveData<Int>()
    fun getRecyclerViewVisibility(): LiveData<Int> = recyclerViewVisibility

    private val errorButtonVisibility = MutableLiveData<Int>()
    fun getErrorButtonVisibility(): LiveData<Int> = errorButtonVisibility

    var listener: Listener? = null

    private var requestStatus: RequestStatus = RequestStatus.IDLE
        set(value) {
            field = value
            when (value) {
                RequestStatus.IDLE -> {
                    progressVisibility.postValue(View.GONE)
                    recyclerViewVisibility.postValue(View.GONE)
                    errorButtonVisibility.postValue(View.GONE)
                }
                RequestStatus.REQUESTING -> {
                    progressVisibility.postValue(View.VISIBLE)
                    recyclerViewVisibility.postValue(View.GONE)
                    errorButtonVisibility.postValue(View.GONE)
                }
                RequestStatus.SUCCESS -> {
                    progressVisibility.postValue(View.GONE)
                }
                RequestStatus.FAILURE -> {
                    progressVisibility.postValue(View.GONE)
                    errorButtonVisibility.postValue(View.VISIBLE)
                }
            }
        }

    init {
        recyclerViewVisibility.value = View.GONE
        errorButtonVisibility.value = View.GONE
    }

    override fun onCleared() {
        listener = null
        super.onCleared()
    }

    fun onResume() {
        fetchStores()
    }

    fun onSelect(store: Store) {
        config.currentStore = store
        listener?.onDismiss()
    }

    fun onReload(@Suppress("UNUSED_PARAMETER") view: View?) {
        fetchStores()
    }

    fun onClose(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onDismiss()
    }

    private fun fetchStores() {
        if (requestStatus == RequestStatus.REQUESTING) {
            return
        }
        requestStatus = RequestStatus.REQUESTING
        launch {
            try {
                val stores: List<Store> = requestFetchStores()
                onFetchStoresSuccess(stores)
                requestStatus = RequestStatus.SUCCESS
            } catch (e: Throwable) {
                onFetchStoresFailure(e)
                requestStatus = RequestStatus.FAILURE
            }
        }
    }

    private suspend fun requestFetchStores() = withContext(Dispatchers.IO) {
        api.fetchStores()
    }

    private fun onFetchStoresSuccess(stores: List<Store>) {
        data.postValue(stores)
        if (!stores.isEmpty()) {
            recyclerViewVisibility.postValue(View.VISIBLE)
        } else {
            errorButtonVisibility.postValue(View.VISIBLE)
        }
        requestStatus = RequestStatus.SUCCESS
    }

    private fun onFetchStoresFailure(error: Throwable) {
        listener?.onShouldShowErrorDialog(error.localizedMessage)
        requestStatus = RequestStatus.FAILURE
    }

    interface Listener {
        fun onShouldShowErrorDialog(message: String)

        fun onDismiss()
    }
}
