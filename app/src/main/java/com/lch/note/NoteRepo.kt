package com.lch.note

interface NoteRepo {
    suspend fun getNotes(): List<NoteMetaData>

    suspend fun save(noteMeta: NoteMetaData, content: List<Any>): Boolean

    suspend fun getNoteContent(id: String): List<Any>?
}