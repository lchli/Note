package com.lch.cl

import java.io.File

sealed class FileSortType {

    abstract fun sort(list: MutableList<String>)

    class Size(val direction: Int) : FileSortType() {

        override fun sort(list: MutableList<String>) {
            if (direction == FileConst.SORT_DIRECTION_ASC) {
                list.sortBy {
                    File(it).length()
                }
            } else {
                list.sortByDescending {
                    File(it).length()
                }
            }
        }


    }

    class Time(val direction: Int) : FileSortType(){

        override fun sort(list: MutableList<String>) {
            if (direction == FileConst.SORT_DIRECTION_ASC) {
                list.sortBy {
                    File(it).lastModified()
                }
            } else {
                list.sortByDescending {
                    File(it).lastModified()
                }
            }
        }
    }

}


