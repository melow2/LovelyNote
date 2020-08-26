package com.khs.lovelynote.repository

import androidx.lifecycle.LiveData
import com.khs.lovelynote.model.LovelyNote

interface NoteBaseRepository{
    fun insert(item: LovelyNote)
    fun delete(item: LovelyNote)
    fun deleteAll(){}
    fun getAll(): LiveData<List<LovelyNote>>?
}