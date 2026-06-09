package com.appinitdev.filtro

// PreferencesManager.kt
import android.content.Context
import androidx.core.content.edit

object PreferencesManager {
    private const val PREFS_NAME = "photo_editor_prefs"
    private const val KEY_LAST_URI = "last_image_uri"

    fun saveLastUri(context: Context, uri: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_LAST_URI, uri)
            }
    }

    fun getLastUri(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_URI, null)
    }
}