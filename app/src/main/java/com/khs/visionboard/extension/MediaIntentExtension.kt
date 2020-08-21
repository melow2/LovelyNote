package com.khs.visionboard.extension

import android.R
import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import com.khs.visionboard.extension.Constants.RC_GET_AUDIO


var outputMediaFileUri: Uri? = null
/**
 * 카메라 선택 인텐트.
 * - 아이디, 날짜, 파일이름으로 uri를 만들고 파일을 생성 하여 intent의 output으로 저장한다.
 *
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-21 오후 1:27
 **/
fun Context.openCameraIntent(): Intent? {
/*    this.createMediaFile(MediaStoreFileType.IMAGE,null)?.let {
        outputMediaFileUri = if(Build.VERSION.SDK_INT<24){
            // create Uri with 'file://' prefix
            Uri.fromFile(it)
        }else{
            //create Uri with 'content://' prefix
            FileProvider.getUriForFile(this, applicationContext.packageName + ".fileprovider",it)
        }
    }*/
    // Camera.
    val cameraIntents: MutableList<Intent> = ArrayList()
    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val packageManager = packageManager
    val listCam = packageManager.queryIntentActivities(captureIntent, 0)
    val currentTime = System.currentTimeMillis()

    ContentValues().apply {
        put(MediaStore.Images.ImageColumns._ID, currentTime)
        put(MediaStore.Images.ImageColumns.DATE_TAKEN, currentTime)
        put(MediaStore.Images.ImageColumns.DISPLAY_NAME, currentTimeStamp() + "_IMAGE")
    }.run {
        outputMediaFileUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this)
        // 여기에서 외부 경로로 저장.
    }

    for (res in listCam) {
        val packageName = res.activityInfo.packageName
        Intent(captureIntent).apply {
            component = ComponentName(packageName, res.activityInfo.name)
            setPackage(packageName)
            putExtra(MediaStore.EXTRA_OUTPUT, outputMediaFileUri)
        }.let {
            cameraIntents.add(it)
        }
    }
    // Filesystem.
    val galleryIntent = Intent().apply {
        type = "image/*"
        action = Intent.ACTION_GET_CONTENT
    }
    // Chooser of filesystem options.
    return Intent.createChooser(galleryIntent, "Select Image Source").apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray<Parcelable>())
    }
}

/**
 * 비디오 선택 인텐트
 * - 카메라와 다르게 비디오는 자동으로 파일이 저장이 되는 것을 확인.
 * - 따라서 저장할 uri가 따로 필요없고, activity result에서 data값으로 확인하면 됨.
 * - 다시 말해 activity result의 data 값의 Uri에서 비디오의 데이터를 확인할 수 있음.
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-21 오후 1:33
 **/
fun Context.openVideoIntent(): Intent? {

    // Video.
    val videoIntents: MutableList<Intent> = ArrayList()
    val captureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
    val packageManager = packageManager
    val listCam = packageManager.queryIntentActivities(captureIntent, 0)

    for (res in listCam) {
        val packageName = res.activityInfo.packageName
        Intent(captureIntent).apply {
            component = ComponentName(packageName, res.activityInfo.name)
            setPackage(packageName)
        }.let {
            videoIntents.add(it)
        }
    }
    // Filesystem.
    val galleryIntent = Intent().apply {
        type = "video/*"
        action = Intent.ACTION_GET_CONTENT
    }
    // Chooser of filesystem options.
    return Intent.createChooser(galleryIntent, "Select Source").apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, videoIntents.toTypedArray<Parcelable>())
    }
}