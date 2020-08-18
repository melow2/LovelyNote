package com.khs.visionboard.extension

object Constants {
    /** Fragment Tag */
    const val TAG_PARCELABLE_BOARD = "ITEM_BOARD"         // parcelable board item
    const val TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT"     // list fragment tag
    const val TAG_DETAIL_FRAGMENT = "TAG_DETAIL_FRAGMENT" // detail fragment tag
    const val TAG_ADD_FRAGMENT = "TAG_DETAIL_FRAGMENT"    // add fragment tag
    const val TAG_AUDIO_DIALOG_FRAGMENT = "CURRENT_AUDIO_PLAYING"

    /** Media Type */

    const val TYPE_IMAGE = "image"
    const val TYPE_AUDIO = "audio"
    const val TYPE_VIDEO = "video"

    const val IMAGE_MIME_TYPE: String = "image/*"
    const val AUDIO_MIME_TYPE: String = "audio/*"
    const val VIDEO_MIME_TYPE: String = "video/*"
    const val FILE_MIME_TYPE: String = "file/*"

    const val IMAGE_PATH_BY_DCIM: String = "/image"
    const val AUDIO_PATH_BY_DCIM: String = "/audio"
    const val VIDEO_PATH_BY_DCIM: String = "/video"
    const val FILE_PATH_BY_DCIM: String = "/file"

    /*
        최초 500개를 불러 들이고, 500개부터 다음페이지를 미리 불러오니, 바로 다음페이지 100개 불러들임.
        페이지가 넘어갈 때 마다 100개씩 자동으로 읽음.
     */
    const val PAGE_SIZE = 100                   // 페이징당 개수.
    const val INITIAL_LOAD_SIZE_HINT = 500      // 최초 개수.
    const val PREFETCH_DISTANCE = 500           // 500개부터 다음 페이지를 미리 불러오기.

    /** RecyclerView */
    const val MEDIA_RCV_HEIGHT = 720
    const val GALLERY_ITEM_RANGE = 2
    const val AUDIO_ITEM_RANGE = 2
    const val VIDEO_ITEM_RANGE = 2
    const val SELECTED_ITEM_RANGE = 2

    /** Animation Duration*/
    const val DURATION_FADE_IN: Long = 1000
    const val DURATION_FADE_OUT: Long = 300



}