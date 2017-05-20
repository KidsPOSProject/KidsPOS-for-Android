package info.nukoneko.cuc.android.kidspos.ui.calculator;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ViewCalculatorBinding;

public final class CalculatorLayout extends LinearLayout {
    public interface Listener {
        void onClickNumber(int number);

        void onClickClear(View view);

        void onClickEnd(View view);
    }

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
        if (context instanceof Listener) {
            final Listener listener = (Listener) context;
            binding.setListener(listener);
            binding.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view instanceof CalculatorView) {
                        listener.onClickNumber(((CalculatorView) view).getNumber());
                    }
                }
            });
        }
    }
}
