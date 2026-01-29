package com.lihan.vibeplayer.core.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int?=null,
    val title: String,
    val coverImageUriString: String?
)
