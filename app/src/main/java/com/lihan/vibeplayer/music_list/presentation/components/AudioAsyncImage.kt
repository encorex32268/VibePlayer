package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest

@Composable
fun AudioAsyncImage(
    model: Any?,
    cacheKey: String,
    modifier: Modifier = Modifier,
    placeholder: Painter?=null,
    error: Painter?=null,
    contentDescription: String?=null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
){
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest
            .Builder(context)
            .data(model)
            .apply {
                if (!cacheKey.isNullOrBlank()) {
                    diskCacheKey(cacheKey)
                    memoryCacheKey(cacheKey)
                }
            }
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = placeholder,
        error = error,
        contentScale = ContentScale.Crop,
        onError = onError,
        onLoading = onLoading
    )
}