package com.lch.binding

import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods

@BindingMethods(value = [
    BindingMethod(
        type = android.widget.ImageView::class,
        attribute = "android:src",
        method = "setImageResource")])
object ImageViewBindingAdapter {




}