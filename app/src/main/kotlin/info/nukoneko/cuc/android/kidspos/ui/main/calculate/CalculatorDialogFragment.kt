package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentCalculatorDialogBinding
import info.nukoneko.cuc.android.kidspos.domain.entity.Item
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import info.nukoneko.cuc.android.kidspos.ui.common.SafetyDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalculatorDialogFragment : SafetyDialogFragment() {
    private val totalPrice: Int by lazyWithArgs(EXTRA_SUM_RIVER)
    private val items: List<Item> by lazyWithArgs(EXTRA_SALE_ITEMS)

    private lateinit var binding: FragmentCalculatorDialogBinding
    private val myViewModel: CalculatorDialogViewModel by viewModel()
    private val listener = object : CalculatorDialogViewModel.Listener {
        override fun onShouldShowResultDialog(totalPrice: Int, deposit: Int) {
            lifecycleScope.launch(Dispatchers.Main) {
                when (AccountResultDialogFragment.show(
                    parentFragmentManager,
                    totalPrice,
                    deposit
                )) {
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

    private val calculatorListener = object : CalculatorLayout.Listener {
        override fun onNumberClick(number: Int) {
            myViewModel.onNumberClick(number)
        }

        override fun onClearClick() {
            myViewModel.onClearClick()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_calculator_dialog, container, false)
        binding.lifecycleOwner = this
        binding.calculatorLayout.listener = calculatorListener
        myViewModel.listener = listener
        myViewModel.setup(items, totalPrice)
        binding.viewModel = myViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    companion object {
        private const val EXTRA_SUM_RIVER = "sum_price"
        private const val EXTRA_SALE_ITEMS = "sales_model"

        fun show(fragmentManager: FragmentManager, saleItems: ArrayList<Item>) =
            CalculatorDialogFragment().apply {
                arguments = bundleOf(
                    EXTRA_SUM_RIVER to saleItems.sumOf { it.price.value },
                    EXTRA_SALE_ITEMS to saleItems
                )
            }.safetyShow(fragmentManager)
    }
}
