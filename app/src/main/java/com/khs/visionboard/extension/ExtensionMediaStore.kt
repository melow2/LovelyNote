package com.khs.visionboard.extension

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.khs.visionboard.model.mediastore.MediaStoreFileType
import com.khs.visionboard.model.mediastore.MediaStoreAudio
import com.khs.visionboard.model.mediastore.MediaStoreImage
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * image allowed directories are [DCIM, Pictures]
 * audio allowed directories are [Alarms, Music, Notifications, Podcasts, Ringtones]
 * video allowed directories are [DCIM, Movies]
 *
 * @SuppressLint("InlinedApi"): 이전플랫폼에서 작동하거나 작동하지 않을 수 있음.
 * */


fun ContentResolver.getMediaStoreImageFiles(
    limit: Int,
    offset: Int,
    type: MediaStoreFileType
): MutableList<MediaStoreImage> {
    val contentResolver = this
    val fileList = mutableListOf<MediaStoreImage>()
    val orderBy = MediaStore.Images.Media.DATE_TAKEN

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        orderBy
    )

    val selection = "$orderBy>= ?"
    val selectionArgs = arrayOf(dateToTimestamp(day = 1, month = 1, year = 1970).toString())
    val sortOrder = "$orderBy DESC LIMIT $limit OFFSET $offset"

    val query = contentResolver.query(
        type.externalContentUri,
        projection,
        null,
        null,
        sortOrder
    )

    query?.use { cursor->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val dateTakenColumn = cursor.getColumnIndexOrThrow(orderBy)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateTaken = Date(cursor.getLong(dateTakenColumn))
            val displayName = cursor.getString(displayNameColumn)
            val contentUri = Uri.withAppendedPath(type.externalContentUri, id.toString())
            fileList.add(MediaStoreImage(id, dateTaken, displayName, contentUri, type))
        }
    }
    // fileList.size.initGalleryItems()
    return fileList
}


/**
 * orderBy에서 DATE_TAKEN을 쓸 수가 없음.
 * */
fun ContentResolver.getMediaStoreAudioFiles(
    limit: Int,
    offset: Int,
    type: MediaStoreFileType
): MutableList<MediaStoreAudio> {
    val contentResolver = this
    val fileList = mutableListOf<MediaStoreAudio>()
    var orderBy = MediaStore.Audio.Media.DATE_ADDED

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
        dateToTimestamp(day = 1, month = 1, year = 1970).toString()
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
        val dateAddedColumn = cursor.getColumnIndexOrThrow(orderBy)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val dateAdded = Date(cursor.getLong(dateAddedColumn))
            val displayName = cursor.getString(displayNameColumn)
            val album = cursor.getString(albumColumn)
            val title = cursor.getString(titleColumn)
            val duration = cursor.getString(durationColumn)
            val contentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())
            fileList.add(
                MediaStoreAudio(
                    id = id,
                    dateTaken = dateAdded,
                    displayName = displayName,
                    album = album,
                    title = title,
                    duration = duration,
                    contentUri = contentUri,
                    type = MediaStoreFileType.AUDIO
                )
            )
        }
    }
    return fileList
}

fun createFile(
    context: Context,
    fileName: String,
    fileType: MediaStoreFileType,
    fileContents: ByteArray
) {
    val contentValues = ContentValues()

    when (fileType) {
        MediaStoreFileType.IMAGE -> {
            contentValues.put(
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + fileType.pathByDCIM
            )
        }
        MediaStoreFileType.AUDIO -> {
            contentValues.put(
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                Environment.DIRECTORY_MUSIC + fileType.pathByDCIM
            )
        }
        MediaStoreFileType.VIDEO -> {
            contentValues.put(
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                Environment.DIRECTORY_MOVIES + fileType.pathByDCIM
            )
        }
    }
    contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
    contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, fileType.mimeType)
    contentValues.put(MediaStore.Files.FileColumns.IS_PENDING, 1)

    val uri = context.contentResolver.insert(
        fileType.externalContentUri,
        contentValues
    )

    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri!!, "w", null)

    val fileOutputStream = FileOutputStream(parcelFileDescriptor!!.fileDescriptor)
    fileOutputStream.write(fileContents)
    fileOutputStream.close()

    contentValues.clear()
    contentValues.put(MediaStore.Files.FileColumns.IS_PENDING, 0)
    context.contentResolver.update(uri, contentValues, null, null)
}

fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
    SimpleDateFormat("dd.MM.yyyy").let { formatter ->
        formatter.parse("$day.$month.$year")?.time ?: 0
    }