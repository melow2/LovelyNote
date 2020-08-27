package com.khs.lovelynote.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.khs.lovelynote.model.mediastore.MediaStoreItem
import java.util.*

@Entity(tableName = "lovely_note")
data class LovelyNoteEntity(
    @ColumnInfo(name = "note_content")
    var noteContent: String?=null,
    @ColumnInfo(name = "note_media_item_list")
    var noteMediaItemList: List<MediaStoreItem>?=null,
    @ColumnInfo(name = "note_create_time")
    var noteCreateTime: Date?=null,
    @ColumnInfo(name = "note_update_time")
    var noteUpdateTime: Date?=null,
    @ColumnInfo(name = "note_is_hold")
    var noteIsHold: Boolean?=null

){
    @PrimaryKey
    @ColumnInfo(name="note_id")
    var noteId:Long?=null

    /* 생성 시*/
    constructor(
        id:Long?,
        content:String?,
        createTime:Date?,
        updateTime:Date?,
        mediaItemList: List<MediaStoreItem>?,
        isHold:Boolean?
    ):this(){
        this.noteId = id
        this.noteContent = content
        this.noteCreateTime = createTime
        this.noteUpdateTime = updateTime
        this.noteMediaItemList = mediaItemList
        this.noteIsHold = isHold
    }
}
