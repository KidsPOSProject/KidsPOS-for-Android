package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import androidx.databinding.DataBindingUtil
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentAccountResultDialogBinding
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class AccountResultDialogFragment : DialogFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    enum class DialogResult {
        OK,
        Cancel
    }

    private val channel = BroadcastChannel<DialogResult>(1)

    private val listener = object : AccountResultDialogViewModel.Listener {
        override fun onOk() {
            launch {
                channel.send(DialogResult.OK)
                dialog.dismiss()
            }
        }

        override fun onCancel() {
            launch {
                channel.send(DialogResult.Cancel)
                dialog.dismiss()
            }
        }
    }

    private lateinit var binding: FragmentAccountResultDialogBinding

    private val myViewModel: AccountResultDialogViewModel by viewModel()

    private val price: Int by lazyWithArgs(EXTRA_PRICE)
    private val receiveMoney: Int by lazyWithArgs(EXTRA_RECEIVE_MONEY)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_result_dialog, container, false)
        myViewModel.listener = listener
        myViewModel.setup(price, receiveMoney)
        binding.viewModel = myViewModel
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

    suspend fun showAndSuspend(fm: FragmentManager, tag: String? = null): DialogResult {
        show(fm, tag)
        return channel.openSubscription().receive()
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
