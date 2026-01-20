package com.lihan.vibeplayer.core.data.local

import androidx.room.TypeConverter

class StringListConverter{

    @TypeConverter
    fun stringToStringList(value: String): List<String>{
        return value.split(",").toList()
    }

    @TypeConverter
    fun stringListToString(value: List<String>): String{
        return value.joinToString(",")
    }



}