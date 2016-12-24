package info.nukoneko.cuc.android.kidspos.ui.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ViewCalculatorBinding;

public class CalcView extends LinearLayout {

    OnItemClickListener mListener;

    private OnClickListener numberClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!(view instanceof Button)) return;
            if (mListener == null) return;
            final Button numberButton = (Button) view;
            int number = 0;
            switch (numberButton.getId()){
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
            }
            mListener.onClickNumber(number);
        }
    };

    private ViewCalculatorBinding binding;

    public CalcView(Context context) {
        this(context, null, 0);
    }

    public CalcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.view_calculator, this, false);

        binding.setButtonClickListener(numberClickListener);

        binding.calcNumC.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onClickClear();
            }
        });

        binding.calcNumEnd.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onClickEnd();
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemClickListener{
        void onClickNumber(int number);
        void onClickClear();
        void onClickEnd();
    }
}
