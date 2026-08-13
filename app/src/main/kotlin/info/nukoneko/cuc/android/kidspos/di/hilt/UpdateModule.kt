package info.nukoneko.cuc.android.kidspos.di.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import info.nukoneko.cuc.android.kidspos.api.ApkDownloader
import info.nukoneko.cuc.android.kidspos.api.OkHttpApkDownloader
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.update.ApkInstaller
import info.nukoneko.cuc.android.kidspos.update.PackageInstallerApkInstaller
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UpdateModule {

    @Provides
    @Singleton
    fun provideApkDownloader(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
        client: OkHttpClient
    ): ApkDownloader = OkHttpApkDownloader(context, settingsRepository, client)

    @Provides
    @Singleton
    fun provideApkInstaller(
        @ApplicationContext context: Context,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): ApkInstaller = PackageInstallerApkInstaller(context, dispatcher)
}
