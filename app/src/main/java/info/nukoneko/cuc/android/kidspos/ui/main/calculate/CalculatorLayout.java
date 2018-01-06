package info.nukoneko.cuc.android.kidspos.ui.main.calculate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ViewCalculatorBinding;

public final class CalculatorLayout extends LinearLayout {
    @Nullable
    private Listener mListener = null;

    public CalculatorLayout(Context context) {
        this(context, null, 0);
    }

    public CalculatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalculatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final ViewCalculatorBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_calculator, this, true);
        binding.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    if (view instanceof CalculatorView) {
                        mListener.onClickNumber(((CalculatorView) view).getNumber());
                    } else if (view.getId() == R.id.calc_num_C) {
                        mListener.onClickClear(view);
                    }
                }
            }
        });
    }

    public void setOnCalculatorClickListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onClickNumber(int number);

        void onClickClear(View view);
    }
}
