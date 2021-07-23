package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.data.datasource.*
import info.nukoneko.cuc.android.kidspos.datasource.*
import org.koin.dsl.module

val datasourceModule = module {
    single<ItemDatasource> {
        ItemDemoDatasource()
    }
    single<SaleDatasource> {
        SaleDemoDatasource()
    }
    single<StaffDatasource> {
        StaffDemoDatasource()
    }
    single<StoreDatasource> {
        StoreDemoDatasource()
    }
    single<StatusDatasource> {
        StatusDemoDatasource()
    }
}
