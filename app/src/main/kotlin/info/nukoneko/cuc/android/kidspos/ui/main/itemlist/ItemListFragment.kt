package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentItemListBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.ui.main.calculate.CalculatorDialogFragment
import org.koin.android.ext.android.inject

class ItemListFragment : Fragment() {
    private lateinit var binding: FragmentItemListBinding
    private val myViewModel: ItemListViewModel by inject()
    private val adapter: ItemListViewAdapter by lazy {
        ItemListViewAdapter()
    }

    private val listener: ItemListViewModel.Listener = object : ItemListViewModel.Listener {
        override fun onDataAdded(data: Item) {
            adapter.add(data)
        }

        override fun onDataClear() {
            adapter.clear()
        }

        override fun onShouldShowMessage(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        override fun onShouldShowMessage(messageId: Int) {
            Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
        }

        override fun onStartAccount(data: List<Item>) {
            CalculatorDialogFragment
                .newInstance(ArrayList(data))
                .show(childFragmentManager, "Calculator")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_list, container, false)
        binding.viewModel = myViewModel.also {
            it.listener = listener
        }
        binding.lifecycleOwner = this
        setupList(binding.recyclerView)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        myViewModel.onResume()
    }

    override fun onStart() {
        super.onStart()
        myViewModel.onStart()
    }

    override fun onStop() {
        myViewModel.onStop()
        super.onStop()
    }

    private fun setupList(list: RecyclerView) {
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(list.context, 3)
    }

    companion object {
        fun newInstance() = ItemListFragment()
    }
}
