package com.lch.cl

import java.io.File

sealed class Mime {
    abstract fun isMatch(f: File): Boolean

    object Video : Mime() {
        override fun isMatch(f: File): Boolean {
            val type = f.mimeType() ?: return false
            return type.startsWith("video/")
        }

    }

    object Audio : Mime() {
        override fun isMatch(f: File): Boolean {
            val type = f.mimeType() ?: return false
            return type.startsWith("audio/")
        }

    }

    object Img : Mime() {
        override fun isMatch(f: File): Boolean {
            val type = f.mimeType() ?: return false
            return type.startsWith("image/")
        }

    }

    object Pdf : Mime() {
        override fun isMatch(f: File): Boolean {
            val type = f.mimeType() ?: return false
            return type.startsWith("application/pdf")||
             type.startsWith("application/vnd.ms-powerpoint")||
             type.startsWith("application/vnd.openxmlformats-officedocument.presentationml.presentation")
        }

    }

    object Zip : Mime() {
        override fun isMatch(f: File): Boolean {
            val type = f.mimeType() ?: return false
            return type.startsWith("application/zip")||
             type.startsWith("application/x-gtar")||
             type.startsWith("application/x-tar")||
             type.startsWith("application/x-zip-compressed")||
             type.startsWith("application/x-compressed")||
             type.startsWith("application/x-gzip")
        }

    }

    object Doc : Mime() {
        override fun isMatch(f: File): Boolean {
            val type = f.mimeType() ?: return false
            return type.startsWith("application/msword")||
                    type.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")||
                    type.startsWith("application/vnd.ms-excel")||
                    type.startsWith("application/vnd.ms-works")||
                    type.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }

    }

    object Apk : Mime() {
        override fun isMatch(f: File): Boolean {
            val type = f.mimeType() ?: return false
            return type.startsWith("application/vnd.android.package-archive")
        }

    }

    object Other : Mime() {
        override fun isMatch(f: File): Boolean {
            if( Video.isMatch(f)||Audio.isMatch(f)||Zip.isMatch(f)||
                    Pdf.isMatch(f)||Doc.isMatch(f)||Img.isMatch(f)||Apk.isMatch(f)){
                return false
            }

            return true
        }

    }

}
