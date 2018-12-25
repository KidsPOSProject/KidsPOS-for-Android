package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogStoreListBinding
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment

class StoreListDialogFragment : BaseDialogFragment() {
    private lateinit var binding: FragmentDialogStoreListBinding

    private val listener = object : StoreListViewModel.Listener {
        override fun onError(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private val viewModel: StoreListViewModel by lazy {
        ViewModelProviders.of(this).get(StoreListViewModel::class.java).also {
            it.listener = listener
        }
    }

    private val adapterListener = object : StoreListViewAdapter.Listener {
        override fun onStoreSelect(store: Store) {
            context?.let {
                KidsPOSApplication[it]?.setStore(store)
            }

            dismiss()
        }
    }

    private val adapter: StoreListViewAdapter by lazy {
        StoreListViewAdapter().also {
            it.listener = adapterListener
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_store_list, null, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        val dialog = AlertDialog.Builder(context!!)
                .setTitle("おみせの変更")
                .setView(binding.root)
                .create()

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSubscriber()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSubscriber() {
        viewModel.getData().observe(this, Observer<List<Store>> { stores ->
            val newData = stores ?: emptyList()
            adapter.data = newData
        })
    }

    companion object {
        fun newInstance(): StoreListDialogFragment = StoreListDialogFragment()
    }
}
