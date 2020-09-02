package com.khs.lovelynote.extension

object Constants {

    /** Fragment Tag */
    const val TAG_NOTE_ID = "ITEM_NOTE"                             // note id
    const val TAG_MEDIA_ITEM_SIZE = "TAG_MEDIA_ITEM_SIZE"           // media item size
    const val TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT"               // list fragment tag
    const val TAG_DETAIL_FRAGMENT = "TAG_DETAIL_FRAGMENT"           // detail fragment tag
    const val TAG_ADD_FRAGMENT = "TAG_DETAIL_FRAGMENT"              // add fragment tag
    const val TAG_AUDIO_DIALOG_FRAGMENT = "CURRENT_AUDIO_PLAYING_DIALOG"   // 오디오 파일 재생 fragment
    const val TAG_IMAGE_DIALOG = "TAG_IMAGE_DIALOG"

    /** Media Type */
    const val TAG_RECORD = "RECORD"
    const val TYPE_IMAGE = "image"
    const val TYPE_AUDIO = "audio"
    const val TYPE_VIDEO = "video"
    const val IMAGE_MIME_TYPE: String = "image/*"
    const val AUDIO_MIME_TYPE: String = "audio/*"
    const val VIDEO_MIME_TYPE: String = "video/*"
    const val FILE_MIME_TYPE: String = "file/*"
    const val TXT_MIME_TYPE:String = "text/*"
    const val APPLICATION_MSWORD = "application/msword"
    const val APPLICATION_XLSX = "application/vnd.ms-excel"
    const val APPLICATION_PPTX= "application/vnd.ms-powerpoint"
    const val APPLICATION_HWP = "application/haansofthwp"
    const val APPLICATION_PDF = "application/pdf"

    /** Constant File Type */
    const val MP3 = "mp3"
    const val MP4 = "mp4"
    const val JPG = "jpg"
    const val JPEG = "jpeg"
    const val GIF = "gif"
    const val PNG = "png"
    const val BMP = "bmp"
    const val TXT = "txt"
    const val DOC = "doc"
    const val DOCX = "docx"
    const val XLS = "xls"
    const val XLSX= "xlsx"
    const val PPT = "ppt"
    const val PPTX= "pptx"
    const val HWP ="hwp"
    const val PDF = "pdf"
    /** PagingLibrary*/
    /*
        최초 500개를 불러 들이고, 500개부터 다음페이지를 미리 불러오니, 바로 다음페이지 100개 불러들임.
        페이지가 넘어갈 때 마다 100개씩 자동으로 읽음.
     */
    const val PAGE_SIZE = 50                   // 페이징당 개수.
    const val INITIAL_LOAD_SIZE_HINT = 100      // 최초 개수.
    const val PREFETCH_DISTANCE = 1000           // 500개부터 다음 페이지를 미리 불러오기.

    /** RecyclerView */
    const val MEDIA_RCV_HEIGHT = 720            // expand height
    const val GALLERY_ITEM_RANGE = 2            // 이미지 아이템 시작 범위
    const val AUDIO_ITEM_RANGE = 2              // 오디오 아이템 시작 범위
    const val VIDEO_ITEM_RANGE = 2              // 비디오 아이템 시작 범위
    const val SELECTED_ITEM_RANGE = 2           // 선택한 아이템 시작 범위

    /** Animation Duration*/
    const val DURATION_FADE_IN: Long = 900
    const val DURATION_FADE_OUT: Long = 300

    /** ActivityForResult */
    const val RC_GET_CONTENT = 1007 // 파일 열기.
    const val RC_GET_CAMERA = 1008  // 카메라 열기.
    const val RC_GET_VIDEO = 1009  // 비디오 열기.

}