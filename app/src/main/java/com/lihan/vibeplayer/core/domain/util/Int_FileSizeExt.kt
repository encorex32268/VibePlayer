package com.lihan.vibeplayer.core.domain.util


val Int.kb: Long
    get() = this * 1024L

val Int.mb: Long
    get() = this.kb * 1024L