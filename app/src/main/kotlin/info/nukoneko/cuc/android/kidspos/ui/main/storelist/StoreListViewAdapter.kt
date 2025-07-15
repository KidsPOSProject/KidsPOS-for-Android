package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import info.nukoneko.cuc.android.kidspos.R
import android.widget.TextView
import info.nukoneko.cuc.android.kidspos.entity.Store

class StoreListViewAdapter : RecyclerView.Adapter<StoreListViewAdapter.ViewHolder>() {

    var data: List<Store> = emptyList()
    var listener: Listener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_store_list, viewGroup, false) as TextView
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listener {
        fun onStoreSelect(store: Store)
    }

    class ViewHolder(private val textView: TextView, private val listener: Listener?) :
        RecyclerView.ViewHolder(textView) {
        
        private var currentStore: Store? = null

        init {
            textView.setOnClickListener {
                currentStore?.let { store ->
                    listener?.onStoreSelect(store)
                }
            }
        }

        fun bind(store: Store) {
            currentStore = store
            textView.text = store.name
        }
    }
}
