package com.khs.visionboard.extension

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.khs.visionboard.extension.Constants.TYPE_AUDIO
import com.khs.visionboard.extension.Constants.TYPE_IMAGE
import com.khs.visionboard.extension.Constants.TYPE_VIDEO
import com.khs.visionboard.model.mediastore.*
import java.io.File
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
        ).toString()
    )
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
        val idColumn = cursor.getColumnIndexOrThrow(projection[0])
        val dateTakenColumn = cursor.getColumnIndexOrThrow(orderBy)
        val displayNameColumn = cursor.getColumnIndexOrThrow(projection[1])
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateTaken = Date(cursor.getLong(dateTakenColumn) * 1000L)
            val displayName = cursor.getString(displayNameColumn)
            val contentUri = Uri.withAppendedPath(type.externalContentUri, id.toString())
            fileList.add(MediaStoreImage(id, dateTaken, displayName, contentUri, type))
        }
    }
    query?.close()
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
        val idColumn = cursor.getColumnIndexOrThrow(projection[0])
        val dateModifiedColumn = cursor.getColumnIndexOrThrow(orderBy)
        val displayNameColumn = cursor.getColumnIndexOrThrow(projection[1])
        val albumColumn = cursor.getColumnIndexOrThrow(projection[2])
        val titleColumn = cursor.getColumnIndexOrThrow(projection[3])
        val durationColumn = cursor.getColumnIndexOrThrow(projection[4])

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateModified = Date(cursor.getLong(dateModifiedColumn) * 1000L)
            val displayName = cursor.getString(displayNameColumn)
            val album = cursor.getString(albumColumn)
            val title = cursor.getString(titleColumn)
            val duration = cursor.getString(durationColumn)
            val contentUri =
                Uri.withAppendedPath(type.externalContentUri, id.toString())
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
        query.close()
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
        MediaStore.Video.Media.DURATION,
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
        type.externalContentUri,
        projection,
        null, // selection
        null, //selectionArgs
        sortOrder
    )
    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(projection[0])
        val displayNameColumn = cursor.getColumnIndexOrThrow(projection[1])
        val durationColumn = cursor.getColumnIndexOrThrow(projection[2])
        val dateTakenColumn = cursor.getColumnIndexOrThrow(orderBy)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateTaken = Date(cursor.getLong(dateTakenColumn) * 1000L)
            val displayName = cursor.getString(displayNameColumn)
            val duration = cursor.getString(durationColumn)
            val contentUri = Uri.withAppendedPath(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )
            fileList.add(MediaStoreVideo(id, dateTaken, displayName, contentUri,type,duration))
        }
    }
    query?.close()
    return fileList
}

fun ContentResolver.getFileDataFromUri(uri: Uri): MediaStoreItem? {
    val contentResolver = this
    val type = getType(uri)?.run {
        val idx = this.indexOf("/")
        this.substring(0, idx)
    }
    var projection: Array<String>? = arrayOf()
    when (type) {
        TYPE_IMAGE -> {
            projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
            )

        }
        TYPE_AUDIO -> {
            projection = arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DURATION
            )
        }
        TYPE_VIDEO -> {
            projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DURATION
            )
        }
        else -> {
            projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_MODIFIED
            )
        }
    }


    var query = contentResolver.query(
        when (type) {
            TYPE_AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            TYPE_VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            TYPE_IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            else -> uri
        },
        projection,
        null, // selection
        null, //selectionArgs
        null
    )

    when (type) {
        TYPE_IMAGE -> {
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(projection[0])
                val displayNameColumn = cursor.getColumnIndexOrThrow(projection[1])
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(projection[2])
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(idColumn)
                    val dateTaken = Date(cursor.getLong(dateModifiedColumn) * 1000L)
                    val displayName = cursor.getString(displayNameColumn)
                    return MediaStoreImage(
                        id,
                        dateTaken,
                        displayName,
                        uri,
                        MediaStoreFileType.IMAGE
                    )
                }
            }
        }
        TYPE_AUDIO -> {
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(projection[0])
                val displayNameColumn = cursor.getColumnIndexOrThrow(projection[1])
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(projection[2])
                val albumColumn = cursor.getColumnIndexOrThrow(projection[3])
                val titleColumn = cursor.getColumnIndexOrThrow(projection[4])
                val durationColumn = cursor.getColumnIndexOrThrow(projection[5])

                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(idColumn)
                    val dateModified = Date(cursor.getLong(dateModifiedColumn) * 1000L)
                    val displayName = cursor.getString(displayNameColumn)
                    val album = cursor.getString(albumColumn)
                    val title = cursor.getString(titleColumn)
                    val duration = cursor.getString(durationColumn)
                    return MediaStoreAudio(
                        id = id,
                        dateTaken = dateModified,
                        displayName = displayName,
                        album = album,
                        title = title,
                        _duration = duration,
                        contentUri = uri,
                        type = MediaStoreFileType.AUDIO
                    )
                }
            }
        }
        TYPE_VIDEO -> {
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(projection[0])
                val displayNameColumn = cursor.getColumnIndexOrThrow(projection[1])
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(projection[2])
                val durationColumn = cursor.getColumnIndexOrThrow(projection[3])
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(idColumn)
                    val dateTaken = Date(cursor.getLong(dateModifiedColumn) * 1000L)
                    val displayName = cursor.getString(displayNameColumn)
                    val duration = cursor.getString(durationColumn)
                    return MediaStoreVideo(
                        id,
                        dateTaken,
                        displayName,
                        uri,
                        MediaStoreFileType.VIDEO,
                        duration
                    )
                }
            }
        }
        else -> {
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(projection[0])
                val displayNameColumn = cursor.getColumnIndexOrThrow(projection[1])
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(projection[2])
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(idColumn)
                    val dateTaken = Date(cursor.getLong(dateModifiedColumn) * 1000L)
                    val displayName = cursor.getString(displayNameColumn)
                    return MediaStoreFile(
                        id,
                        dateTaken,
                        displayName,
                        uri,
                        MediaStoreFileType.FILE.apply {
                            externalContentUri = uri
                        }
                    )
                }
            }
        }
    }
    query?.close()
    return null
}

fun ContentResolver.getMimeType(uri: Uri): String? {
    val contentResolver = this
    val extension: String?
    //Check uri format to avoid null
    extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        //If scheme is a content
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(contentResolver.getType(uri))
    } else {
        //If scheme is a File
        //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
        MimeTypeMap.getFileExtensionFromUrl(
            Uri.fromFile(File(uri.path)).toString()
        )
    }
    return extension
}
