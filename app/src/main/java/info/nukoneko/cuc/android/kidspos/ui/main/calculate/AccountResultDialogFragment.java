package info.nukoneko.cuc.android.kidspos.ui.main.calculate;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.FragmentDialogAccountResultBinding;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseDialogFragment;

public final class AccountResultDialogFragment extends BaseDialogFragment {
    private final static String EXTRA_PRICE = "price";
    private final static String EXTRA_RECEIVE_MONEY = "receive_money";
    private Listener mListener;
    private FragmentDialogAccountResultBinding mBinding;

    public static AccountResultDialogFragment newInstance(int price, int receive) {
        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_PRICE, price);
        bundle.putInt(EXTRA_RECEIVE_MONEY, receive);

        AccountResultDialogFragment alertView = new AccountResultDialogFragment();
        alertView.setArguments(bundle);
        return alertView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_dialog_account_result, container, false);
        final AccountResultDialogFragmentViewModel mViewModel = new AccountResultDialogFragmentViewModel(getPrice(), getReceiveMoney());
        mBinding.setViewModel(mViewModel);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().setCancelable(false);

        final Window dialogWindow = getDialog().getWindow();
        if (dialogWindow != null) {
            dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        mBinding.yes.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onClickPositiveButton(getDialog());
            } else {
                dismiss();
            }
        });

        mBinding.no.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onClickNegativeButton(getDialog());
            } else {
                dismiss();
            }
        });
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private int getPrice() {
        return getArguments().getInt(EXTRA_PRICE);
    }

    private int getReceiveMoney() {
        return getArguments().getInt(EXTRA_RECEIVE_MONEY);
    }

    public interface Listener {
        void onClickPositiveButton(Dialog dialog);

        void onClickNegativeButton(Dialog dialog);
    }
}
