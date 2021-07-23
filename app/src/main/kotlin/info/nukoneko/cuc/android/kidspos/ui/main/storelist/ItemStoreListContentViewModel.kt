package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import info.nukoneko.cuc.android.kidspos.entity.Store

class ItemStoreListContentViewModel(private var store: Store, private val listener: Listener?) :
    BaseObservable() {
    @Bindable
    val storeName = store.name

    fun onItemClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onStoreSelected(store)
    }

    fun setStore(store: Store) {
        this.store = store
        notifyChange()
    }

    interface Listener {
        fun onStoreSelected(store: Store)
    }
}
