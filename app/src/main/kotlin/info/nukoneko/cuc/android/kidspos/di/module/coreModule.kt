package info.nukoneko.cuc.android.kidspos.di.module

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import info.nukoneko.cuc.android.kidspos.di.EventBusImpl
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.di.ServerSelectionInterceptor
import info.nukoneko.cuc.android.kidspos.event.EventBus
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

@ExperimentalSerializationApi
val coreModule = module {
    single<EventBus> { EventBusImpl() }
    single { GlobalConfig(androidApplication(), get()) }
    single<Interceptor>(named("serverSelection")) {
        ServerSelectionInterceptor((get<GlobalConfig>().currentServerAddress))
    }
    single {
        OkHttpClient.Builder().addInterceptor(get<Interceptor>(named("serverSelection"))).build()
    }
    single {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(get())
            .baseUrl((get<GlobalConfig>().currentServerAddress))
            .build()
    }
}
