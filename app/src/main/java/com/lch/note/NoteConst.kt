package com.lch.note

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

object NoteConst {
    const val TEXT = "text"
    const val IMG = "img"
    const val VIDEO = "video"
    const val AUDIO = "audio"
    private const val NOTE_ROOT_DIR = "notes"
     const val NOTE_META_FILE = "note-meta.json"
     const val NOTE_CONTENT_FILE = "note-content.json"

    fun getNoteRootDir(context: Context): String {
        val dir = "${context.filesDir}/${NOTE_ROOT_DIR}/"
        val dirFile = File(dir)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        Log.e("sss","root dir:"+dir)
        Log.e("sss","root dir exists:"+dirFile.exists())
        return dir
    }
}