package info.nukoneko.cuc.android.kidspos

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.di.ServerSelectionInterceptor
import info.nukoneko.cuc.android.kidspos.di.module.apiModule
import info.nukoneko.cuc.android.kidspos.di.module.coreModule
import info.nukoneko.cuc.android.kidspos.di.module.viewModelModule
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import okhttp3.Interceptor
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin

open class App : Application() {
    private val event: EventBus by inject()
    private val serverSelectionInterceptor: Interceptor by inject("serverSelection")

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
        startKoin(this, listOf(coreModule, apiModule, viewModelModule))

        event.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onServerAddressChangedEvent(event: SystemEvent.ServerAddressChanged) {
        (serverSelectionInterceptor as? ServerSelectionInterceptor)?.serverAddress = event.newServerAddress
    }
}