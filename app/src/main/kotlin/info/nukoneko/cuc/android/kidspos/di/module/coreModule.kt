package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.di.EventBusImpl
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.di.HostSelectionInterceptor
import info.nukoneko.cuc.android.kidspos.event.EventBus
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val coreModule = module {
    single<EventBus> { EventBusImpl() }
    single("config") { GlobalConfig(androidApplication(), get()) }
    single<Interceptor>("hostSelection") { HostSelectionInterceptor((get<GlobalConfig>("config").baseUrl)) }
    single {
        OkHttpClient.Builder()
                .addInterceptor(get("hostSelection"))
                .build()
    }
    single<Retrofit> {
        Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(get())
                .baseUrl((get<GlobalConfig>("config").baseUrl))
                .build()
    }
}