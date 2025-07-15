
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ErrorDialogFragment : DialogFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val message: String by lazyWithArgs(EXTRA_MESSAGE)

    enum class DialogResult {
        OK
    }

    private val resultFlow = MutableSharedFlow<DialogResult>(replay = 1)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                launch {
                    resultFlow.emit(DialogResult.OK)
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
            val fragment = ErrorDialogFragment().also {
                it.arguments = Bundle().apply {
                    putString(EXTRA_MESSAGE, message)
                }
            }
            fragment.show(fragmentManager, message)
            return fragment.resultFlow.first()
        }

    }
}
