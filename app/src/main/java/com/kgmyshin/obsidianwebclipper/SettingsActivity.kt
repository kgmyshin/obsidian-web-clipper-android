package com.kgmyshin.obsidianwebclipper

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SettingsScreen()
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var noteName by remember { mutableStateOf("") }
    var notePath by remember { mutableStateOf("") }
    var defaultTags by remember { mutableStateOf(listOf<String>()) }
    var contentTemplate by remember { mutableStateOf("") }

    // 初回読み込み
    LaunchedEffect(Unit) {
        val loaded = SettingsDataStore.loadSettings(context)
        noteName = loaded.noteName
        notePath = loaded.notePath
        defaultTags = loaded.defaultTags
        contentTemplate = loaded.contentTemplate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ノート名
        Text("ノート名")
        OutlinedTextField(
            value = noteName,
            onValueChange = { noteName = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ノートパス
        Text("ノートの場所")
        OutlinedTextField(
            value = notePath,
            onValueChange = { notePath = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Text("デフォルトタグ一覧（,区切り）")
        OutlinedTextField(
            value = defaultTags.joinToString(","),
            onValueChange = { defaultTags = it.split(",").map { it.trim() } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // ノート本文テンプレート
        Text("ノートの内容")
        OutlinedTextField(
            value = contentTemplate,
            onValueChange = { contentTemplate = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            maxLines = 10
        )

        Spacer(Modifier.height(24.dp))

        // 保存ボタン
        Button(
            onClick = {
                scope.launch {
                    SettingsDataStore.saveSettings(
                        context,
                        noteName,
                        notePath,
                        defaultTags,
                        contentTemplate
                    )
                    Toast.makeText(context, "設定を保存しました", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("設定を保存")
        }

        Spacer(Modifier.height(16.dp))

        // 初期値に戻すボタン
        Button(
            onClick = {
                scope.launch {
                    SettingsDataStore.resetSettings(context)
                    val loaded = SettingsDataStore.loadSettings(context)
                    noteName = loaded.noteName
                    notePath = loaded.notePath
                    contentTemplate = loaded.contentTemplate
                    Toast.makeText(context, "初期値に戻しました", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("初期値に戻す")
        }

        Spacer(Modifier.height(32.dp))
    }
}

