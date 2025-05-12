package com.kgmyshin.obsidianwebclipper.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun saveToObsidian(
    context: Context,
    noteName: String,
    noteFile: String,
    noteContent: String
) {
    // Obsidian の URI スキームを使用してノートを保存
    val encodedContent = encodeUriComponent(noteContent)
    val encodedFile = encodeUriComponent(noteFile)
    val encodedName = encodeUriComponent(noteName)

    val uri =
        "obsidian://new?content=$encodedContent&name=$encodedName&file=$encodedFile".toUri()

    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun encodeUriComponent(value: String): String {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
        .replace("+", "%20")
}