package com.ile.syrin_x.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import java.io.File
import java.io.FileOutputStream

fun getUriFromDrawable(context: Context, @DrawableRes drawableResId: Int): Uri {
    val resources = context.resources
    val drawable = ResourcesCompat.getDrawable(resources, drawableResId, null)
    val bitmap = (drawable as BitmapDrawable).bitmap

    val file = File(context.cacheDir, "default_profile.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}