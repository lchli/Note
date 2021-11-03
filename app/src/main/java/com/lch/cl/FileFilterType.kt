package com.lch.cl

sealed class FileFilterType {

 class Size(val length: Long) : FileFilterType()

 class Category(val cat: Int) : FileFilterType()

}
