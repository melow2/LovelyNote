package com.khs.lovelynote.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.room.entity.LovelyNoteEntity
import java.util.*

fun toListModel(testEntity: List<LovelyNoteEntity>): List<LovelyNote> {
    val itemList = mutableListOf<LovelyNote>()
    testEntity.map {
        itemList.add(
            LovelyNote(
                it.testId ?: 0,
                it.noteContent ?: "",
                it.mediaItemList ?: arrayListOf(),
                it.noteCreateTime?:null,
                it.noteUpdateTime?:null
            )
        )
    }
    return itemList
}


fun LiveData<List<LovelyNoteEntity>>.toLiveDataListModel(): LiveData<List<LovelyNote>> {
    // 0: 대상 리스트,
    // 1: 변환될 리스트 결과값이 List<TestModel>이어야 함. 인자가 source값으로 들어감.
    return Transformations.map<List<LovelyNoteEntity>, List<LovelyNote>>(this, ::toListModel)
}

fun LovelyNote.toEntity(): LovelyNoteEntity {
    return LovelyNoteEntity(
        id = Id?:0,
        content = content?:"",
        mediaItemList = mediaItems?: arrayListOf(),
        createTime = createTimeStamp?:null,
        updateTime = updateTimeStamp?:null
    )
}