package com.lch.note

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteListViewModel: ViewModel() {

    val noteListEvent=MutableLiveData<List<NoteMetaData>>()

    fun getNoteList(context: Context){
        viewModelScope.launch {
            noteListEvent.value=context.noteRepo().getNotes()
        }
    }

}