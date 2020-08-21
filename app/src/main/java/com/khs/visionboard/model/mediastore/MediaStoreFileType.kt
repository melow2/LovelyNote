package com.khs.visionboard.model.mediastore

import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.khs.visionboard.extension.Constants.AUDIO_MIME_TYPE
import com.khs.visionboard.extension.Constants.AUDIO_PATH_BY_DCIM
import com.khs.visionboard.extension.Constants.FILE_MIME_TYPE
import com.khs.visionboard.extension.Constants.FILE_PATH_BY_DCIM
import com.khs.visionboard.extension.Constants.IMAGE_MIME_TYPE
import com.khs.visionboard.extension.Constants.IMAGE_PATH_BY_DCIM
import com.khs.visionboard.extension.Constants.VIDEO_MIME_TYPE
import com.khs.visionboard.extension.Constants.VIDEO_PATH_BY_DCIM

enum class MediaStoreFileType(
    var externalContentUri: Uri,
    val mimeType: String,
    val directory: String,
    val typeCode: Int
) {
    IMAGE(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        IMAGE_MIME_TYPE,
        Environment.DIRECTORY_PICTURES,
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
    ),
    AUDIO(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        AUDIO_MIME_TYPE,
        Environment.DIRECTORY_MUSIC,
        MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO
    ),
    VIDEO(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        VIDEO_MIME_TYPE,
        Environment.DIRECTORY_MOVIES,
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
    ),
    FILE(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        FILE_MIME_TYPE,
        Environment.DIRECTORY_DOCUMENTS,
        MediaStore.Files.FileColumns.MEDIA_TYPE_NONE
    )
}