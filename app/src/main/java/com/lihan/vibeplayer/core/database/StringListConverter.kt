package com.lihan.vibeplayer.core.database

import androidx.room.TypeConverter

class StringListConverter{

    @TypeConverter
    fun stringToStringList(value: String): List<String>{
        if (value.isBlank()) return emptyList()
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun stringListToString(value: List<String>): String{
        return value.filter { it.isNotBlank() }.joinToString(",")
    }

}