package info.nukoneko.cuc.android.kidspos.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import info.nukoneko.cuc.android.kidspos.R;

/**
 * Created by TEJNEK on 2015/10/12.
 */
public class YesNoDialog extends DialogFragment {
    final static String ARG_TITLE = "ARG_TITLE";

    final static String ARG_PRICE = "ARG_PRICE";

    final static String ARG_RECEIVE = "ARG_RECEIVE";

    SimpleDialogInterface positive = null;
    SimpleDialogInterface negative = null;

    public static YesNoDialog newInstance(
            @StringRes int titleID,
            int price, int receive) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TITLE, titleID);
        bundle.putInt(ARG_PRICE, price);
        bundle.putInt(ARG_RECEIVE, receive);
        return newInstance(bundle);
    }

    public YesNoDialog setPositiveInterface(DialogOnClickListener listener){
        this.positive = new SimpleDialogInterface(listener);
        return this;
    }

    public YesNoDialog setNegativeInterface(DialogOnClickListener listener){
        this.negative = new SimpleDialogInterface(listener);
        return this;
    }

    private boolean hasListener(){
        return this.positive != null || this.negative != null;
    }

    private static YesNoDialog newInstance(Bundle arguments) {
        YesNoDialog alertView = new YesNoDialog();
        alertView.setArguments(arguments);
        return alertView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Integer titleID = bundle.getInt(ARG_TITLE);

        Integer price = bundle.getInt(ARG_PRICE, -1);
        Integer receive = bundle.getInt(ARG_RECEIVE, -1);

        final String title = getActivity().getResources().getString(titleID);

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = null;
        try {
            view = getActivity().getLayoutInflater().inflate(R.layout.dialog_yesno, null, false);
        } catch (Exception ignore) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
        }

        assert view != null;

        TextView vTitle = (TextView) view.findViewById(R.id.title);
        TextView vPriceView = (TextView) view.findViewById(R.id.price_value);
        TextView vReceiveView = (TextView) view.findViewById(R.id.receive_value);
        LinearLayout button = (LinearLayout) view.findViewById(R.id.buttonArea);
        Button bPositive = (Button) view.findViewById(R.id.yes);
        Button bNegative = (Button) view.findViewById(R.id.no);

        vTitle.setText(title);

        vPriceView.setText(String.valueOf(price) + "円");
        vReceiveView.setText(String.valueOf(receive) + "円");

        if (hasListener()) {
            button.setVisibility(View.VISIBLE);
            if (this.positive != null) {
                bPositive.setVisibility(View.VISIBLE);
                if(positive.getListener() == null){
                    bPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                        }
                    });
                }else {
                    bPositive.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) { positive.getListener().onClick(dialog);}
                    });
                }
            }
            if (this.negative != null) {
                bNegative.setVisibility(View.VISIBLE);
                if(negative.getListener() == null){
                    bNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {dismiss();}
                    });
                }else {
                    bNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { negative.getListener().onClick(dialog);}
                    });
                }
            }
        }
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    private class SimpleDialogInterface{
        DialogOnClickListener listener;
        public SimpleDialogInterface(DialogOnClickListener c){
            this.listener = c;
        }
        public DialogOnClickListener  getListener(){
            return listener;
        }
    }

    public interface DialogOnClickListener {
        void onClick(Dialog dialog);
    }
}
