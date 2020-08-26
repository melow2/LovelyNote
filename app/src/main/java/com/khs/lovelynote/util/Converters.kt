package com.khs.lovelynote.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.khs.lovelynote.model.mediastore.MediaStoreItem
import java.lang.reflect.Type
import java.util.*

class Converters {
    @TypeConverter
    fun fromValuesToList(value: List<MediaStoreItem?>?): String? {
        if (value == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<MediaStoreItem?>?>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toOptionValuesList(value: String?): List<MediaStoreItem?>? {
        if (value == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<MediaStoreItem?>?>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}