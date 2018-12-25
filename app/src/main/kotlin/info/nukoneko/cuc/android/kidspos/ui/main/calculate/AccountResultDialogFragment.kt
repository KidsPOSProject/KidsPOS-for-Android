package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogAccountResultBinding
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment

class AccountResultDialogFragment : BaseDialogFragment() {
    private var listener: Listener? = null

    private lateinit var binding: FragmentDialogAccountResultBinding

    private val viewModel: AccountResultDialogViewModel by lazy {
        ViewModelProviders.of(this)[AccountResultDialogViewModel::class.java].also {
            it.listener = object: AccountResultDialogViewModel.Listener {
                override fun onAccount() {
                    listener?.onAccount()
                    dialog.dismiss()
                }

                override fun onBack() {
                    listener?.onAccountResultDialogBack()
                    dialog.dismiss()
                }
            }
        }
    }

    private val price: Int by lazyWithArgs(EXTRA_PRICE)

    private val receiveMoney: Int by lazyWithArgs(EXTRA_RECEIVE_MONEY)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as? Listener
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_dialog_account_result, container, false)
        binding.viewModel = viewModel.apply {
            setupValue(price, receiveMoney)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog.setCancelable(false)

        val dialogWindow = dialog.window
        if (dialogWindow != null) {
            dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialogWindow.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }
    }

    interface Listener {
        fun onAccount()

        fun onAccountResultDialogBack()
    }

    companion object {
        private const val EXTRA_PRICE = "price"
        private const val EXTRA_RECEIVE_MONEY = "receive_money"

        fun newInstance(price: Int, receive: Int) = AccountResultDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_PRICE, price)
                putInt(EXTRA_RECEIVE_MONEY, receive)
            }
        }
    }
}
