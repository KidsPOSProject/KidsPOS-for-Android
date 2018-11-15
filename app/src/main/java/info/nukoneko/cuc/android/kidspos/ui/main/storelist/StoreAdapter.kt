package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.ItemStoreListBinding
import info.nukoneko.cuc.android.kidspos.entity.Store

class StoreAdapter: RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private var data: List<Store> = emptyList()
    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setData(data: List<Store>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemStoreListBinding>(
                LayoutInflater.from(viewGroup.context),
                R.layout.item_store_list, viewGroup, false
        )
        return ViewHolder(binding, object : ViewHolder.Listener {
            override fun onItemClick(store: Store) {
                listener?.onItemClick(store)
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
        fun onItemClick(store: Store)
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
