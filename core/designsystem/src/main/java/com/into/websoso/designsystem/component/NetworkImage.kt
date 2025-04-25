package com.into.websoso.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State
import coil.compose.AsyncImagePainter.State.Empty
import coil.compose.AsyncImagePainter.State.Loading
import coil.compose.AsyncImagePainter.State.Success
import com.into.websoso.resource.R.drawable.img_my_library_empty_cat

@Composable
fun NetworkImage(
    contentDescription: String? = null,
    imageUrl: String,
    contentScale: ContentScale = Fit,
    alignment: Alignment = Center,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Image(
            painter = painterResource(id = img_my_library_empty_cat),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
        )
        return
    }

    var imagePainterState by remember { mutableStateOf<State>(Empty) }

    Box {
        when (imagePainterState) {
            is Success -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    alignment = alignment,
                    onState = { state -> imagePainterState = state },
                    modifier = modifier,
                )
            }

            is Loading -> CircularProgressIndicator()

            else -> {
                Image(
                    painter = painterResource(id = img_my_library_empty_cat),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = modifier,
                )
            }
        }
    }
}

@Preview
@Composable
private fun UrlImagePreview() {
    NetworkImage(
        imageUrl = "",
        modifier = Modifier.size(100.dp),
    )
}
