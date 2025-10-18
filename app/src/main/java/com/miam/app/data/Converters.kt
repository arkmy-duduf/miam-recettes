package com.miam.app.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(list: List<String>?): String = json.encodeToString(list ?: emptyList())

    @TypeConverter
    fun toStringList(data: String): List<String> = try { json.decodeFromString(data) } catch (_: Exception){ emptyList() }
}