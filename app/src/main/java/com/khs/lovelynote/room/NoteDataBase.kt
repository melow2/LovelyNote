package com.khs.lovelynote.room

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.khs.lovelynote.room.entity.LovelyNoteEntity
import com.khs.lovelynote.util.Converters

@Database(
    entities = [LovelyNoteEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NoteDataBase:RoomDatabase(){

    abstract fun noteDao(): NoteDao?

    companion object{
        @Volatile private var instance: NoteDataBase?=null
        fun getInstance(context: Context): NoteDataBase?{
            instance ?: synchronized(NoteDataBase::class){
                instance = Room.databaseBuilder(context,
                    NoteDataBase::class.java,
                    "LOVELY_NOTE"
                ).fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            return instance
        }

        private val roomCallback:Callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // PopulateAsyncTask(instance).execute()
            }
        }
    }

}

