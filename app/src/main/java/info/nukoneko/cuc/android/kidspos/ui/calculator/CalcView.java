package info.nukoneko.cuc.android.kidspos.ui.calculator;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ViewCalculatorBinding;

public class CalcView extends LinearLayout {
    public interface Listener {
        void onClickNumber(int number);
        void onClickClear();
        void onClickEnd();
    }

    private OnClickListener mOnValueClickListener = view -> {
        // 応急処置
        @IdRes final int id = view.getId();
        final int number;
        switch (id) {
            case R.id.calc_num_0:
                number = 0;
                break;
            case R.id.calc_num_1:
                number = 1;
                break;
            case R.id.calc_num_2:
                number = 2;
                break;
            case R.id.calc_num_3:
                number = 3;
                break;
            case R.id.calc_num_4:
                number = 4;
                break;
            case R.id.calc_num_5:
                number = 5;
                break;
            case R.id.calc_num_6:
                number = 6;
                break;
            case R.id.calc_num_7:
                number = 7;
                break;
            case R.id.calc_num_8:
                number = 8;
                break;
            case R.id.calc_num_9:
                number = 9;
                break;
            default:
                return;
        }
        if (getContext() instanceof Listener) {
            ((Listener) getContext()).onClickNumber(number);
        }
    };

    public CalcView(Context context) {
        this(context, null, 0);
    }

    public CalcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final ViewCalculatorBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_calculator, this, true);
        binding.setListener(mOnValueClickListener);
        binding.calcNumC.setOnClickListener(view -> {
            if (context instanceof Listener) {
                ((Listener) context).onClickClear();
            }
        });

        binding.calcNumEnd.setOnClickListener(view -> {
            if (context instanceof Listener) {
                ((Listener) context).onClickEnd();
            }
        });
    }
}
