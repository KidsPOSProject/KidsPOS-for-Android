package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import info.nukoneko.cuc.android.kidspos.R

class CalculatorLayout : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet? = null) : super(context, attr)
    constructor(
        context: Context,
        attr: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attr, defStyleAttr)

    var listener: Listener? = null

    private val onClickListener = OnClickListener { view ->
        when (view) {
            is CalculatorView -> listener?.onNumberClick(view.number)
            else -> {
                if (view.id == R.id.btn_delete_one) listener?.onClearClick()
            }
        }
    }

    private val viewIds: Array<Int> = arrayOf(
        R.id.btn_0, R.id.btn_1, R.id.btn_2,
        R.id.btn_3, R.id.btn_4, R.id.btn_5,
        R.id.btn_6, R.id.btn_7, R.id.btn_8,
        R.id.btn_9, R.id.btn_delete_one
    )

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_calculator_layout, this)
        for (viewId in viewIds) {
            root.findViewById<View>(viewId).setOnClickListener(onClickListener)
        }
    }

    interface Listener {
        fun onNumberClick(number: Int)

        fun onClearClick()
    }
}
