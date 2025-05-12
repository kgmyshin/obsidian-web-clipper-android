package com.kgmyshin.obsidianwebclipper

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kgmyshin.obsidianwebclipper.utils.fetchHtml
import com.kgmyshin.obsidianwebclipper.utils.htmlToMarkdown
import com.kgmyshin.obsidianwebclipper.utils.markdownHeader
import com.kgmyshin.obsidianwebclipper.utils.saveToObsidian
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedUrl =
            intent?.takeIf { it.action == Intent.ACTION_SEND }?.getStringExtra(Intent.EXTRA_TEXT)

        setContent {
            MaterialTheme {
                MarkdownPreviewScreen(sharedUrl)
            }
        }
    }
}

@Composable
fun MarkdownPreviewScreen(sharedUrl: String?) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var noteName by remember { mutableStateOf("") }
    var notePath by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var contentTemplate by remember { mutableStateOf("") }
    var markdown by remember { mutableStateOf("") }

    // 初回読み込み
    LaunchedEffect(Unit) {
        val loaded = SettingsDataStore.loadSettings(context)
        noteName = loaded.noteName
        notePath = "${loaded.notePath}/${loaded.noteName}"
        tags = loaded.defaultTags
        contentTemplate = loaded.contentTemplate
    }

    LaunchedEffect(sharedUrl) {
        if (!sharedUrl.isNullOrBlank()) {
            isLoading = true
            scope.launch(Dispatchers.IO) {
                try {
                    val html = fetchHtml(sharedUrl)
                    val parsedDoc = Jsoup.parse(html)
                    val pageTitle = parsedDoc.title()
                    val mdHeader = markdownHeader(
                        pageTitle,
                        sharedUrl,
                        Date(),
                        tags
                    )
                    val md = mdHeader + contentTemplate.replace("{{content}}", htmlToMarkdown(html))

                    withContext(Dispatchers.Main) {
                        markdown = md
                        title = pageTitle
                        noteName = noteName.replace("{{title}}", pageTitle)
                        notePath = notePath.replace("{{title}}", pageTitle)
                        isLoading = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        markdown = "エラー: ${e.message}"
                        isLoading = false
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("共有されたURL:", style = MaterialTheme.typography.titleMedium)
        Text(sharedUrl ?: "（なし）", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = noteName,
            onValueChange = { noteName = it },
            label = { Text("ノート名") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notePath,
            onValueChange = { notePath = it },
            label = { Text("ノートの保存場所") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("タイトル") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tags.joinToString(","),
            onValueChange = { tags = it.split(",").map { it.trim() } },
            label = { Text("タグ一覧 （カンマ区切り）") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            OutlinedTextField(
                value = markdown,
                onValueChange = { markdown = it },
                label = { Text("Markdown") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    saveToObsidian(
                        context,
                        noteName,
                        notePath,
                        markdown
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Obsidianに保存")
        }
    }
}

