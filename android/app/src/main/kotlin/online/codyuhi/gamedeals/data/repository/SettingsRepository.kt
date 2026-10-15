package online.codyuhi.gamedeals.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import online.codyuhi.gamedeals.ui.theme.ThemeMode

data class Settings(
    val themeMode: ThemeMode = ThemeMode.System,
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gamedeals_settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val theme = stringPreferencesKey("cedar_theme")
    }

    val settings: Flow<Settings> = context.dataStore.data.map { p ->
        Settings(
            themeMode = p[Keys.theme]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.System,
        )
    }

    suspend fun current(): Settings = settings.first()

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.theme] = mode.name }
    }
}
