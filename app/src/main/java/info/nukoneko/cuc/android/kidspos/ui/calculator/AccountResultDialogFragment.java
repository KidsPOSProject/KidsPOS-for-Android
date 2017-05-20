package info.nukoneko.cuc.android.kidspos.ui.calculator;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogAccountResultBinding;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment;

public final class AccountResultDialogFragment extends BaseDialogFragment {
    private final static String EXTRA_DIALOG_TITLE = "dialog_title";
    private final static String EXTRA_PRICE = "price";
    private final static String EXTRA_RECEIVE_MONEY = "receive_money";

    public interface Listener {
        void onClickPositiveButton(Dialog dialog);
        void onClickNegativeButton(Dialog dialog);
    }

    public static AccountResultDialogFragment newInstance(@StringRes int titleId, int price, int receive) {
        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_DIALOG_TITLE, titleId);
        bundle.putInt(EXTRA_PRICE, price);
        bundle.putInt(EXTRA_RECEIVE_MONEY, receive);

        AccountResultDialogFragment alertView = new AccountResultDialogFragment();
        alertView.setArguments(bundle);
        return alertView;
    }

    @NonNull
    private String getTitle() {
        return getResources().getString(getArguments().getInt(EXTRA_DIALOG_TITLE));
    }

    private int getPrice() {
        return getArguments().getInt(EXTRA_PRICE);
    }

    private int getReceiveMoney() {
        return getArguments().getInt(EXTRA_RECEIVE_MONEY);
    }

    private FragmentDialogAccountResultBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_dialog_account_result, container, false);
        final AccountResultDialogFragmentViewModel mViewModel = new AccountResultDialogFragmentViewModel(getTitle(), getPrice(), getReceiveMoney());
        mBinding.setViewModel(mViewModel);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof Listener) {
                    ((Listener) getContext()).onClickPositiveButton(getDialog());
                } else {
                    dismiss();
                }
            }
        });

        mBinding.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof Listener) {
                    ((Listener) getContext()).onClickNegativeButton(getDialog());
                } else {
                    dismiss();
                }
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
}
