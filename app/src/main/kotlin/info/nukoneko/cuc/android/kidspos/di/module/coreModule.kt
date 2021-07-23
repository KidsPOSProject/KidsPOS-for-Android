package info.nukoneko.cuc.android.kidspos.di.module

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import info.nukoneko.cuc.android.kidspos.di.EventBusImpl
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.di.ServerSelectionInterceptor
import info.nukoneko.cuc.android.kidspos.event.EventBus
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val coreModule = module {
    single<EventBus> { EventBusImpl() }
    single { GlobalConfig(androidApplication(), get()) }
    single<Interceptor>(named("serverSelection")) {
        ServerSelectionInterceptor((get<GlobalConfig>().currentServerAddress))
    }
    single {
        OkHttpClient.Builder().addInterceptor(get<Interceptor>(named("serverSelection"))).build()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(get())
            .baseUrl((get<GlobalConfig>().currentServerAddress))
            .build()
    }
}
