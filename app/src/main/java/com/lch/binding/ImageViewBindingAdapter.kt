package com.lch.binding

import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.bumptech.glide.Glide

@BindingMethods(value = [
    BindingMethod(
        type = android.widget.ImageView::class,
        attribute = "android:src",
        method = "setImageResource")])
object ImageViewBindingAdapter {




}



@BindingAdapter(value = ["app:path","app:placeHolder"])
fun ImageView.path(path: String?,ph:Drawable?) {
    Glide.with(this).load(path).placeholder(ph).into(this)
}