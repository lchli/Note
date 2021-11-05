package com.lch.cl

import java.io.File

sealed class FileFilterType {
    abstract fun isMatch(f: File): Boolean

    class Size(val length: Long) : FileFilterType() {
        override fun isMatch(f: File): Boolean {
            return f.length() >= length
        }

    }

    class Category(val cat: Mime) : FileFilterType() {
        override fun isMatch(f: File): Boolean {
            return cat.isMatch(f)
        }
    }

    class Date(val start: Long, val end: Long) : FileFilterType() {
        override fun isMatch(f: File): Boolean {
            return f.lastModified() in start..end
        }
    }

    object No : FileFilterType() {
        override fun isMatch(f: File): Boolean {
            return true
        }
    }

}
