package com.khs.visionboard.model.gallery

import androidx.recyclerview.widget.DiffUtil

data class PhotoItem(
    val imageDataPath:String
){
    companion object {
       val diffCallback = object : DiffUtil.ItemCallback<PhotoItem>() {
            override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean =
                oldItem.imageDataPath == newItem.imageDataPath

            override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean =
                oldItem.imageDataPath == newItem.imageDataPath
        }
    }
}