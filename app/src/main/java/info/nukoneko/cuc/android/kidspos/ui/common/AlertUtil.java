package info.nukoneko.cuc.android.kidspos.ui.common;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import info.nukoneko.cuc.android.kidspos.KPApplicationController;

public final class AlertUtil {
    private AlertUtil(){}

    public static void showAlert(@NonNull Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null);
        if (!TextUtils.isEmpty(title)) builder.setTitle(title);
        if (!TextUtils.isEmpty(message)) builder.setMessage(message);
        builder.show();
    }

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
        throwable.printStackTrace();
        if (KPApplicationController.get(context).isDebugModeEnabled()) {
            showErrorDialog(context, throwable.getLocalizedMessage(), true, onClickListener);
        } else {
            showErrorDialog(context, "エラーが発生しました", true, onClickListener);
        }
    }
}
