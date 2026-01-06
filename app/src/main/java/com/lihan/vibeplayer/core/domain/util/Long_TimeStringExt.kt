package com.lihan.vibeplayer.core.domain.util

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