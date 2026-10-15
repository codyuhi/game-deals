package online.codyuhi.gamedeals.di

import android.content.Context
import androidx.room.Room
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import online.codyuhi.gamedeals.data.api.CheapSharkApi
import online.codyuhi.gamedeals.data.db.GameDealsDatabase
import online.codyuhi.gamedeals.data.repository.GameDealsRepository
import online.codyuhi.gamedeals.data.repository.SettingsRepository
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

interface AppContainer {
    val settingsRepository: SettingsRepository
    val gameDealsRepository: GameDealsRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.cheapshark.com/api/1.0/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api: CheapSharkApi = retrofit.create(CheapSharkApi::class.java)

    private val database: GameDealsDatabase = Room.databaseBuilder(
        context,
        GameDealsDatabase::class.java,
        "gamedeals.db",
    ).fallbackToDestructiveMigration(dropAllTables = true).build()

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context)
    }

    override val gameDealsRepository: GameDealsRepository by lazy {
        GameDealsRepository(
            context = context,
            api = api,
            storeDao = database.storeDao(),
            dealDao = database.dealDao(),
            favoriteDao = database.favoriteDao(),
        )
    }
}
