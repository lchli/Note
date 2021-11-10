package com.lch.cl.util

import android.app.Activity

object ActivityScopeStore {
    private val map = hashMapOf<Class<out Activity>, HashMap<Any, Any?>>()

    fun of(activity: Class<out Activity>): HashMap<Any, Any?> {
        var mp = map[activity]
        if (mp == null) {
            mp = hashMapOf()
            map[activity] = mp
        }

        return mp
    }

    fun delete(activity: Class<out Activity>) {
        map[activity]?.clear()
    }
}