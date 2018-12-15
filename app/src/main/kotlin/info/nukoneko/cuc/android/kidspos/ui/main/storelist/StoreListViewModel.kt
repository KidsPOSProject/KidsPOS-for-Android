package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.view.View
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication
import info.nukoneko.cuc.android.kidspos.entity.Store
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class StoreListViewModel(application: Application) : AndroidViewModel(application) {

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

    private fun fetchStores() {
        val app = KidsPOSApplication[getApplication()] ?: return

        progressVisibility.postValue(View.VISIBLE)
        recyclerViewVisibility.postValue(View.GONE)
        errorButtonVisibility.postValue(View.GONE)

        val disposable = app.storeManager.fetchStores()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(app.getDefaultSubscribeScheduler())
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
    }
}
