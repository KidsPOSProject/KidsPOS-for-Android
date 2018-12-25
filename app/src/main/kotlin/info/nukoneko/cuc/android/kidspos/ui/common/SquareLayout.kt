package info.nukoneko.cuc.android.kidspos.ui.common

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class SquareLayout : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet? = null) : super(context, attr)
    constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attr, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (widthMeasureSpec > heightMeasureSpec) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }
    }
}
