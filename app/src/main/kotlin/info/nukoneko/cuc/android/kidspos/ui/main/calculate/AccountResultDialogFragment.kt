@file:Suppress("EXPERIMENTAL_API_USAGE")

package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentAccountResultDialogBinding
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import info.nukoneko.cuc.android.kidspos.ui.common.SafetyDialogFragment
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountResultDialogFragment : SafetyDialogFragment() {
    enum class DialogResult {
        OK,
        Cancel
    }

    private val channel = BroadcastChannel<DialogResult>(1)

    private val listener = object : AccountResultDialogViewModel.Listener {
        override fun onOk() {
            lifecycleScope.launch {
                channel.send(DialogResult.OK)
                dialog?.dismiss()
            }
        }

        override fun onCancel() {
            lifecycleScope.launch {
                channel.send(DialogResult.Cancel)
                dialog?.dismiss()
            }
        }
    }

    private lateinit var binding: FragmentAccountResultDialogBinding

    private val myViewModel: AccountResultDialogViewModel by viewModel()

    private val price: Int by lazyWithArgs(EXTRA_PRICE)
    private val receiveMoney: Int by lazyWithArgs(EXTRA_RECEIVE_MONEY)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account_result_dialog,
            container,
            false
        )
        myViewModel.listener = listener
        myViewModel.setup(price, receiveMoney)
        binding.viewModel = myViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        val dialogWindow = dialog?.window
        if (dialogWindow != null) {
            dialogWindow.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialogWindow.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }
    }

    private suspend fun showAndSuspend(fm: FragmentManager): DialogResult {
        safetyShow(fm)
        return channel.openSubscription().receive()
    }

    companion object {
        private const val EXTRA_PRICE = "price"
        private const val EXTRA_RECEIVE_MONEY = "receive_money"

        suspend fun show(fragmentManager: FragmentManager, price: Int, receive: Int) =
            AccountResultDialogFragment().apply {
                arguments = bundleOf(
                    EXTRA_PRICE to price,
                    EXTRA_RECEIVE_MONEY to receive
                )
                isCancelable = false
            }.showAndSuspend(fragmentManager)
    }
}
