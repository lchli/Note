package com.lch.cl

sealed class FileSortType{

 class Size(val direction:Int) :FileSortType()

 class Time(val direction:Int) :FileSortType()

}


