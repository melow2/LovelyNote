package com.khs.visionboard.extension

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.khs.visionboard.model.mediastore.MediaStoreAudio
import com.khs.visionboard.model.mediastore.MediaStoreFileType
import com.khs.visionboard.model.mediastore.MediaStoreImage
import com.khs.visionboard.model.mediastore.MediaStoreVideo
import java.util.*

/**
 * image allowed directories are [DCIM, Pictures]
 * audio allowed directories are [Alarms, Music, Notifications, Podcasts, Ringtones]
 * video allowed directories are [DCIM, Movies]
 *
 * @SuppressLint("InlinedApi"): 이전플랫폼에서 작동하거나 작동하지 않을 수 있음.
 * LIMIT 10: 10개의 로우를 출력.
 * LIMIT 3 OFFSET 2: 2개의 로우를 건너 뛰고 3개를 출력하라는 의미.
 * */


fun ContentResolver.getMediaStoreImageFiles(
    limit: Int?,
    offset: Int?,
    type: MediaStoreFileType
): MutableList<MediaStoreImage> {
    val contentResolver = this
    val fileList = mutableListOf<MediaStoreImage>()
    val orderBy = MediaStore.Images.Media.DATE_MODIFIED

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        orderBy
    )

    val selection = "$orderBy>= ?"
    val selectionArgs = arrayOf(
        dateToTimestamp(
            day = 1,
            month = 1,
            year = 1970
        ).toString())
    val sortOrder = if (limit == null) "$orderBy DESC"
    else "$orderBy DESC LIMIT $limit OFFSET $offset"

    val query = contentResolver.query(
        type.externalContentUri,
        projection,
        null,
        null,
        sortOrder
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val dateTakenColumn = cursor.getColumnIndexOrThrow(orderBy)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateTaken = Date(cursor.getLong(dateTakenColumn)*1000L)
            val displayName = cursor.getString(displayNameColumn)
            val contentUri = Uri.withAppendedPath(type.externalContentUri, id.toString())
            fileList.add(MediaStoreImage(id, dateTaken, displayName, contentUri, type))
        }
    }
    // fileList.size.initGalleryItems()
    return fileList
}

fun ContentResolver.getMediaStoreAudioFiles(
    limit: Int,
    offset: Int,
    type: MediaStoreFileType
): MutableList<MediaStoreAudio> {
    val contentResolver = this
    val fileList = mutableListOf<MediaStoreAudio>()
    var orderBy = MediaStore.Audio.Media.DATE_MODIFIED

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DURATION,
        orderBy
    )

    val selection = "$orderBy >= ?"
    val selectionArgs = arrayOf(
        dateToTimestamp(day = 1, month = 1, year = 1970)
            .toString()
    )

    val sortOrder = "$orderBy DESC LIMIT $limit OFFSET $offset"

    val query = contentResolver.query(
        type.externalContentUri,
        projection,
        null,
        null,
        sortOrder
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val dateModifiedColumn = cursor.getColumnIndexOrThrow(orderBy)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateModified = Date(cursor.getLong(dateModifiedColumn)*1000L)
            val displayName = cursor.getString(displayNameColumn)
            val album = cursor.getString(albumColumn)
            val title = cursor.getString(titleColumn)
            val duration = cursor.getString(durationColumn)
            val contentUri =
                Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())
            fileList.add(
                MediaStoreAudio(
                    id = id,
                    dateTaken = dateModified,
                    displayName = displayName,
                    album = album,
                    title = title,
                    _duration = duration,
                    contentUri = contentUri,
                    type = MediaStoreFileType.AUDIO
                )
            )
        }
    }
    return fileList
}

fun ContentResolver.getMediaStoreVideoFiles(
    limit: Int?,
    offset: Int?,
    type: MediaStoreFileType
): MutableList<MediaStoreVideo> {
    val fileList = mutableListOf<MediaStoreVideo>()
    val contentResolver = this
    val orderBy = MediaStore.Video.Media.DATE_MODIFIED
    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        orderBy
    )
    val selection = "$orderBy >= ?"
    val selectionArgs = arrayOf(
        dateToTimestamp(day = 1, month = 1, year = 1970)
            .toString()
    )

    val sortOrder = if (limit == null) "$orderBy DESC"
    else "$orderBy DESC LIMIT $limit OFFSET $offset"

    val query = contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null, // selection
        null, //selectionArgs
        sortOrder
    )
    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val dateTakenColumn = cursor.getColumnIndexOrThrow(orderBy)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateTaken = Date(cursor.getLong(dateTakenColumn)*1000L)
            val displayName = cursor.getString(displayNameColumn)
            val contentUri = Uri.withAppendedPath(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )
            fileList.add(MediaStoreVideo(id, dateTaken, displayName, contentUri, type))
        }
    }
    return fileList
}
