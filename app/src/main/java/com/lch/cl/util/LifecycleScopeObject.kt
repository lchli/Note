package com.lch.cl.util

import androidx.lifecycle.*
import java.util.HashMap

class LifecycleScopeObject : LifecycleEventObserver {
    private val mMap = HashMap<String, Any?>(1)

    fun getData(key: String): Any? = mMap[key]

    fun saveData(lifecycleOwner: LifecycleOwner, key: String, value: Any?) {
        mMap[key] = value
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            mMap.clear()
            source.lifecycle.removeObserver(this)
        }
    }

    companion object {
        private val objectMap = HashMap<String, LifecycleScopeObject>()

        fun of(clazz: Class<out Any>, tag: String = ""): LifecycleScopeObject {
            val key = clazz.name + tag
            var obj = objectMap[key]
            if (obj == null) {
                obj = LifecycleScopeObject()
                objectMap[key] = obj
            }

            return obj
        }
    }

}