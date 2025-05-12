package com.kgmyshin.obsidianwebclipper

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object SettingsDataStore {
    private val Context.dataStore by preferencesDataStore("obsidian_settings")

    private val NOTE_NAME = stringPreferencesKey("note_name")
    private val NOTE_PATH = stringPreferencesKey("note_path")
    private val DEFAULT_TAGS = stringPreferencesKey("default_tags")
    private val CONTENT_TEMPLATE = stringPreferencesKey("content_template")

    suspend fun resetSettings(context: Context) {
        saveSettings(
            context = context,
            noteName = "{{title}}",
            notePath = "",
            contentTemplate = "{{content}}",
            defaultTags = listOf("clipping")
        )
    }

    suspend fun saveSettings(
        context: Context,
        noteName: String,
        notePath: String,
        defaultTags: List<String>,
        contentTemplate: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[NOTE_NAME] = noteName
            prefs[NOTE_PATH] = notePath
            prefs[CONTENT_TEMPLATE] = contentTemplate
            prefs[DEFAULT_TAGS] = defaultTags.joinToString(",")
        }
    }

    suspend fun loadSettings(context: Context): SettingsState {
        val prefs = context.dataStore.data.first()

        val noteName = prefs[NOTE_NAME] ?: "{{title}}"
        val notePath = prefs[NOTE_PATH] ?: ""
        val defaultTags =
            prefs[DEFAULT_TAGS]?.split(",")?.map { it.trim() } ?: listOf("clipping")
        val contentTemplate = prefs[CONTENT_TEMPLATE] ?: "{{content}}"

        return SettingsState(noteName, notePath, defaultTags, contentTemplate)
    }

}

data class SettingsState(
    val noteName: String,
    val notePath: String,
    val defaultTags: List<String>,
    val contentTemplate: String
)
