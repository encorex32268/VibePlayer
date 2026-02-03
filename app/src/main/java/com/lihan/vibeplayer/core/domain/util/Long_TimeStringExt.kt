package com.lihan.vibeplayer.core.domain.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun Long.toTimeString(): String{
    val mins = this / 60 / 1000
    var seconds = (this / 1000) % 60
    if (mins == 0L && seconds ==0L){
        seconds += 1
    }
    return String.format(
        "%02d:%02d",mins,seconds
    )
}

@SuppressLint("DefaultLocale")
fun Long.toTimeStringWithoutZero(): String{
    val mins = this / 60 / 1000
    val seconds = (this / 1000) % 60
    return String.format(
        "%2d:%02d",mins,seconds
    )
}