package com.lch.note

import java.text.FieldPosition

object EditNoteRepo {
      val list=ArrayList<Any>()

    fun add(e:Any,position: Int?=null){
        if(position==null) {
            list.add(e)
        }else{
            list.add(position,e)
        }
    }

    fun remove(e:Int){
        list.removeAt(e)
    }


}