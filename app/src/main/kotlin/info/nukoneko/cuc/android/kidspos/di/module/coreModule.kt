package info.nukoneko.cuc.android.kidspos.di.module

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import info.nukoneko.cuc.android.kidspos.di.EventBusImpl
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.di.HostSelectionInterceptor
import info.nukoneko.cuc.android.kidspos.event.EventBus
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import retrofit2.Retrofit
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
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(get())
                .baseUrl((get<GlobalConfig>("config").baseUrl))
                .build()
    }
}