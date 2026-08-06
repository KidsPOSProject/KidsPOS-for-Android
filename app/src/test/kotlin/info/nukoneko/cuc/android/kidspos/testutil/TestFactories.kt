package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.data.repository.ItemRepository
import info.nukoneko.cuc.android.kidspos.data.repository.SaleRepository
import info.nukoneko.cuc.android.kidspos.data.repository.ServerStatusRepository
import info.nukoneko.cuc.android.kidspos.data.repository.StoreRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.main.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json

fun fakeSettingsRepository(): SettingsRepository =
    SettingsRepository(FakePreferencesDataStore(), Json)

fun createMainViewModel(
    apiService: APIService,
    settingsRepository: SettingsRepository,
    barcodeEventBus: BarcodeEventBus,
    dispatcher: CoroutineDispatcher
): MainViewModel = MainViewModel(
    ItemRepository(apiService, dispatcher),
    ServerStatusRepository(apiService, dispatcher),
    StoreRepository(apiService, dispatcher),
    SaleRepository(apiService, dispatcher),
    settingsRepository,
    barcodeEventBus
)
