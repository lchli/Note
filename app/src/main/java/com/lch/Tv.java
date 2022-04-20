package com.lch;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class Tv extends androidx.appcompat.widget.AppCompatTextView {
    public Tv(Context context) {
       // Log.d("sss","");
        this(context,null);
    }

    public Tv(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Tv(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
