package com.lch.cl.util

import android.util.Log
import com.lch.cl.Contexter

fun log(msg:String){
    Log.e("sss","$msg")
}

fun getStrings(id:Int):String{
    return Contexter.getContext().getString(id)
}