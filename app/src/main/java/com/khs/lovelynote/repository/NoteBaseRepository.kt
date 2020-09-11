package com.khs.lovelynote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khs.lovelynote.model.LovelyNote

interface NoteBaseRepository{
    suspend fun insert(item: LovelyNote)
    suspend fun update(item: LovelyNote)
    suspend fun delete(item: LovelyNote)
    suspend fun delete(noteId: Long)
    suspend fun deleteAll(){}
    fun getItem(id:Long): LiveData<LovelyNote>?
    fun getAll(): LiveData<List<LovelyNote>>?
}