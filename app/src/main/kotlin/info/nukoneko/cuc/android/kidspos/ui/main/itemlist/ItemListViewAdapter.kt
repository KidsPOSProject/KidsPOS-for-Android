package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.ItemListItemBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import java.util.*

class ItemListViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.item_list_item,
                viewGroup, false)

        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> holder.bind(data[position])
        }
    }

    private val data = ArrayList<Item>()

    override fun getItemCount(): Int {
        return data.size
    }

    fun setItems(items: List<Item>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun add(item: Item) {
        data.add(0, item)
        notifyItemInserted(0)
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    companion object {
        private class ContentViewHolder(private val binding: ItemListItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: Item) {
                if (binding.viewModel == null) {
                    binding.viewModel = ItemItemListContentViewModel(data)
                } else {
                    binding.viewModel!!.data = data
                }
            }
        }
    }
}
