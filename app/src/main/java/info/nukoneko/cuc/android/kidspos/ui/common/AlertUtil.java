package info.nukoneko.cuc.android.kidspos.ui.common;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;

public final class AlertUtil {
    private AlertUtil(){}

    public static void showErrorDialog(@NonNull Context context,
                                       String message, boolean cancelable,
                                       DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setCancelable(cancelable)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .show();
    }

    public static void showErrorDialog(@NonNull Context context, Throwable throwable,
                                       DialogInterface.OnClickListener onClickListener) {
        showErrorDialog(context, "エラーが発生しました", false, onClickListener);
        KidsPOSApplication.get(context).sendErrorReport(throwable.getLocalizedMessage());
    }
}
