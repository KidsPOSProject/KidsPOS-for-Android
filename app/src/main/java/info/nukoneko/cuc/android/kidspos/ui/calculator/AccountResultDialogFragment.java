package info.nukoneko.cuc.android.kidspos.ui.calculator;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.Locale;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogAccountResultBinding;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment;

public class AccountResultDialogFragment extends BaseDialogFragment {
    public interface Listener {
        void onClickPositiveButton(Dialog dialog);
        void onClickNegativeButton(Dialog dialog);
    }

    private final static String ARG_TITLE = "ARG_TITLE";
    private final static String ARG_PRICE = "ARG_PRICE";
    private final static String ARG_RECEIVE = "ARG_RECEIVE";

    private String mTitle;
    private int mPrice;
    private int mReceive;

    public static AccountResultDialogFragment newInstance(@StringRes int titleId, int price, int receive) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TITLE, titleId);
        bundle.putInt(ARG_PRICE, price);
        bundle.putInt(ARG_RECEIVE, receive);

        AccountResultDialogFragment alertView = new AccountResultDialogFragment();
        alertView.setArguments(bundle);
        return alertView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();
        mTitle = getActivity().getResources().getString(bundle.getInt(ARG_TITLE));
        mPrice = bundle.getInt(ARG_PRICE);
        mReceive = bundle.getInt(ARG_RECEIVE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_account_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentDialogAccountResultBinding mBinding = FragmentDialogAccountResultBinding.bind(view);
        mBinding.title.setText(mTitle);
        mBinding.priceValue.setText(String.format(Locale.getDefault(), "%d リバー", mPrice));
        mBinding.receiveValue.setText(String.format(Locale.getDefault(), "%d リバー", mReceive));
        mBinding.resultValue.setText(String.format(Locale.getDefault(), "%d リバー", mReceive - mPrice));

        mBinding.yes.setOnClickListener(v -> {
           if (getListener() == null) {
               dismiss();
           } else {
               getListener().onClickPositiveButton(getDialog());
           }
        });

        mBinding.no.setOnClickListener(v -> {
            if (getListener() == null) {
                dismiss();
            } else {
                getListener().onClickNegativeButton(getDialog());
            }
        });

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    private Listener getListener() {
        return (getContext() instanceof Listener) ? (Listener) getContext() : null;
    }
}
