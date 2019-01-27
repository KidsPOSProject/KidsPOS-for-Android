package info.nukoneko.cuc.android.kidspos.util

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

object AlertUtil {
    fun showErrorDialog(context: Context,
                        message: String, cancelable: Boolean,
                        onClickListener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(context)
                .setCancelable(cancelable)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .show()
    }
}
