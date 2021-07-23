package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.data.repository.*
import info.nukoneko.cuc.android.kidspos.domain.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single<ItemRepository> {
        ItemRepositoryImpl(
            itemDatasource = get()
        )
    }
    single<SaleRepository> {
        SaleRepositoryImpl(
            saleDatasource = get()
        )
    }
    single<StaffRepository> {
        StaffRepositoryImpl(
            staffDatasource = get()
        )
    }
    single<StoreRepository> {
        StoreRepositoryImpl(
            storeDatasource = get()
        )
    }
    single<StatusRepository> {
        StatusRepositoryImpl(
            statusDatasource = get()
        )
    }
}
