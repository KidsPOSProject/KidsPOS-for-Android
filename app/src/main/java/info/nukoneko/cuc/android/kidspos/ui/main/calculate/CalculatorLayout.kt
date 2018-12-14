package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import info.nukoneko.cuc.android.kidspos.R

class CalculatorLayout: LinearLayout {
    constructor(context: Context): super(context)
    constructor(context: Context, attr: AttributeSet? = null): super(context, attr)
    constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0): super(context, attr, defStyleAttr)

    var listener: Listener? = null

    private val onClickListener = OnClickListener { view ->
        when (view) {
            is CalculatorView -> listener?.onNumberClick(view.number)
            else -> {
                if (id == R.id.calc_num_C) listener?.onClear(view)
            }
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_calculator, this) as LinearLayout
        for (i in 0 until view.childCount) {
            val childView = view.getChildAt(i)
            if (childView is Button) childView.setOnClickListener(onClickListener)
        }
    }

    interface Listener {
        fun onNumberClick(number: Int)

        fun onClear(view: View)
    }
}
