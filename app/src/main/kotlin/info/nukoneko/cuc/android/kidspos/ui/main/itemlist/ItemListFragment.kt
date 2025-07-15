package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    ): View {
        binding = FragmentItemListBinding.inflate(inflater, container, false)
        myViewModel.listener = listener
        setupList(binding.recyclerView)
        setupViewModelObservers()
        setupClickListeners()
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

    private fun setupViewModelObservers() {
        myViewModel.getCurrentPriceText().observe(viewLifecycleOwner) { price ->
            binding.priceView.text = price
        }
        myViewModel.getCurrentStaffText().observe(viewLifecycleOwner) { staff ->
            binding.staffText.text = staff
        }
        myViewModel.getCurrentStaffVisibility().observe(viewLifecycleOwner) { visibility ->
            binding.staffLayout.visibility = visibility
        }
        myViewModel.getAccountButtonEnabled().observe(viewLifecycleOwner) { enabled ->
            binding.accountButton.isEnabled = enabled
        }
    }

    private fun setupClickListeners() {
        binding.clearButton.setOnClickListener {
            myViewModel.onClickClear(it)
        }
        binding.accountButton.setOnClickListener {
            myViewModel.onClickAccount(it)
        }
    }

    companion object {
        fun newInstance() = ItemListFragment()
    }
}
