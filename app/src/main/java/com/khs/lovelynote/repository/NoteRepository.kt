package com.khs.lovelynote.repository

import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.os.AsyncTask
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

    override fun update(item: LovelyNote) {
        AsyncTask.execute {
            noteDao?.update(item.toEntity())
        }
    }

    override fun insert(item: LovelyNote) {
        AsyncTask.execute {
            noteDao?.insert(item.toEntity())
        }
    }

    override fun delete(item: LovelyNote) {
        AsyncTask.execute {
            noteDao?.delete(item.toEntity())
        }
    }

    override fun delete(noteId: Long) {
        AsyncTask.execute {
            noteDao?.delete(noteId)
        }
    }

    override fun deleteAll() {

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