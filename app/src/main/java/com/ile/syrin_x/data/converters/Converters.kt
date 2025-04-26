package com.ile.syrin_x.data.converters

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.ile.syrin_x.data.enums.MusicSource
import java.time.LocalDate

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromMusicSource(musicSource: MusicSource): String = musicSource.toString()

    @TypeConverter
    fun toMusicSource(musicSourceString: String): MusicSource = MusicSource.valueOf(musicSourceString)

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? =
        date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? =
        value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? =
        list?.let { gson.toJson(it) }

    @TypeConverter
    fun toStringList(json: String?): List<String>? =
        json?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(it, type)
        }
}