package com.khs.lovelynote.repository

import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khs.lovelynote.extension.toEntity
import com.khs.lovelynote.extension.toLiveDataListModel
import com.khs.lovelynote.extension.toLiveDataSingleModel
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.room.NoteDao
import com.khs.lovelynote.room.NoteDataBase
import com.khs.lovelynote.room.entity.LovelyNoteEntity


class NoteRepository(application: Application): NoteBaseRepository {

    private val noteDao: NoteDao?

    companion object{
        @Volatile private var instance: NoteRepository?=null
        fun getInstance(application: Application): NoteRepository {
            return instance ?: NoteRepository(application)
        }
    }

    init {
        val db = NoteDataBase.getInstance(application)
        noteDao=db?.noteDao()
    }

    override suspend fun update(item: LovelyNote) {
        noteDao?.update(item.toEntity())
    }

    override suspend fun insert(item: LovelyNote) {
        noteDao?.insert(item.toEntity())
    }

    override suspend fun delete(item: LovelyNote) {
        noteDao?.delete(item.toEntity())
    }

    override suspend fun delete(noteId: Long) {
        noteDao?.delete(noteId)
    }

    override suspend fun deleteAll() {

    }

    override fun getItem(id: Long): LiveData<LovelyNote>? {
        val item = noteDao?.getItem(id)
        return item?.toLiveDataSingleModel()
    }

    override fun getAll(): LiveData<List<LovelyNote>>? {
        val localList: LiveData<List<LovelyNoteEntity>>? = noteDao?.getAll()
        return localList?.toLiveDataListModel()
    }

}