package com.lch.binding

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.databinding.BindingAdapter


@BindingAdapter("android:layout_marginBottom")
fun setMarginBottom(view: View, value: Int) {
    (view.layoutParams as? MarginLayoutParams)?.bottomMargin = value
}


@BindingAdapter("layout_width")
fun setLayoutWidth(view: View, value: Int) {
    view.layoutParams?.width = value
    view.layoutParams=view.layoutParams
}




