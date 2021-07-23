package info.nukoneko.cuc.android.kidspos.ui.common

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

open class SafetyDialogFragment : DialogFragment() {
    fun safetyShow(manager: FragmentManager, tag: String = this::class.java.simpleName) {
        val sameTagFragment = manager.findFragmentByTag(tag)
        if (sameTagFragment is DialogFragment) {
            if (sameTagFragment.dialog?.isShowing == true) {
                return
            }
        }
        super.showNow(manager, tag)
    }
}
