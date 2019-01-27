package info.nukoneko.cuc.android.kidspos

import android.databinding.BindingAdapter
import android.widget.TextView

@BindingAdapter("bind:river")
fun TextView.setRiverText(river: Int) {
    text = "$river リバー"
}