package com.kgmyshin.obsidianwebclipper.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun markdownHeader(
    title: String,
    source: String,
    created: Date,
    tags: List<String>
): String {

    /* サンプルは下記
---
title: "メモ管理は Obsidian in  Cursor が最強｜松濤Vimmer"
source: "https://note.com/shotovim/n/na1d91f10c1d0"
created: 2025-05-07
tags:
  - "clippings"
---
     */

    // created は YYYY-MM-DD 形式で渡す
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val header = StringBuilder()
    header.append("---\n")
    header.append("title: \"$title\"\n")
    header.append("source: \"$source\"\n")
    header.append("created: ${dateFormat.format(created)}\n")
    header.append("tags:\n")
    tags.forEach { tag ->
        header.append("  - \"$tag\"\n")
    }
    header.append("---\n")

    return header.toString()
}