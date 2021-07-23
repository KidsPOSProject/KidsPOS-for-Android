package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.Item
import java.util.*

class ItemListViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_list_item, viewGroup, false)
        return ContentViewHolder(view)
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
        private class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val itemNameText: TextView = view.findViewById(R.id.item_name)
            private val itemPriceText: TextView = view.findViewById(R.id.item_price)
            private val itemBarcodeText: TextView = view.findViewById(R.id.item_barcode)
            fun bind(data: Item) {
                itemNameText.text = data.name
                itemPriceText.text = "${data.price}"
                itemBarcodeText.text = data.barcode
            }
        }
    }
}
