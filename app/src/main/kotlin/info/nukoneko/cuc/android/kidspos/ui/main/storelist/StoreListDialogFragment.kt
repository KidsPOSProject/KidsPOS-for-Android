package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentStoreListDialogBinding
import info.nukoneko.cuc.android.kidspos.extensions.safetyObserve
import info.nukoneko.cuc.android.kidspos.ui.common.SafetyDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoreListDialogFragment : SafetyDialogFragment() {
    private lateinit var binding: FragmentStoreListDialogBinding
    private val myViewModel: StoreListViewModel by viewModel()

    private val adapterListener = object : StoreListViewAdapter.Listener {
        override fun onStoreSelect(store: info.nukoneko.cuc.android.kidspos.domain.entity.Store) {
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_store_list_dialog, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = myViewModel
        setupSubscriber()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        myViewModel.onResume()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSubscriber() {
        myViewModel.data.safetyObserve(this) { data ->
            adapter.data = data
        }
        myViewModel.presentErrorDialog.safetyObserve(this) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        myViewModel.shouldDismiss.safetyObserve(this) {
            dismiss()
        }
    }
}
