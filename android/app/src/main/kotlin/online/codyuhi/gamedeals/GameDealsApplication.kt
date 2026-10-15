package online.codyuhi.gamedeals

import android.app.Application
import online.codyuhi.gamedeals.di.AppContainer
import online.codyuhi.gamedeals.di.DefaultAppContainer

class GameDealsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
