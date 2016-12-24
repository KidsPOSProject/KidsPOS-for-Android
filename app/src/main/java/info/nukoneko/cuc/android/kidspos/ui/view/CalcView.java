package info.nukoneko.cuc.android.kidspos.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;
import info.nukoneko.cuc.android.kidspos.R;

/**
 * Created by TEJNEK on 2015/10/12.
 */
public class CalcView extends LinearLayout {

    OnItemClickListener mListener;

    @OnClick({
            R.id.calc_num_0, R.id.calc_num_1, R.id.calc_num_2, R.id.calc_num_3, R.id.calc_num_4,
            R.id.calc_num_5, R.id.calc_num_6, R.id.calc_num_7, R.id.calc_num_8, R.id.calc_num_9,
    }) void onClickNumber(Button numberButton){
        if (this.mListener == null) return;
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
        this.mListener.onClickNumber(number);
    }

    @OnClick(R.id.calc_num_C) void onClickClear(){
        if (this.mListener == null) return;
        this.mListener.onClickClear();
    }

    @OnClick(R.id.calc_num_end) void onClickEnd(){
        if (this.mListener == null) return;
        this.mListener.onClickEnd();
    }

    public CalcView(Context context) {
        this(context, null, 0);
    }

    public CalcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_calculator, this);
        ButterKnife.bind(this, view);
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
