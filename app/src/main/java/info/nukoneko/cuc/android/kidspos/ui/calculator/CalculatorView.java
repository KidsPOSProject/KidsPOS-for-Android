package info.nukoneko.cuc.android.kidspos.ui.calculator;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.Gravity;

import info.nukoneko.cuc.android.kidspos.R;


public final class CalculatorView extends AppCompatButton {
    private final int mNumber;

    public CalculatorView(Context context) {
        this(context, null, 0);
    }

    public CalculatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalculatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CalculatorView, defStyleAttr, defStyleAttr);
        mNumber = typedArray.getInteger(R.styleable.CalculatorView_number, -1);
        typedArray.recycle();

        setGravity(Gravity.CENTER);
        setText(String.valueOf(mNumber));
    }

    public int getNumber() {
        return mNumber;
    }
}
