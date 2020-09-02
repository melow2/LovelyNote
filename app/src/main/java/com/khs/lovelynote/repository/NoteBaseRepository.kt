package com.khs.lovelynote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khs.lovelynote.model.LovelyNote

interface NoteBaseRepository{
    fun insert(item: LovelyNote)
    fun update(item: LovelyNote)
    fun delete(item: LovelyNote)
    fun delete(noteId: Long)
    fun deleteAll(){}
    fun getItem(id:Long): LiveData<LovelyNote>?
    fun getAll(): LiveData<List<LovelyNote>>?
}