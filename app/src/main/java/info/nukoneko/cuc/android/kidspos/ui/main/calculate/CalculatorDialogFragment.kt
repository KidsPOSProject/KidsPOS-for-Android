package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.app.Dialog
import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentCalculatorBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers

class CalculatorDialogFragment : BaseDialogFragment(), AccountResultDialogFragment.Listener {

    private var deposit = 0
    private lateinit var binding: FragmentCalculatorBinding

    // TODO: coroutine + channel へ乗り換え
    private var resultDialogFragment: AccountResultDialogFragment? = null

    private val totalPrice: Int by lazyWithArgs(EXTRA_SUM_RIVER)
    private val items: List<Item> by lazyWithArgs(EXTRA_SALE_ITEMS)

    private val viewModel: CalculatorDialogViewModel by lazy {
        ViewModelProviders.of(this)[CalculatorDialogViewModel::class.java]
    }

    private val isValid: Boolean
        get() = totalPrice <= this.deposit

    private val calculatorListener = object: CalculatorLayout.Listener {
        override fun onNumberClick(number: Int) {
            if (deposit > 100000) return

            if (deposit == 0) {
                deposit = number
            } else {
                deposit = deposit * 10 + number
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
        binding.viewModel = viewModel
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
            resultDialogFragment!!.setListener(this@CalculatorDialogFragment)
            resultDialogFragment!!.show(childFragmentManager, "yesNoDialog")
        })
        binding.back.setOnClickListener { dismiss() }
    }

    override fun onClickPositiveButton(dialog: Dialog) {
        sendToServer()
    }

    override fun onClickNegativeButton(dialog: Dialog) {
        dialog.cancel()
    }

    private fun finishFragment() {
        val app = KidsPOSApplication[context] ?: return
        resultDialogFragment?.dialog?.cancel()
        app.postEvent(SystemEvent.SentSaleSuccess)
        dismiss()
    }

    private fun sendToServer() {
        val app = KidsPOSApplication[context] ?: return

        var sum = StringBuilder()
        for ((id) in items) {
            sum.append(id.toString()).append(",")
        }
        sum = StringBuilder(sum.substring(0, sum.length - 1))
        val staff = app.storeManager.lastStaff
        val store = app.storeManager.lastStore
        val staffBarcode = staff?.barcode ?: ""
        val storeId = store?.id ?: 0

        if (app.isPracticeModeEnabled) {
            Toast.makeText(context, "練習モードのためレシートは出ません", Toast.LENGTH_SHORT).show()
            finishFragment()
        } else {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("送信しています")
            progressDialog.show()

            app.saleManager.createSale(deposit, items.size, totalPrice, sum.toString(), storeId, staffBarcode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(app.getDefaultSubscribeScheduler())
                    .subscribe({ (id, barcode, createdAt, points, price, items, storeId1, staffId) ->
                        progressDialog.dismiss()
                        finishFragment()
                    }, { throwable ->
                        throwable.printStackTrace()
                        progressDialog.dismiss()
                        finishFragment()
                    })
        }
    }

    companion object {
        private const val EXTRA_SUM_RIVER = "sum_price"
        private const val EXTRA_SALE_ITEMS = "sales_model"

        fun newInstance(sumRiver: Int, saleItems: List<Item>) = CalculatorDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_SUM_RIVER, sumRiver)
                putParcelableArray(EXTRA_SALE_ITEMS, saleItems.toTypedArray())
            }
        }
    }
}
