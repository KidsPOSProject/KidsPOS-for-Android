package info.nukoneko.cuc.android.kidspos.di.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.OpenApiAPIService
import info.nukoneko.cuc.android.kidspos.api.generated.ItemsApi
import info.nukoneko.cuc.android.kidspos.api.generated.SalesApi
import info.nukoneko.cuc.android.kidspos.api.generated.StaffApi
import info.nukoneko.cuc.android.kidspos.api.generated.StoresApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideItemsApi(retrofit: Retrofit): ItemsApi =
        retrofit.create(ItemsApi::class.java)

    @Provides
    @Singleton
    fun provideSalesApi(retrofit: Retrofit): SalesApi =
        retrofit.create(SalesApi::class.java)

    @Provides
    @Singleton
    fun provideStaffApi(retrofit: Retrofit): StaffApi =
        retrofit.create(StaffApi::class.java)

    @Provides
    @Singleton
    fun provideStoresApi(retrofit: Retrofit): StoresApi =
        retrofit.create(StoresApi::class.java)

    @Provides
    @Singleton
    fun provideApiService(
        itemsApi: ItemsApi,
        salesApi: SalesApi,
        staffApi: StaffApi,
        storesApi: StoresApi
    ): APIService = OpenApiAPIService(
        itemsApi = itemsApi,
        salesApi = salesApi,
        staffApi = staffApi,
        storesApi = storesApi
    )
}
