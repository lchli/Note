package com.lch.cl

sealed class FileFilterType {

 class Size(val length: Long) : FileFilterType()

 class Category(val cat: Int) : FileFilterType()

 class Date(val start: Long,val end:Long) : FileFilterType()

}
