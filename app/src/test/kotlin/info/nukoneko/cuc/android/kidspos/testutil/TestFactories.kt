package info.nukoneko.cuc.android.kidspos.testutil

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.connection.ConnectionMonitor
import info.nukoneko.cuc.android.kidspos.connection.ReachabilityProbe
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
import info.nukoneko.cuc.android.kidspos.update.AppUpdateManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
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
    itemRepository: ItemRepository = createItemRepository(apiService, dispatcher),
    applicationScope: CoroutineScope = CoroutineScope(dispatcher)
): MainViewModel = MainViewModel(
    itemRepository,
    ServerStatusRepository(apiService, dispatcher),
    StoreRepository(apiService, dispatcher),
    SaleRepository(apiService, dispatcher),
    settingsRepository,
    applicationScope,
    barcodeEventBus
)

fun createAppUpdateManager(
    appUpdateService: FakeAppUpdateService = FakeAppUpdateService(),
    apkDownloader: FakeApkDownloader = FakeApkDownloader(),
    apkInstaller: FakeApkInstaller = FakeApkInstaller(),
    apkInstallResultBus: ApkInstallResultBus = ApkInstallResultBus(),
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    applicationScope: CoroutineScope = CoroutineScope(dispatcher)
): AppUpdateManager = AppUpdateManager(
    AppUpdateRepository(appUpdateService, apkDownloader, dispatcher),
    apkInstaller,
    apkInstallResultBus,
    applicationScope
)

fun createConnectionMonitor(
    settingsRepository: SettingsRepository,
    apiService: APIService = FakeAPIService(),
    reachabilityProbe: ReachabilityProbe = FakeReachabilityProbe(),
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    applicationScope: CoroutineScope = CoroutineScope(dispatcher)
): ConnectionMonitor = ConnectionMonitor(
    settingsRepository,
    reachabilityProbe,
    ServerStatusRepository(apiService, dispatcher),
    applicationScope
)

fun createSettingsViewModel(
    settingsRepository: SettingsRepository,
    appUpdateService: FakeAppUpdateService = FakeAppUpdateService(),
    apkDownloader: FakeApkDownloader = FakeApkDownloader(),
    apkInstaller: FakeApkInstaller = FakeApkInstaller(),
    apkInstallResultBus: ApkInstallResultBus = ApkInstallResultBus(),
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    appUpdateManager: AppUpdateManager = createAppUpdateManager(
        appUpdateService,
        apkDownloader,
        apkInstaller,
        apkInstallResultBus,
        dispatcher
    ),
    apiService: FakeAPIService = FakeAPIService(),
    reachabilityProbe: FakeReachabilityProbe = FakeReachabilityProbe(),
    connectionMonitor: ConnectionMonitor = createConnectionMonitor(
        settingsRepository,
        apiService,
        reachabilityProbe,
        dispatcher
    ),
    applicationScope: CoroutineScope = CoroutineScope(dispatcher)
): SettingsViewModel = SettingsViewModel(
    settingsRepository,
    appUpdateManager,
    connectionMonitor,
    applicationScope
)
