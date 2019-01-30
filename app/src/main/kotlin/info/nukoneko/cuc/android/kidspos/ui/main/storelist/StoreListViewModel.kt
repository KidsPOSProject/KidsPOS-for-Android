package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Store
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class StoreListViewModel(
        private val api: APIService,
        private val config: GlobalConfig) : ViewModel() {
    private val data = MutableLiveData<List<Store>>()
    fun getData(): LiveData<List<Store>> = data

    private val progressVisibility = MutableLiveData<Int>()
    fun getProgressVisibility(): LiveData<Int> = progressVisibility

    private val recyclerViewVisibility = MutableLiveData<Int>()
    fun getRecyclerViewVisibility(): LiveData<Int> = recyclerViewVisibility

    private val errorButtonVisibility = MutableLiveData<Int>()
    fun getErrorButtonVisibility(): LiveData<Int> = errorButtonVisibility

    var listener: Listener? = null

    private val compositeDisposable = CompositeDisposable()

    init {
        progressVisibility.value = View.GONE
        recyclerViewVisibility.value = View.GONE
        errorButtonVisibility.value = View.GONE
    }

    override fun onCleared() {
        listener = null
        compositeDisposable.clear()
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
        progressVisibility.postValue(View.VISIBLE)
        recyclerViewVisibility.postValue(View.GONE)
        errorButtonVisibility.postValue(View.GONE)

        val disposable = api.fetchStores()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(config.getDefaultSubscribeScheduler())
                .subscribe({ stores ->
                    onFetchStoresSuccess(stores)
                }, {
                    onFetchStoresFailure(it)
                })
        compositeDisposable.add(disposable)
    }

    private fun onFetchStoresSuccess(stores: List<Store>) {
        data.postValue(stores)
        progressVisibility.postValue(View.GONE)
        if (!stores.isEmpty()) {
            recyclerViewVisibility.postValue(View.VISIBLE)
        } else {
            errorButtonVisibility.postValue(View.VISIBLE)
        }
    }

    private fun onFetchStoresFailure(error: Throwable) {
        listener?.onError(error.localizedMessage)
        progressVisibility.postValue(View.GONE)
        errorButtonVisibility.postValue(View.VISIBLE)
    }

    interface Listener {
        fun onError(message: String)

        fun onDismiss()
    }
}