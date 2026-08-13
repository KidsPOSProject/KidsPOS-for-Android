package info.nukoneko.cuc.android.kidspos.di.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.AppUpdateService
import info.nukoneko.cuc.android.kidspos.api.DemoAPIService
import info.nukoneko.cuc.android.kidspos.api.DemoAppUpdateService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApiService(): APIService = DemoAPIService()

    @Provides
    @Singleton
    fun provideAppUpdateService(): AppUpdateService = DemoAppUpdateService()
}
