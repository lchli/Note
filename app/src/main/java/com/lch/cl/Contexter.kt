package com.lch.cl

import android.app.Activity
import android.content.Context

object Contexter {
    private lateinit var context: Context


    fun bindContext(ctx: Context){
        if(ctx is Activity){
            context=ctx.applicationContext
        }else{
            context=ctx
        }
    }

    fun getContext():Context= context
}