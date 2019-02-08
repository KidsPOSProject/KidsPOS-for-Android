package info.nukoneko.cuc.android.kidspos.extensions

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.widget.EditText

@BindingAdapter("editTextIntText")
fun EditText.setEditTextIntText(value: Int) {
    setText("$value")
}

@InverseBindingAdapter(attribute = "editTextIntText")
fun EditText.getEditTextIntText(): Int {
    return text?.toString()?.toIntOrNull() ?: 0
}