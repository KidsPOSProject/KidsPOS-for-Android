package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentItemListBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.Event
import info.nukoneko.cuc.android.kidspos.ui.main.MainActivityViewModel

class ItemListFragment : Fragment() {
    private lateinit var binding: FragmentItemListBinding

    private val mainViewModel: MainActivityViewModel? by lazy {
        activity?.let {
            ViewModelProviders.of(it)[MainActivityViewModel::class.java]
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_list, container, false)
        setupRecyclerView(binding.list)
        setupSubscriber()
        return binding.root
    }

    private fun setupSubscriber() {
        KidsPOSApplication[context]?.let {
            it.getGlobalEventObserver().observe(this, Observer<Event> { event ->
                when (event) {
                    BarcodeEvent.ReadItemSuccess -> {
                        val item = event.value as? Item ?: return@Observer
                        (binding.list.adapter as? ItemListViewAdapter)?.add(item)
                    }
                }
            })
        }
    }

    private fun setupRecyclerView(list: RecyclerView) {
        list.adapter = ItemListViewAdapter()
        list.layoutManager = GridLayoutManager(context, 3, RecyclerView.HORIZONTAL, false)
    }

    companion object {
        fun newInstance(): ItemListFragment = ItemListFragment()
    }
}