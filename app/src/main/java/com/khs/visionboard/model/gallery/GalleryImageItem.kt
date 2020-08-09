package com.khs.visionboard.model.gallery

import android.net.Uri

data class GalleryImageItem(
    var position: Int,
    var uri: Uri? = null
) {
    companion object {
        val result: MutableList<GalleryImageItem> = mutableListOf()
        fun getGalleryItems() = result
    }
}
fun Int.initGalleryItems(): MutableList<GalleryImageItem> {
    for(i in 0 until this) {
        GalleryImageItem.result.add(GalleryImageItem(i, null))
    }
    return GalleryImageItem.result
}


