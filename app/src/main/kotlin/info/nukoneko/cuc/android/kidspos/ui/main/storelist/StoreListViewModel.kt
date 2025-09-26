package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.RequestStatus
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
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

        // 練習モードの場合はダミー店舗を即座に表示
        if (config.currentRunningMode == Mode.PRACTICE) {
            val dummyStores = listOf(
                Store(1, "100リバー", null),
                Store(2, "デパート", null)
            )
            onFetchStoresSuccess(dummyStores)
            return
        }

        requestStatus = RequestStatus.REQUESTING
        launch {
            requestStatus = try {
                // 3秒のタイムアウトを設定
                val stores: List<Store> = withTimeout(3000L) {
                    requestFetchStores()
                }
                onFetchStoresSuccess(stores)
                RequestStatus.SUCCESS
            } catch (e: Throwable) {
                onFetchStoresFailure(e)
                RequestStatus.FAILURE
            }
        }
    }

    private suspend fun requestFetchStores() = withContext(Dispatchers.IO) {
        api.fetchStores()
    }

    private fun onFetchStoresSuccess(stores: List<Store>) {
        data.postValue(stores)
        if (stores.isNotEmpty()) {
            recyclerViewVisibility.postValue(View.VISIBLE)
        } else {
            errorButtonVisibility.postValue(View.VISIBLE)
        }
        requestStatus = RequestStatus.SUCCESS
    }

    private fun onFetchStoresFailure(error: Throwable) {
        val message = when {
            error is kotlinx.coroutines.TimeoutCancellationException ->
                "サーバーとの通信がタイムアウトしました。\n接続を確認してください。"
            error.localizedMessage != null ->
                error.localizedMessage!!
            else ->
                "通信エラーが発生しました。"
        }
        listener?.onShouldShowErrorDialog(message)
        requestStatus = RequestStatus.FAILURE
    }

    interface Listener {
        fun onShouldShowErrorDialog(message: String)

        fun onDismiss()
    }
}
