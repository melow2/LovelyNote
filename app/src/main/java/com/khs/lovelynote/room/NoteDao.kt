package com.khs.lovelynote.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.khs.lovelynote.room.entity.LovelyNoteEntity

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteEntity: LovelyNoteEntity)

    @Delete
    suspend fun delete(noteEntity: LovelyNoteEntity)

    @Update
    suspend fun update(noteEntity: LovelyNoteEntity)

    @Query("DELETE FROM lovely_note WHERE :noteId LIKE note_id")
    suspend fun delete(noteId:Long)

    @Query("DELETE FROM lovely_note")
    suspend fun deleteAll()

    @Query("SELECT * FROM lovely_note ORDER BY note_update_time DESC")
    fun getAll(): LiveData<List<LovelyNoteEntity>>?

    @Query("SELECT * FROM lovely_note WHERE :noteId LIKE note_id")
    fun getItem(noteId:Long) : LiveData<LovelyNoteEntity>
}