package info.nukoneko.cuc.android.kidspos.extensions

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter

@BindingAdapter("editTextIntText")
fun EditText.setEditTextIntText(value: Int) {
    setText("$value")
}

@InverseBindingAdapter(attribute = "editTextIntText")
fun EditText.getEditTextIntText(): Int {
    return text?.toString()?.toIntOrNull() ?: 0
}