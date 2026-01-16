package com.lihan.vibeplayer.core.presentation.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource


@Stable
sealed interface UiText {

    @Stable
    data class DynamicString(val value: String): UiText

    @Stable
    class StringResource(
        @param:StringRes val resId: Int,
        val args: Array<Any> = arrayOf()
    ): UiText

    @Composable
    fun asString(): String {
        return when(this){
            is DynamicString -> value
            is StringResource -> stringResource(resId,*args)
        }
    }

    fun asString(context: Context): String {
        return when(this){
            is DynamicString -> value
            is StringResource -> context.getString(resId,*args)
        }
    }

    suspend fun asStringAsync(context: Context): String {
        return when(this){
            is DynamicString -> value
            is StringResource -> context.getString(resId,*args)
        }
    }

}