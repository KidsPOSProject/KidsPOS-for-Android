package info.nukoneko.cuc.android.kidspos

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.di.EventBusImpl
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.di.HostSelectionInterceptor
import info.nukoneko.cuc.android.kidspos.event.EventBus
import okhttp3.Interceptor
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin

class MainApplication : Application() {
    private val event: EventBus by inject()
    private val config: GlobalConfig by inject()
    private val hostSelectionInterceptor: Interceptor by inject("hostSelection")

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
        startKoin(this, listOf(coreModule, viewModelModule))

        (event as? EventBusImpl)?.getGlobalEventObserver()?.observeForever { event ->
            when (event) {
                SystemEvent.HostChanged -> {
                    (hostSelectionInterceptor as? HostSelectionInterceptor)?.host = config.baseUrl
                }
            }
        }
    }
}