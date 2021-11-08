package com.lch.cl

import androidx.core.content.FileProvider
import com.lch.cln.BuildConfig
import java.io.File

fun File.mimeType(): String? {
    return try {
        val uri = FileProvider.getUriForFile(
            Contexter.getContext(),
            "${BuildConfig.APPLICATION_ID}.file.provider", this
        )
        val type = Contexter.getContext().contentResolver.getType(uri)
        type
    } catch (e: Throwable) {
        null
    }
}