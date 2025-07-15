package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentStoreListDialogBinding
import info.nukoneko.cuc.android.kidspos.entity.Store
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoreListDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentStoreListDialogBinding
    private val myViewModel: StoreListViewModel by viewModel()
    private val listener = object : StoreListViewModel.Listener {
        override fun onDismiss() {
            dismiss()
        }

        override fun onShouldShowErrorDialog(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private val adapterListener = object : StoreListViewAdapter.Listener {
        override fun onStoreSelect(store: Store) {
            myViewModel.onSelect(store)
        }
    }

    private val adapter: StoreListViewAdapter by lazy {
        StoreListViewAdapter().also {
            it.listener = adapterListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreListDialogBinding.inflate(inflater, container, false)
        myViewModel.listener = listener
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setupRecyclerView()
        setupSubscriber()
        setupViewModelObservers()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        myViewModel.onResume()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context!!,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSubscriber() {
        myViewModel.getData().observe(this, { stores ->
            val newData = stores ?: emptyList()
            adapter.data = newData
        })
    }

    private fun setupViewModelObservers() {
        myViewModel.getProgressVisibility().observe(viewLifecycleOwner) { visibility ->
            binding.progressBar.visibility = visibility
        }
        myViewModel.getRecyclerViewVisibility().observe(viewLifecycleOwner) { visibility ->
            binding.recyclerView.visibility = visibility
        }
        myViewModel.getErrorButtonVisibility().observe(viewLifecycleOwner) { visibility ->
            binding.bottomView.visibility = visibility
        }
    }

    private fun setupClickListeners() {
        binding.reloadButton.setOnClickListener {
            myViewModel.onReload(it)
        }
        binding.closeButton.setOnClickListener {
            myViewModel.onClose(it)
        }
    }

    companion object {
        fun newInstance(): StoreListDialogFragment = StoreListDialogFragment()
    }
}
