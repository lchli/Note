package com.lch.note

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

class EditNoteViewModel : ViewModel() {

    val noteListEvent = MutableLiveData<List<Any>>()
    val inserImgEvent = MutableLiveData<Int?>()
    val inserAudioEvent = MutableLiveData<Int?>()
    val inserVideooEvent = MutableLiveData<Int?>()
    val showOpEvent = MutableLiveData<Int?>()


    fun insertText(context: Context, text: String, position: Int? = null) {
        viewModelScope.launch {
            context.noteEditRepo().add(NoteContentText(text), position)
            noteListEvent.value = context.noteEditRepo().list
        }
    }

    fun insertImg(context: Context, uri: String, position: Int? = null) {
        viewModelScope.launch {
            context.noteEditRepo().add(NoteContentImg(uri), position)
            noteListEvent.value = context.noteEditRepo().list
        }
    }

    fun insertAudio(context: Context, uri: String, position: Int? = null) {
        viewModelScope.launch {
            context.noteEditRepo().add(NoteContentAudio(uri), position)
            noteListEvent.value = context.noteEditRepo().list
        }
    }

    fun insertVideo(context: Context, uri: String, position: Int? = null) {
        viewModelScope.launch {
            context.noteEditRepo().add(NoteContentVideo(uri), position)
            noteListEvent.value = context.noteEditRepo().list
        }
    }


    fun save(context: Context) {
        val meta = NoteMetaData(
            UUID.randomUUID().toString(),
            "skkkkkkkkkkkkkk",
            System.currentTimeMillis()
        )
        viewModelScope.launch {
            context.noteRepo().save(meta, context.noteEditRepo().list)
        }
    }

    fun onInsertImg(position: Int? = null) {
        inserImgEvent.postValue(position)
    }

    fun onInsertAudio(position: Int? = null) {
        inserAudioEvent.postValue(position)
    }

    fun onInsertVideo(position: Int? = null) {
        inserVideooEvent.postValue(position)
    }

    fun showOp() {
        // showOpEvent.postValue(System.currentTimeMillis())
    }

    fun insertBefore(position: Int) {
        showOpEvent.postValue(position)
    }

    fun insertAfter(position: Int) {
        showOpEvent.postValue(position)
    }

    fun del(context: Context, position: Int) {
        context.noteEditRepo().remove(position)
        noteListEvent.value = context.noteEditRepo().list
    }

}