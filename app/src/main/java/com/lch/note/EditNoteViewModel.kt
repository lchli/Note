package com.lch.note

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

class EditNoteViewModel: ViewModel() {

    val noteListEvent=MutableLiveData<List<Any>>()
    val inserImgEvent=MutableLiveData<Int?>()
    val inserAudioEvent=MutableLiveData<Long?>()
    val inserVideooEvent=MutableLiveData<Long?>()
    val showOpEvent=MutableLiveData<Int?>()



    fun insertText(context: Context,text:String,position: Int?=null){
        viewModelScope.launch {
            context.noteEditRepo().add(NoteContentText(text),position)
            noteListEvent.value=context.noteEditRepo().list
        }
    }

    fun insertImg(context: Context,uri:String,position: Int?=null){
        viewModelScope.launch {
            context.noteEditRepo().add(NoteContentImg(uri),position)
            noteListEvent.value=context.noteEditRepo().list
        }
    }


    fun save(context: Context){
        val meta=NoteMetaData(UUID.randomUUID().toString(),"skkkkkkkkkkkkkk",System.currentTimeMillis())
        viewModelScope.launch {
            context.noteRepo().save(meta,context.noteEditRepo().list)
        }
    }

    fun onInsertImg(position:Int?=null){
        inserImgEvent.postValue(position)
    }
    fun onInsertAudio(){
        inserAudioEvent.postValue(System.currentTimeMillis())
    }
    fun onInsertVideo(){
        inserVideooEvent.postValue(System.currentTimeMillis())
    }

    fun showOp(){
       // showOpEvent.postValue(System.currentTimeMillis())
    }

    fun insertBefore(position:Int){
        showOpEvent.postValue(position)
    }

    fun insertAfter(position:Int){
        showOpEvent.postValue(position)
    }

    fun del(context: Context,position:Int){
            context.noteEditRepo().remove(position)
            noteListEvent.value=context.noteEditRepo().list
    }

}