package info.nukoneko.cuc.android.kidspos.di.module

import info.nukoneko.cuc.android.kidspos.ui.main.MainViewModel
import info.nukoneko.cuc.android.kidspos.ui.main.calculate.AccountResultDialogViewModel
import info.nukoneko.cuc.android.kidspos.ui.main.calculate.CalculatorDialogViewModel
import info.nukoneko.cuc.android.kidspos.ui.main.itemlist.ItemListViewModel
import info.nukoneko.cuc.android.kidspos.ui.main.storelist.StoreListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { ItemListViewModel(get(), get()) }
    viewModel { StoreListViewModel(get(), get()) }
    viewModel { CalculatorDialogViewModel(get(), get(), get()) }
    viewModel { AccountResultDialogViewModel() }
}