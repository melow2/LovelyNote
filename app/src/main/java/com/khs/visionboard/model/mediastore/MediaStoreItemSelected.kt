package com.khs.visionboard.model.mediastore

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.khs.visionboard.databinding.BoardMediaItemBinding

data class MediaStoreItemSelected(
    val itemBinding: BoardMediaItemBinding,
    val position: Int,
    val contentUri:Uri,
    val type: MediaStoreFileType,
    val item:Any?
){
    companion object {
        val diffCallback: DiffUtil.ItemCallback<MediaStoreItemSelected> = object : DiffUtil.ItemCallback<MediaStoreItemSelected>() {
            override fun areItemsTheSame(oldItem: MediaStoreItemSelected, newItem: MediaStoreItemSelected): Boolean {
                return oldItem.itemBinding == newItem.itemBinding
            }

            override fun areContentsTheSame(oldItem: MediaStoreItemSelected, newItem: MediaStoreItemSelected): Boolean {
                return oldItem.itemBinding == newItem.itemBinding
            }
        }

    }
}
/*
// 갤러리에 있는 모든 이미지를 갖고 있는 변수.
var galleryItemList: MutableList<GalleryImageItem> = mutableListOf()

// 리스트를 갤러리에 있는 이미지의 갯수 만큼 초기화.
fun Int.initGalleryItems(): MutableList<GalleryImageItem> {
    for (i in 0 until this) {
        galleryItemList.add(GalleryImageItem(i, null,false))
    }
    return galleryItemList
}

// 리스트의 값을 모두 초기화.
fun Int.resetGalleryItem(){
    for(i in 0 until this){
        galleryItemList[i].uri = null
    }
}

fun getGalleryItems() = galleryItemList

 */

