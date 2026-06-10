package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.OpenApiAPIService
import info.nukoneko.cuc.android.kidspos.api.generated.ItemsApi
import info.nukoneko.cuc.android.kidspos.api.generated.SalesApi
import info.nukoneko.cuc.android.kidspos.api.generated.SettingsApi
import info.nukoneko.cuc.android.kidspos.api.generated.StaffApi
import info.nukoneko.cuc.android.kidspos.api.generated.StoresApi
import info.nukoneko.cuc.android.kidspos.api.generated.UsersApi
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single<ItemsApi> { get<Retrofit>().create(ItemsApi::class.java) }
    single<SalesApi> { get<Retrofit>().create(SalesApi::class.java) }
    single<StaffApi> { get<Retrofit>().create(StaffApi::class.java) }
    single<StoresApi> { get<Retrofit>().create(StoresApi::class.java) }
    single<SettingsApi> { get<Retrofit>().create(SettingsApi::class.java) }
    single<UsersApi> { get<Retrofit>().create(UsersApi::class.java) }

    single<APIService> {
        OpenApiAPIService(
            itemsApi = get(),
            salesApi = get(),
            staffApi = get(),
            storesApi = get(),
            settingsApi = get()
        )
    }
}
