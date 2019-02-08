package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentCalculatorDialogBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import org.koin.android.viewmodel.ext.android.viewModel

class CalculatorDialogFragment : DialogFragment(), AccountResultDialogFragment.Listener {
    private val totalPrice: Int by lazyWithArgs(EXTRA_SUM_RIVER)
    private val items: List<Item> by lazyWithArgs(EXTRA_SALE_ITEMS)

    private lateinit var binding: FragmentCalculatorDialogBinding
    private val myViewModel: CalculatorDialogViewModel by viewModel()
    private val listener = object : CalculatorDialogViewModel.Listener {
        override fun onShouldShowResultDialog(totalPrice: Int, deposit: Int) {
            AccountResultDialogFragment.newInstance(totalPrice, deposit).also {
                it.isCancelable = false
            }.show(childFragmentManager, "yesNoDialog")
        }

        override fun onShouldShowErrorMessage(message: String) {
            Toast.makeText(context!!, message, Toast.LENGTH_SHORT).show()
        }

        override fun onDismiss() {
            dismiss()
        }
    }

    override fun onAccount() {
        myViewModel.onAccount()
    }

    override fun onAccountResultDialogBack() {

    }

    private val calculatorListener = object : CalculatorLayout.Listener {
        override fun onNumberClick(number: Int) {
            myViewModel.onNumberClick(number)
        }

        override fun onClearClick() {
            myViewModel.onClearClick()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calculator_dialog, container, false)
        binding.setLifecycleOwner(this)
        binding.calculatorLayout.listener = calculatorListener
        myViewModel.listener = listener
        myViewModel.setup(items, totalPrice)
        binding.viewModel = myViewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    companion object {
        private const val EXTRA_SUM_RIVER = "sum_price"
        private const val EXTRA_SALE_ITEMS = "sales_model"

        fun newInstance(saleItems: ArrayList<Item>) = CalculatorDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_SUM_RIVER, saleItems.sumBy { it.price })
                putParcelableArrayList(EXTRA_SALE_ITEMS, saleItems)
            }
        }
    }
}
