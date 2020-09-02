package com.khs.lovelynote.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.room.entity.LovelyNoteEntity

fun toListModel(entities: List<LovelyNoteEntity>): List<LovelyNote> {
    val itemList = mutableListOf<LovelyNote>()
    var thumbnail: String? = null
    entities.map {
        thumbnail = if (it.noteMediaItemList?.size != 0) {
            it.noteMediaItemList?.get(0)?.contentUri
        } else {
            null
        }
        itemList.add(
            LovelyNote(
                it.noteId ?: 0,
                it.noteContent ?: "",
                thumbnail,
                it.noteMediaItemList ?: arrayListOf(),
                it.noteCreateTime,
                it.noteUpdateTime,
                it.noteIsHold ?: false,
                it.noteIsLock ?: false
            )
        )
    }
    return itemList
}

fun toSingleModel(entity: LovelyNoteEntity): LovelyNote {
    val thumbnail = if (entity.noteMediaItemList?.size != 0) {
        entity.noteMediaItemList?.get(0)?.contentUri
    } else {
        null
    }
    return LovelyNote(
        entity.noteId ?: 0,
        entity.noteContent ?: "",
        thumbnail,
        entity.noteMediaItemList ?: arrayListOf(),
        entity.noteCreateTime,
        entity.noteUpdateTime,
        entity.noteIsHold ?: false,
        entity.noteIsLock ?: false
    )
}


fun LiveData<LovelyNoteEntity>.toLiveDataSingleModel(): LiveData<LovelyNote> {
    // 0: 대상 리스트,
    // 1: 변환될 리스트 결과값이 List<TestModel>이어야 함. 인자가 source값으로 들어감.
    return Transformations.map<LovelyNoteEntity, LovelyNote>(this, ::toSingleModel)
}

fun LiveData<List<LovelyNoteEntity>>.toLiveDataListModel(): LiveData<List<LovelyNote>> {
    // 0: 대상 리스트,
    // 1: 변환될 리스트 결과값이 List<TestModel>이어야 함. 인자가 source값으로 들어감.
    return Transformations.map<List<LovelyNoteEntity>, List<LovelyNote>>(this, ::toListModel)
}

fun LovelyNote.toEntity(): LovelyNoteEntity {
    return LovelyNoteEntity(
        id = Id ?: 0,
        content = content ?: "",
        mediaItemList = mediaItems ?: arrayListOf(),
        createTime = createTimeStamp,
        updateTime = updateTimeStamp,
        isHold = isHold ?: false,
        isLock = isLock ?: false
    )
}