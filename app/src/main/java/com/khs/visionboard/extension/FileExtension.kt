package com.khs.visionboard.extension

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import com.khs.visionboard.model.mediastore.MediaStoreFileType
import org.apache.commons.io.IOUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


fun getExtension(fileStr: String): String {
    return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length)
}

fun Context.viewFile(filePath: Uri?, fileName: String?) {
    val fileLinkIntent = Intent(Intent.ACTION_VIEW)
    fileLinkIntent.addCategory(Intent.CATEGORY_DEFAULT)
    val file = File(filePath.toString(), fileName)
    //확장자 구하기
    val fileExtend = getExtension(file.absolutePath)
    // 파일 확장자 별로 mime type 지정해 준다.

    if (fileExtend.equals("mp3", ignoreCase = true)) {
        fileLinkIntent.setDataAndType(Uri.fromFile(file), "audio/*")
    } else if (fileExtend.equals("mp4", ignoreCase = true)) {
        fileLinkIntent.setDataAndType(Uri.fromFile(file), "vidio/*")
    } else if (fileExtend.equals("jpg", ignoreCase = true)
        || fileExtend.equals("jpeg", ignoreCase = true)
        || fileExtend.equals("gif", ignoreCase = true)
        || fileExtend.equals("png", ignoreCase = true)
        || fileExtend.equals("bmp", ignoreCase = true)
    ) {
        fileLinkIntent.setDataAndType(Uri.fromFile(file), "image/*")
    } else if (fileExtend.equals("txt", ignoreCase = true)) {
        fileLinkIntent.setDataAndType(Uri.fromFile(file), "text/*")
    } else if (fileExtend.equals("doc", ignoreCase = true)
        || fileExtend.equals("docx", ignoreCase = true)
    ) {
        fileLinkIntent.setDataAndType(Uri.fromFile(file), "application/msword")
    } else if (fileExtend.equals("xls", ignoreCase = true)
        || fileExtend.equals("xlsx", ignoreCase = true)
    ) {
        fileLinkIntent.setDataAndType(
            Uri.fromFile(file),
            "application/vnd.ms-excel"
        )
    } else if (fileExtend.equals("ppt", ignoreCase = true)
        || fileExtend.equals("pptx", ignoreCase = true)
    ) {
        fileLinkIntent.setDataAndType(
            Uri.fromFile(file),
            "application/vnd.ms-powerpoint"
        )
    } else if (fileExtend.equals("pdf", ignoreCase = true)) {
        fileLinkIntent.setDataAndType(filePath, "application/pdf")
    } else if (fileExtend.equals("hwp", ignoreCase = true)) {
        fileLinkIntent.setDataAndType(
            Uri.fromFile(file),
            "application/haansofthwp"
        )
    }
    val pm: PackageManager = this.packageManager
    val list = pm.queryIntentActivities(
        fileLinkIntent,
        PackageManager.GET_META_DATA
    )
    if (list.size == 0) {
        Toast.makeText(
            this, "$fileName 파일을 실행할 수 있는 앱이 필요합니다.",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        this.startActivity(fileLinkIntent)
    }
}

fun Context.copyFileUri(sourceUri: Uri?, destFile: File): Uri? {
    try {
        val inputStream: InputStream = sourceUri?.let { contentResolver.openInputStream(it) } ?: return null
        val outputStream: OutputStream = FileOutputStream(destFile)
        IOUtils.copy(inputStream, outputStream)
        inputStream.close()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return if (Build.VERSION.SDK_INT < 24) {
        Uri.fromFile(destFile)
    } else {
        FileProvider.getUriForFile(this, applicationContext.packageName + ".fileprovider", destFile)
    }
}

fun Context.createMediaFile(mediaType: MediaStoreFileType, fileName: String?): File? {
    val dirPath = File(getExternalFilesDir(null), SimpleDateFormat("yyyyMMdd").format(Date()))
    if (!dirPath.exists()) {
        dirPath.mkdirs()
    }
    return when (mediaType) {
        MediaStoreFileType.IMAGE -> {
            File(dirPath, currentTimeStamp() + "_IMAGE.png")
        }
        MediaStoreFileType.VIDEO -> {
            File(dirPath, currentTimeStamp() + "_VIDEO.mp4")
        }
        MediaStoreFileType.AUDIO -> {
            File(dirPath, currentTimeStamp() + "_AUDIO.mp3")
        }
        else -> {
            val name = fileName?.substringBeforeLast(".")
            val extension = fileName?.substringAfterLast(".")
            File(dirPath, "$name.$extension")
        }
    }
}


fun Uri.delete(contentResolver: ContentResolver) {
    contentResolver.delete(this, null, null)
}

fun Context.clearCacheData() {
    val cache = File(cacheDir, SimpleDateFormat("yyyyMMdd").format(Date()))
    if (cache.isDirectory) {
        val children: Array<String> = cache.list()
        for (i in children.indices) {
            File(cache, children[i]).delete()
        }
    }
}

fun Context.getFilePathFromContentUri(selectedVideoUri: Uri): String? {
    val filePath: String
    val filePathColumn = arrayOf(MediaColumns.DATA)
    val cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null)
    cursor!!.moveToFirst()
    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
    filePath = cursor.getString(columnIndex)
    cursor.close()
    return filePath
}


fun Context.getPath(string: String?): String? {
    return getPath(this, Uri.parse(string))
}

fun getPath(context:Context,uri: Uri): String? {
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
            // TODO handle non-primary volumes
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                java.lang.Long.valueOf(id)
            )
            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context The context.
 * @param uri The Uri to query.
 * @param selection (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(
            uri!!, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val column_index: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        if (cursor != null) cursor.close()
    }
    return null
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}