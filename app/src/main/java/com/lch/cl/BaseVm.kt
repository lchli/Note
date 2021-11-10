package com.lch.cl

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseVm:ViewModel(){
    private val liveContext= MutableLiveData<FragmentActivity>()
     val context:FragmentActivity?
     get() {
        return liveContext.value
    }


    fun attachContext(context: FragmentActivity){
        liveContext.value=context
    }





}