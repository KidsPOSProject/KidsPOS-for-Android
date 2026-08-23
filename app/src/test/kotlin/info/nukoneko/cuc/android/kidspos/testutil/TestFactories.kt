package info.nukoneko.cuc.android.kidspos.testutil

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.data.repository.AppUpdateRepository
import info.nukoneko.cuc.android.kidspos.data.repository.ItemRepository
import info.nukoneko.cuc.android.kidspos.data.repository.SaleRepository
import info.nukoneko.cuc.android.kidspos.data.repository.ServerStatusRepository
import info.nukoneko.cuc.android.kidspos.data.repository.StoreRepository
import info.nukoneko.cuc.android.kidspos.data.settings.SettingsRepository
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.main.MainViewModel
import info.nukoneko.cuc.android.kidspos.ui.settings.SettingsViewModel
import info.nukoneko.cuc.android.kidspos.update.ApkInstallResultBus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

fun fakeSettingsRepository(): SettingsRepository =
    SettingsRepository(FakePreferencesDataStore(), Json)

fun createItemRepository(
    apiService: APIService,
    dispatcher: CoroutineDispatcher,
    dataStore: DataStore<Preferences> = FakePreferencesDataStore()
): ItemRepository = ItemRepository(apiService, dataStore, Json, dispatcher)

fun createMainViewModel(
    apiService: APIService,
    settingsRepository: SettingsRepository,
    barcodeEventBus: BarcodeEventBus,
    dispatcher: CoroutineDispatcher,
    itemRepository: ItemRepository = createItemRepository(apiService, dispatcher)
): MainViewModel = MainViewModel(
    itemRepository,
    ServerStatusRepository(apiService, dispatcher),
    StoreRepository(apiService, dispatcher),
    SaleRepository(apiService, dispatcher),
    settingsRepository,
    barcodeEventBus
)

fun createSettingsViewModel(
    settingsRepository: SettingsRepository,
    appUpdateService: FakeAppUpdateService = FakeAppUpdateService(),
    apkDownloader: FakeApkDownloader = FakeApkDownloader(),
    apkInstaller: FakeApkInstaller = FakeApkInstaller(),
    apkInstallResultBus: ApkInstallResultBus = ApkInstallResultBus(),
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
): SettingsViewModel = SettingsViewModel(
    settingsRepository,
    AppUpdateRepository(appUpdateService, apkDownloader, dispatcher),
    apkInstaller,
    apkInstallResultBus
)
