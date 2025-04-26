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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.into.websoso.core.resource.R.drawable.img_my_library_empty_cat

@Composable
fun NetworkImage(
    contentDescription: String? = null,
    imageUrl: String,
    contentScale: ContentScale = Fit,
    alignment: Alignment = Center,
    placeholder: Painter = painterResource(id = img_my_library_empty_cat),
    modifier: Modifier = Modifier,
) {
    val isPreview = LocalInspectionMode.current
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        },
    )

    Box(
        modifier = modifier,
        contentAlignment = Center,
    ) {
        if (isLoading && !isPreview) CircularProgressIndicator(modifier = Modifier.size(size = 48.dp))

        Image(
            painter = if (!isError && !isPreview) painter else placeholder,
            contentDescription = contentDescription,
            contentScale = contentScale,
            alignment = alignment,
            modifier = Modifier.matchParentSize(),
        )
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
