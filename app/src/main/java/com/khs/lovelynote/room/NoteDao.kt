package com.khs.lovelynote.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.khs.lovelynote.room.entity.LovelyNoteEntity

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noteEntity: LovelyNoteEntity)

    @Delete
    fun delete(noteEntity: LovelyNoteEntity)

    @Query("DELETE FROM lovely_note")
    fun deleteAll()

    @Query("SELECT * FROM lovely_note ")
    fun getAll(): LiveData<List<LovelyNoteEntity>>?
}