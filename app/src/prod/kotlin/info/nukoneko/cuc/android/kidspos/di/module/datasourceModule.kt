package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.data.datasource.ItemDatasource
import info.nukoneko.cuc.android.kidspos.data.datasource.SaleDatasource
import info.nukoneko.cuc.android.kidspos.data.datasource.StaffDatasource
import info.nukoneko.cuc.android.kidspos.data.datasource.StoreDatasource
import info.nukoneko.cuc.android.kidspos.data.datasourceimpl.ItemRestDatasource
import info.nukoneko.cuc.android.kidspos.data.datasourceimpl.SaleRestDatasource
import info.nukoneko.cuc.android.kidspos.data.datasourceimpl.StaffRestDatasource
import info.nukoneko.cuc.android.kidspos.data.datasourceimpl.StoreRestDatasource
import org.koin.dsl.module

val datasourceModule = module {
    single<ItemDatasource> {
        ItemRestDatasource(
            api = get()
        )
    }
    single<SaleDatasource> {
        SaleRestDatasource(
            api = get()
        )
    }
    single<StaffDatasource> {
        StaffRestDatasource(
            api = get()
        )
    }
    single<StoreDatasource> {
        StoreRestDatasource(
            api = get()
        )
    }
}
