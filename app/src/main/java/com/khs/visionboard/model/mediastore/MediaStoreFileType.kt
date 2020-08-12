package com.khs.visionboard.model.mediastore

import android.net.Uri
import android.provider.MediaStore
import com.khs.visionboard.extension.Constants.AUDIO_MIME_TYPE
import com.khs.visionboard.extension.Constants.AUDIO_PATH_BY_DCIM
import com.khs.visionboard.extension.Constants.IMAGE_MIME_TYPE
import com.khs.visionboard.extension.Constants.IMAGE_PATH_BY_DCIM
import com.khs.visionboard.extension.Constants.VIDEO_MIME_TYPE
import com.khs.visionboard.extension.Constants.VIDEO_PATH_BY_DCIM

enum class MediaStoreFileType(
    val externalContentUri: Uri,
    val mimeType: String,
    val pathByDCIM: String,
    val typeCode: Int
) {
    IMAGE(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        IMAGE_MIME_TYPE,
        IMAGE_PATH_BY_DCIM,
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
    ),
    AUDIO(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        AUDIO_MIME_TYPE,
        AUDIO_PATH_BY_DCIM,
        MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO
    ),
    VIDEO(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        VIDEO_MIME_TYPE,
        VIDEO_PATH_BY_DCIM,
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
    );
}