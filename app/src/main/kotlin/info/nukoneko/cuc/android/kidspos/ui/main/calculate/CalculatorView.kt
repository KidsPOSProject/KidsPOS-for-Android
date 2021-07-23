package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import info.nukoneko.cuc.android.kidspos.R

class CalculatorView : AppCompatButton {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet? = null) : this(context, attr, 0)
    constructor(
        context: Context,
        attr: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attr, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(
            attr,
            R.styleable.CalculatorView,
            defStyleAttr,
            defStyleAttr
        )
        number = typedArray.getInteger(R.styleable.CalculatorView_number, -1)
        typedArray.recycle()
        gravity = Gravity.CENTER
        text = number.toString()
    }

    val number: Int
}
