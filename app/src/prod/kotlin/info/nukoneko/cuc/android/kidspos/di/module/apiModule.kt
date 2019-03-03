package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.api.APIService
import org.koin.dsl.module.module
import retrofit2.Retrofit

val apiModule = module {
    single<APIService> {
        (get<Retrofit>()).create(APIService::class.java)
    }
}