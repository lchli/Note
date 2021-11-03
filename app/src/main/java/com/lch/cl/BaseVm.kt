package com.lch.cl

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseVm:ViewModel(){
    private val liveContext= MutableLiveData<Context>()
     val context:Context?
     get() {
        return liveContext.value
    }


    fun attachContext(context: Context){
        liveContext.value=context
    }





}