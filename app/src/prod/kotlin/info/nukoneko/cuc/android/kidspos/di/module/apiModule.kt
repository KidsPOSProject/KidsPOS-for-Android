package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.generated.*
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    // 生成されたAPIインターフェース
    single<ItemsApi> {
        (get<Retrofit>()).create(ItemsApi::class.java)
    }
    single<SalesApi> {
        (get<Retrofit>()).create(SalesApi::class.java)
    }
    single<StaffApi> {
        (get<Retrofit>()).create(StaffApi::class.java)
    }
    single<StoresApi> {
        (get<Retrofit>()).create(StoresApi::class.java)
    }
    single<SettingsApi> {
        (get<Retrofit>()).create(SettingsApi::class.java)
    }
    single<UsersApi> {
        (get<Retrofit>()).create(UsersApi::class.java)
    }

    // APIServiceの実装
    single {
        APIService(get(), get(), get(), get(), get())
    }
}
