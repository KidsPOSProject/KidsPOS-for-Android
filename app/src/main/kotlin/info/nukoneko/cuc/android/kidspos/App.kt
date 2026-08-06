package info.nukoneko.cuc.android.kidspos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import info.nukoneko.cuc.android.kidspos.api.ServerSelectionInterceptor
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
open class App : Application() {
    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var serverSelectionInterceptor: ServerSelectionInterceptor

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        settingsRepository.serverAddress
            .onEach { serverSelectionInterceptor.serverAddress = it }
            .launchIn(applicationScope)
    }
}
