package com.kgmyshin.obsidianwebclipper.utils

import okhttp3.OkHttpClient
import okhttp3.Request

fun fetchHtml(url: String): String {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()

    if (!response.isSuccessful) throw RuntimeException("HTTPエラー: ${response.code}")
    return response.body?.string() ?: throw RuntimeException("本文が空です")
}
