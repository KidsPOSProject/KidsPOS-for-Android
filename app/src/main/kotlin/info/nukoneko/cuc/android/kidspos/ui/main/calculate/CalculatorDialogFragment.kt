package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentCalculatorBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import org.koin.android.viewmodel.ext.android.viewModel

class CalculatorDialogFragment : DialogFragment(), AccountResultDialogFragment.Listener {
    override fun onAccount() {
        myViewModel.sendToServer(items, totalPrice, deposit)
    }

    override fun onAccountResultDialogBack() {

    }

    private val listener = object : CalculatorDialogViewModel.Listener {
        override fun showMessage(message: String) {
            Toast.makeText(context!!, message, Toast.LENGTH_SHORT).show()
        }

        override fun onDismiss() {
            resultDialogFragment?.dialog?.cancel()
            dismiss()
        }
    }

    private var deposit = 0
    private lateinit var binding: FragmentCalculatorBinding

    // TODO: coroutine + channel へ乗り換え
    private var resultDialogFragment: AccountResultDialogFragment? = null

    private val totalPrice: Int by lazyWithArgs(EXTRA_SUM_RIVER)
    private val items: List<Item> by lazyWithArgs(EXTRA_SALE_ITEMS)

    private val myViewModel: CalculatorDialogViewModel by viewModel()

    private val isValid: Boolean
        get() = totalPrice <= this.deposit

    private val calculatorListener = object : CalculatorLayout.Listener {
        override fun onNumberClick(number: Int) {
            if (deposit > 100000) return

            deposit = if (deposit == 0) {
                number
            } else {
                deposit * 10 + number
            }
            binding.receiveRiver.setText(deposit.toString())
        }

        override fun onClear(view: View) {
            if (10 > deposit) {
                deposit = 0
            } else {
                deposit = Math.floor((deposit / 10).toDouble()).toInt()
            }
            binding.receiveRiver.setText(deposit.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_calculator, container, true)
        binding.calculatorLayout.listener = calculatorListener
        myViewModel.listener = listener
        binding.viewModel = myViewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog.setCancelable(false)

        val dialogWindow = dialog.window
        dialogWindow?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        binding.sumRiver.text = totalPrice.toString()
        binding.done.setOnClickListener(View.OnClickListener {
            if (!isValid) return@OnClickListener

            resultDialogFragment = AccountResultDialogFragment.newInstance(totalPrice, deposit)
            resultDialogFragment!!.isCancelable = false
            resultDialogFragment!!.show(childFragmentManager, "yesNoDialog")
        })
        binding.back.setOnClickListener { dismiss() }
    }

    companion object {
        private const val EXTRA_SUM_RIVER = "sum_price"
        private const val EXTRA_SALE_ITEMS = "sales_model"

        fun newInstance(saleItems: List<Item>) = CalculatorDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_SUM_RIVER, saleItems.sumBy { it.price })
                putParcelableArray(EXTRA_SALE_ITEMS, saleItems.toTypedArray())
            }
        }
    }
}
