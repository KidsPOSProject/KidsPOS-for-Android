package info.nukoneko.cuc.android.kidspos.di.hilt

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import info.nukoneko.cuc.android.kidspos.api.ServerSelectionInterceptor
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.openapitools.client.infrastructure.Serializer
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 生成モデルの date-time 項目は @Contextual な java.time.OffsetDateTime になるため、
    // 生成側の SerializersModule を登録しないとシリアライザを解決できず実行時に失敗する
    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            serializersModule = Serializer.kotlinxSerializationAdapters
            ignoreUnknownKeys = true
        }

    @Provides
    @Singleton
    fun provideServerSelectionInterceptor(): ServerSelectionInterceptor =
        ServerSelectionInterceptor(SettingsRepository.DEFAULT_SERVER_ADDRESS)

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
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .baseUrl(SettingsRepository.DEFAULT_SERVER_ADDRESS)
            .build()
}
