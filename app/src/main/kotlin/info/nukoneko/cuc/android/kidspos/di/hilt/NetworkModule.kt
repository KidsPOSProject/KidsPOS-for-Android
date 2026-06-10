package info.nukoneko.cuc.android.kidspos.di.hilt

import android.content.Context
import androidx.preference.PreferenceManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.di.ServerSelectionInterceptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideServerSelectionInterceptor(@ApplicationContext context: Context): ServerSelectionInterceptor {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val serverAddress = prefs.getString(GlobalConfig.KEY_SERVER_INFO, GlobalConfig.DEFAULT_SERVER_INFO)
            ?: GlobalConfig.DEFAULT_SERVER_INFO
        return ServerSelectionInterceptor(serverAddress)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: ServerSelectionInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
        client: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val baseUrl = prefs.getString(GlobalConfig.KEY_SERVER_INFO, GlobalConfig.DEFAULT_SERVER_INFO)
            ?: GlobalConfig.DEFAULT_SERVER_INFO
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .baseUrl(baseUrl)
            .build()
    }
}
