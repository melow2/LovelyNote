package com.khs.visionboard.model.mediastore

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil

data class SelectedMediaStoreItem(
    val itemBinding: ViewDataBinding,
    val selectedItem:SelectedItem
) {
    companion object {
        val diffCallback: DiffUtil.ItemCallback<SelectedMediaStoreItem> =
            object : DiffUtil.ItemCallback<SelectedMediaStoreItem>() {
                override fun areItemsTheSame(
                    oldItem: SelectedMediaStoreItem,
                    newItem: SelectedMediaStoreItem
                ): Boolean {
                    return oldItem.itemBinding == newItem.itemBinding
                }

                override fun areContentsTheSame(
                    oldItem: SelectedMediaStoreItem,
                    newItem: SelectedMediaStoreItem
                ): Boolean {
                    return oldItem.selectedItem.contentUri == newItem.selectedItem.contentUri
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

