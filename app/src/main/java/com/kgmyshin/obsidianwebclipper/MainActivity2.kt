package com.kgmyshin.obsidianwebclipper

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ObsidianClipperScreen()
            }
        }
    }
}

@Composable
fun ObsidianClipperScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                // 固定ノート内容
                val noteContent = "# 固定ノート\n\nこれは固定された内容です。\n\n#sample #obsidian"
                val encodedContent =
                    URLEncoder.encode(noteContent, StandardCharsets.UTF_8.toString())

                val noteName = ""
                val file = "Daily/${noteName}"
                val encodedFile = URLEncoder.encode(file, StandardCharsets.UTF_8.toString())
                val encodedName = URLEncoder.encode(noteName, StandardCharsets.UTF_8.toString())

                val uri =
                    "obsidian://new?content=$encodedContent&name=$encodedName&file=$encodedFile".toUri()

                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Obsidian に送る")
        }
    }
}