package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.ItemStoreListBinding
import info.nukoneko.cuc.android.kidspos.entity.Store

class StoreListViewAdapter : RecyclerView.Adapter<StoreListViewAdapter.ViewHolder>() {

    var data: List<Store> = emptyList()
    var listener: Listener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemStoreListBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.item_store_list, viewGroup, false
        )
        return ViewHolder(binding, object : ViewHolder.Listener {
            override fun onItemClick(store: Store) {
                listener?.onStoreSelect(store)
            }
        })
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

    class ViewHolder(private val binding: ItemStoreListBinding, listener: Listener?) : RecyclerView.ViewHolder(binding.item) {
        private val listener: ItemStoreListContentViewModel.Listener

        init {
            this.listener = object : ItemStoreListContentViewModel.Listener {
                override fun onStoreSelected(store: Store) {
                    listener?.onItemClick(store)
                }
            }
        }

        fun bind(store: Store) {
            if (binding.viewModel == null) {
                binding.viewModel = ItemStoreListContentViewModel(store, listener)
            } else {
                binding.viewModel!!.setStore(store)
            }
        }

        interface Listener {
            fun onItemClick(store: Store)
        }
    }
}
