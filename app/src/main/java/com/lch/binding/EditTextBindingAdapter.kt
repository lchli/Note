package com.lch.binding

import android.widget.EditText
import androidx.databinding.BindingAdapter


@BindingAdapter("correctCursorInputType")
fun EditText.correctCursorInputType(type: Int?) {
    if (type == null) {
        return
    }
    val oldSec = selectionStart
    inputType = type

    setSelection(oldSec)

}





