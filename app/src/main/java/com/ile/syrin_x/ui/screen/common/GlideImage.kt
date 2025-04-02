package com.ile.syrin_x.ui.screen.common

import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide

@Composable
fun GlideImage(url: String?, contentDescription: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    this.contentDescription = contentDescription
                    Glide.with(context)
                        .load(url)
                        .into(this)
                }
            }
        )
    }
}