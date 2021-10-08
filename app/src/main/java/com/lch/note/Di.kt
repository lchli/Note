package com.lch.note

import android.content.Context

fun Context.noteRepo():NoteRepo{
      return NoteDataStore(this)
}



fun Context.noteEditRepo():EditNoteRepo{
      return EditNoteRepo
}
