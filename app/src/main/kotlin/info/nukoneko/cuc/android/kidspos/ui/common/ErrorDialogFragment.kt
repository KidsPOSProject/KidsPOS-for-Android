@file:Suppress("EXPERIMENTAL_API_USAGE")

package info.nukoneko.cuc.android.kidspos.ui.common

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.extensions.lazyWithArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ErrorDialogFragment : DialogFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val message: String by lazyWithArgs(EXTRA_MESSAGE)

    enum class DialogResult {
        OK
    }

    private val channel = BroadcastChannel<DialogResult>(1)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                launch {
                    channel.send(DialogResult.OK)
                }
            }
            .setCancelable(false)
            .create().also {
                it.setCancelable(false)
                it.setCanceledOnTouchOutside(false)
            }
    }

    companion object {
        private const val EXTRA_MESSAGE: String = "message"

        suspend fun showWithSuspend(
            fragmentManager: FragmentManager,
            message: String
        ): DialogResult {
            // Fragment表示前に状態をチェック
            if (fragmentManager.isStateSaved) {
                // 状態が保存済みの場合は表示をスキップ
                return DialogResult.OK
            }

            val fragment = ErrorDialogFragment().also {
                it.arguments = Bundle().apply {
                    putString(EXTRA_MESSAGE, message)
                }
            }

            try {
                fragment.show(fragmentManager, message)
                return fragment.channel.openSubscription().receive()
            } catch (e: IllegalStateException) {
                // 万が一エラーが発生した場合は無視
                return DialogResult.OK
            }
        }

    }
}
