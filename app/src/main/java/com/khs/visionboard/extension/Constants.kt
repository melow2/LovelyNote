package com.khs.visionboard.extension

object Constants {
    /** Fragment Tag */
    const val TAG_PARCELABLE_BOARD = "ITEM_BOARD"         // parcelable board item
    const val TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT"     // list fragment tag
    const val TAG_DETAIL_FRAGMENT = "TAG_DETAIL_FRAGMENT" // detail fragment tag
    const val TAG_ADD_FRAGMENT = "TAG_DETAIL_FRAGMENT"    // add fragment tag

    /** Media Type */
    const val IMAGE_MIME_TYPE: String = "image/*"
    const val AUDIO_MIME_TYPE: String = "audio/*"
    const val VIDEO_MIME_TYPE: String = "video/*"

    const val IMAGE_PATH_BY_DCIM: String = "/image"
    const val AUDIO_PATH_BY_DCIM: String = "/audio"
    const val VIDEO_PATH_BY_DCIM: String = "/video"

    /** RecyclerView */
    const val MEDIA_RCV_HEIGHT = 1000
    const val GALLERY_ITEM_RANGE = 3
    const val SELECTED_ITEM_RANGE = 2

    /** Animation Duration*/
    const val DURATION_FADE_IN: Long = 1000
    const val DURATION_FADE_OUT: Long = 200
}