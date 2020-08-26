package com.khs.lovelynote.model

import com.khs.lovelynote.model.mediastore.MediaStoreItem
import java.util.*

data class LovelyNote(
    var Id:Long?=null,
    var content:String?=null,
    var mediaItems:List<MediaStoreItem>?=null,
    var createTimeStamp: Date?=null,
    var updateTimeStamp: Date?=null
)