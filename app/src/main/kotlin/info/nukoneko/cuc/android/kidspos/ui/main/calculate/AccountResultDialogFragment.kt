@file:Suppress("EXPERIMENTAL_API_USAGE")

package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.FragmentAccountResultDialogBinding
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AccountResultDialogFragment : DialogFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    enum class DialogResult {
        OK,
        Cancel
    }

    private val channel = BroadcastChannel<DialogResult>(1)

    private lateinit var binding: FragmentAccountResultDialogBinding

    private val price: Int by lazyWithArgs(EXTRA_PRICE)
    private val receiveMoney: Int by lazyWithArgs(EXTRA_RECEIVE_MONEY)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountResultDialogBinding.inflate(inflater, container, false)
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
        binding.result4.findViewById<View>(R.id.ok).setOnClickListener {
            launch {
                channel.send(DialogResult.OK)
                dialog?.dismiss()
            }
        }
        binding.result4.findViewById<View>(R.id.go_back).setOnClickListener {
            launch {
                channel.send(DialogResult.Cancel)
                dialog?.dismiss()
            }
        }
        binding.resultSum.findViewById<TextView>(R.id.sum_river).text = "$price リバー"
        binding.result2.findViewById<TextView>(R.id.receive_river).text = "$receiveMoney リバー"
        binding.result3.findViewById<TextView>(R.id.change_river).text =
            "${receiveMoney - price} リバー"
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
