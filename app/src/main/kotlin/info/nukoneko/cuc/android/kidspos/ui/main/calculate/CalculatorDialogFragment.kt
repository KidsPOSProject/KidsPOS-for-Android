package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentCalculatorDialogBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class CalculatorDialogFragment : DialogFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val totalPrice: Int by lazyWithArgs(EXTRA_SUM_RIVER)
    private val items: List<Item> by lazyWithArgs(EXTRA_SALE_ITEMS)

    private lateinit var binding: FragmentCalculatorDialogBinding
    private val myViewModel: CalculatorDialogViewModel by viewModel()
    private val listener = object : CalculatorDialogViewModel.Listener {
        override fun onShouldShowResultDialog(totalPrice: Int, deposit: Int) {
            launch {
                val result = AccountResultDialogFragment.newInstance(totalPrice, deposit).also {
                    it.isCancelable = false
                }.showAndSuspend(parentFragmentManager, "yesNoDialog")
                when (result) {
                    AccountResultDialogFragment.DialogResult.OK -> {
                        myViewModel.onOk()
                    }

                    else -> {
                    }
                }
            }
        }

        override fun onShouldShowErrorMessage(message: String) {
            Toast.makeText(context!!, message, Toast.LENGTH_SHORT).show()
        }

        override fun onDismiss() {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorDialogBinding.inflate(inflater, container, false)
        setupNumberPanel()
        myViewModel.listener = listener
        myViewModel.setup(items, totalPrice)
        setupViewModelObservers()
        setupClickListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun setupNumberPanel() {
        val numberIds = listOf(
            R.id.number_0,
            R.id.number_1,
            R.id.number_2,
            R.id.number_3,
            R.id.number_4,
            R.id.number_5,
            R.id.number_6,
            R.id.number_7,
            R.id.number_8,
            R.id.number_9
        )
        numberIds.forEach {
            binding.calculatorLayout.root.findViewById<View>(it).setOnClickListener { v ->
                myViewModel.onNumberClick("${(v as TextView).text}".toInt())
            }
        }
        binding.calculatorLayout.delete.setOnClickListener { myViewModel.onClearClick() }
    }

    private fun setupViewModelObservers() {
        myViewModel.getTotalPriceText().observe(viewLifecycleOwner) { price ->
            binding.sumRiver.text = price
        }
        myViewModel.getDepositText().observe(viewLifecycleOwner) { deposit ->
            binding.receiveRiver.text = deposit
        }
        myViewModel.getAccountButtonEnabled().observe(viewLifecycleOwner) { enabled ->
            binding.done.isEnabled = enabled
            binding.done.isClickable = enabled
        }
    }

    private fun setupClickListeners() {
        binding.back.setOnClickListener {
            myViewModel.onCancelClick(it)
        }
        binding.done.setOnClickListener {
            myViewModel.onDoneClick(it)
        }
    }

    companion object {
        private const val EXTRA_SUM_RIVER = "sum_price"
        private const val EXTRA_SALE_ITEMS = "sales_model"

        fun newInstance(saleItems: ArrayList<Item>) = CalculatorDialogFragment().apply {
            val totalRiver = saleItems.sumOf { it.price }
            arguments = Bundle().apply {
                putInt(EXTRA_SUM_RIVER, totalRiver)
                putParcelableArrayList(EXTRA_SALE_ITEMS, saleItems)
            }
        }
    }
}
