package com.khs.visionboard.extension

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.*
import android.net.Uri
import android.provider.MediaStore
import com.khs.visionboard.extension.Constants.TYPE_AUDIO
import com.khs.visionboard.extension.Constants.TYPE_IMAGE
import com.khs.visionboard.extension.Constants.TYPE_VIDEO
import com.khs.visionboard.model.mediastore.*
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

fun Context.getMediaStoreImageFiles(
    limit: Int?,
    offset: Int?,
    type: MediaStoreFileType
): MutableList<MediaStoreImage> {
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
            val filePath = getPath(contentUri.toString())
            fileList.add(MediaStoreImage(id, dateTaken, displayName, Uri.parse(filePath), type))
        }
    }
    query?.close()
    // fileList.size.initGalleryItems()
    return fileList
}

fun Context.getMediaStoreAudioFiles(
    limit: Int,
    offset: Int,
    type: MediaStoreFileType
): MutableList<MediaStoreAudio> {
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
            val contentUri = Uri.withAppendedPath(type.externalContentUri, id.toString())
            val filePath = getPath(contentUri.toString())
            fileList.add(
                MediaStoreAudio(
                    id = id,
                    dateTaken = dateModified,
                    displayName = displayName,
                    album = album,
                    title = title,
                    _duration = duration,
                    contentUri = Uri.parse(filePath),
                    type = MediaStoreFileType.AUDIO
                )
            )
        }
        query.close()
    }
    return fileList
}

fun Context.getMediaStoreVideoFiles(
    limit: Int?,
    offset: Int?,
    type: MediaStoreFileType
): MutableList<MediaStoreVideo> {
    val fileList = mutableListOf<MediaStoreVideo>()
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
        null,     // selection
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
            val contentUri = Uri.withAppendedPath(type.externalContentUri, id.toString())
            val filePath = getPath(contentUri.toString())
            fileList.add(
                MediaStoreVideo(
                    id,
                    dateTaken,
                    displayName,
                    Uri.parse(filePath),
                    type,
                    duration
                )
            )
        }
    }
    query?.close()
    return fileList
}


/**
 * ContentUri에서 미디어 데이터를 추출하여 Model로 저장한다.
 * getPath() 메소드로 ContentUri에서 파싱한 뒤 실제 저장 위치를 모델에 저장.
 * todo 미디어 데이터를 추출하는 과정에서 dateTaken과 Id를 추출할 수가 없어 임의로 생성 후 저장.
 * @param uri file intent에서 선택한 content:// Uri
 * @author 권혁신
 * @version 1.0.0
 * @since
 **/

fun Context.getFileDataFromUri(uri: Uri): MediaStoreItem? {
    val type = contentResolver.getMediaItemType(uri)             // 아이템 타입.
    val parsedUri = Uri.parse(getPath(uri.toString()))           // 실제 경로.
    val projection = arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME)
    var query = contentResolver.query(
        uri,
        projection,
        null, // selection
        null, //selectionArgs
        null
    )

    when (type) {
        MediaStoreFileType.IMAGE -> {
            query?.use { cursor ->
                val displayNameColumn = cursor.getColumnIndexOrThrow(projection[0])
                if (cursor.moveToFirst()) {
                    val id = System.currentTimeMillis()
                    val dateModified = Date()
                    val displayName = cursor.getString(displayNameColumn)
                    return MediaStoreImage(
                        id,
                        dateModified,
                        displayName,
                        parsedUri,
                        MediaStoreFileType.IMAGE
                    )
                }
            }
        }
        MediaStoreFileType.AUDIO -> {
            query?.use { cursor ->
                val displayNameColumn = cursor.getColumnIndexOrThrow(projection[0])
                if (cursor.moveToFirst()) {
                    val id = System.currentTimeMillis()
                    val dateModified = Date()
                    val displayName = cursor.getString(displayNameColumn)
                    val album = getMediaMetaData(parsedUri, METADATA_KEY_ALBUM)
                    val title = getMediaMetaData(parsedUri, METADATA_KEY_TITLE)
                    val duration = getMediaMetaData(parsedUri, METADATA_KEY_DURATION)
                    return MediaStoreAudio(
                        id = id,
                        dateTaken = dateModified,
                        displayName = displayName,
                        album = album,
                        title = title,
                        _duration = duration,
                        contentUri = parsedUri,
                        type = MediaStoreFileType.AUDIO
                    )
                }
            }
        }
        MediaStoreFileType.VIDEO -> {
            query?.use { cursor ->
                val displayNameColumn = cursor.getColumnIndexOrThrow(projection[0])
                if (cursor.moveToFirst()) {
                    val id = System.currentTimeMillis()
                    val dateModified = Date()
                    val displayName = cursor.getString(displayNameColumn)
                    val _duration = getMediaMetaData(uri, METADATA_KEY_DURATION)
                    return MediaStoreVideo(
                        id,
                        dateModified,
                        displayName,
                        parsedUri,
                        MediaStoreFileType.VIDEO,
                        _duration
                    )
                }
            }
        }
        else -> {
            query?.use { cursor ->
                val displayNameColumn = cursor.getColumnIndexOrThrow(projection[0])
                if (cursor.moveToFirst()) {
                    val id = System.currentTimeMillis()
                    val dateModified = Date()
                    val displayName = cursor.getString(displayNameColumn)
                    return MediaStoreFile(
                        id,
                        dateModified,
                        displayName,
                        parsedUri,
                        MediaStoreFileType.FILE
                    )
                }
            }
        }
    }
    query?.close()
    return null
}

fun ContentResolver.getMediaItemType(uri: Uri): MediaStoreFileType {
    val type = getType(uri)?.run {
        val idx = this.indexOf("/")
        this.substring(0, idx)
    }
    return when (type) {
        TYPE_IMAGE -> MediaStoreFileType.IMAGE
        TYPE_VIDEO -> MediaStoreFileType.VIDEO
        TYPE_AUDIO -> MediaStoreFileType.AUDIO
        else -> MediaStoreFileType.FILE
    }
}

fun Context.getMediaMetaData(uri: Uri, metaData: Int): String? {
    val retriever = MediaMetadataRetriever();
    retriever.setDataSource(this, uri);
    val data = retriever.extractMetadata(metaData);
    retriever.release()
    return when (metaData) {
        METADATA_KEY_DURATION -> data?.toLong().toString()
        METADATA_KEY_ALBUM, METADATA_KEY_TITLE -> data
        else -> null
    }
}

