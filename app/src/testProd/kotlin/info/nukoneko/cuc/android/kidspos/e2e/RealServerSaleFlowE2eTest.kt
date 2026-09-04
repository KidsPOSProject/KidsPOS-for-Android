package info.nukoneko.cuc.android.kidspos.e2e

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.OpenApiAPIService
import info.nukoneko.cuc.android.kidspos.api.generated.ItemsApi
import info.nukoneko.cuc.android.kidspos.api.generated.SalesApi
import info.nukoneko.cuc.android.kidspos.api.generated.StatusApi
import info.nukoneko.cuc.android.kidspos.api.generated.StoresApi
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.testutil.MainDispatcherRule
import info.nukoneko.cuc.android.kidspos.testutil.createItemRepository
import info.nukoneko.cuc.android.kidspos.testutil.createMainViewModel
import info.nukoneko.cuc.android.kidspos.testutil.fakeSettingsRepository
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeEventBus
import info.nukoneko.cuc.android.kidspos.ui.barcode.BarcodeInput
import info.nukoneko.cuc.android.kidspos.ui.main.MainViewModel
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import info.nukoneko.cuc.android.kidspos.util.Mode
import java.io.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.openapitools.client.infrastructure.Serializer
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@OptIn(ExperimentalSerializationApi::class)
class RealServerSaleFlowE2eTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val serverUrl: String? =
        System.getenv("E2E_SERVER_URL")?.let { if (it.endsWith("/")) it else "$it/" }

    private val settingsRepository = fakeSettingsRepository()
    private val barcodeEventBus = BarcodeEventBus()
    private val httpClient = OkHttpClient()
    private lateinit var apiService: APIService

    @Before
    fun setUp() {
        assumeTrue("E2E_SERVER_URL is not set", serverUrl != null)
        val json = Json {
            serializersModule = Serializer.kotlinxSerializationAdapters
            ignoreUnknownKeys = true
        }
        val retrofit = Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(httpClient)
            .baseUrl(serverUrl!!)
            .build()
        apiService = OpenApiAPIService(
            itemsApi = retrofit.create(ItemsApi::class.java),
            salesApi = retrofit.create(SalesApi::class.java),
            statusApi = retrofit.create(StatusApi::class.java),
            storesApi = retrofit.create(StoresApi::class.java)
        )
    }

    @Test
    fun serverStatusReportsSupportedApiVersion() {
        val status = runBlocking { apiService.getServerStatus() }

        assertEquals("OK", status.status)
        assertEquals(APIService.SUPPORTED_API_VERSION, status.apiVersion)
    }

    @Test
    fun barcodeDepositAndAccountFlowPersistsSaleOnServer() {
        val unique = System.currentTimeMillis()
        val storeName = "E2Eストア$unique"
        val barcode = itemBarcode(unique)
        val storeId = seedStore(storeName)
        seedItem(barcode, "E2E商品", 300)

        runBlocking {
            settingsRepository.setRunningMode(Mode.PRODUCTION)
            settingsRepository.setCurrentStore(Store(storeId, storeName))
        }
        val viewModel = createViewModel()

        barcodeEventBus.emit(BarcodeInput(barcode, BarcodeKind.ITEM))
        awaitUntil("item is fetched from server") { viewModel.uiState.value.items.size == 1 }
        assertEquals(300, viewModel.uiState.value.total)
        assertEquals(barcode, viewModel.uiState.value.items.single().barcode)

        viewModel.onAccountClick()
        listOf(5, 0, 0).forEach { viewModel.onCalculatorNumber(it) }
        viewModel.onCalculatorOk()
        val result = viewModel.uiState.value.accountResult
        assertEquals(300, result?.totalPrice)
        assertEquals(500, result?.deposit)
        assertEquals(200, result?.change)

        viewModel.onAccountResultOk()
        awaitUntil("sale is accepted and the cart is reset") {
            viewModel.uiState.value.items.isEmpty() && viewModel.uiState.value.accountResult == null
        }
        assertNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.errorMessageRes)

        val sale = fetchSingleSaleForStore(storeId)
        assertEquals(300, sale.getValue("totalAmount"))
        assertEquals(500, sale.getValue("deposit"))
        assertEquals(200, sale.getValue("change"))
    }

    @Test
    fun changeStoreFetchesSeededStoreFromServer() {
        val unique = System.currentTimeMillis()
        val storeName = "E2E店舗一覧$unique"
        val storeId = seedStore(storeName)

        runBlocking { settingsRepository.setRunningMode(Mode.PRODUCTION) }
        val viewModel = createViewModel()

        viewModel.onChangeStoreClick()
        awaitUntil("stores are fetched from server") {
            viewModel.uiState.value.storeSelection?.loading == false
        }

        val selection = viewModel.uiState.value.storeSelection
        assertEquals(false, selection?.failed)
        assertTrue(selection?.stores.orEmpty().any { it.id == storeId && it.name == storeName })
    }

    @Test
    fun manualItemSelectionAddsSeededItemToCart() {
        val unique = System.currentTimeMillis()
        val barcode = itemBarcode(unique, 111_111)
        val name = "E2E手動商品$unique"
        seedItem(barcode, name, 420)

        runBlocking { settingsRepository.setRunningMode(Mode.PRODUCTION) }
        val viewModel = createViewModel()

        viewModel.onManualItemSelectionClick()
        awaitUntil("items are fetched from server") {
            viewModel.uiState.value.itemSelection?.loading == false
        }

        val selection = viewModel.uiState.value.itemSelection
        assertEquals(false, selection?.failed)
        val seeded = selection?.items.orEmpty().single { it.barcode == barcode }
        assertEquals(name, seeded.name)
        assertEquals(420, seeded.price)

        viewModel.onManualItemSelected(seeded)

        assertEquals(listOf(seeded), viewModel.uiState.value.items)
        assertEquals(420, viewModel.uiState.value.total)
    }

    @Test
    fun manualItemSelectionUsesCacheWhenServerIsUnreachable() {
        val unique = System.currentTimeMillis()
        val barcode = itemBarcode(unique, 222_222)
        seedItem(barcode, "E2Eキャッシュ商品$unique", 130)

        runBlocking { settingsRepository.setRunningMode(Mode.PRODUCTION) }
        val itemRepository = createItemRepository(apiService, mainDispatcherRule.dispatcher)
        val viewModel = createMainViewModel(
            apiService,
            settingsRepository,
            barcodeEventBus,
            mainDispatcherRule.dispatcher,
            itemRepository
        )
        viewModel.onManualItemSelectionClick()
        awaitUntil("items are fetched from server") {
            viewModel.uiState.value.itemSelection?.loading == false
        }
        assertTrue(runBlocking { itemRepository.getCachedItems() }.any { it.barcode == barcode })

        val offlineViewModel = createMainViewModel(
            UnreachableAPIService(apiService),
            settingsRepository,
            barcodeEventBus,
            mainDispatcherRule.dispatcher,
            itemRepository
        )
        offlineViewModel.onManualItemSelectionClick()

        val selection = offlineViewModel.uiState.value.itemSelection
        assertEquals(false, selection?.failed)
        assertTrue(selection?.items.orEmpty().any { it.barcode == barcode })
    }

    private class UnreachableAPIService(private val delegate: APIService) : APIService by delegate {
        override suspend fun fetchItems(): List<Item> = throw IOException("unreachable")
    }

    private fun createViewModel(): MainViewModel = createMainViewModel(
        apiService,
        settingsRepository,
        barcodeEventBus,
        mainDispatcherRule.dispatcher
    )

    // サーバは登録時に id を採番するため、指定せずレスポンスから受け取る
    private fun seedStore(name: String): Int {
        val body = postJson("api/stores", """{"name":"$name","printerUri":""}""")
        return Json.parseToJsonElement(body).jsonObject.getValue("id").jsonPrimitive.int
    }

    // サーバは商品バーコードを ^A(00|01|02)\d{6}A$ で検証するため、種別は BarcodeKind.ITEM に固定し
    // テストごとの衝突は offset で避ける
    private fun itemBarcode(unique: Long, offset: Long = 0): String =
        "A%s%06dA".format(BarcodeKind.ITEM.prefix, (unique + offset) % 1_000_000)

    private fun seedItem(barcode: String, name: String, price: Int) {
        postJson("api/item", """{"barcode":"$barcode","name":"$name","price":$price}""")
    }

    private fun postJson(path: String, body: String): String {
        val request = Request.Builder()
            .url(serverUrl + path)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        httpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            check(response.isSuccessful) {
                "POST $path failed: ${response.code} $responseBody"
            }
            return responseBody
        }
    }

    private fun fetchSingleSaleForStore(storeId: Int): Map<String, Int> {
        val request = Request.Builder().url(serverUrl + "api/sales").build()
        httpClient.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "GET api/sales failed: ${response.code}" }
            val sales = Json.parseToJsonElement(response.body!!.string()).jsonArray
                .map { it.jsonObject }
                .filter { it.getValue("storeId").jsonPrimitive.int == storeId }
            assertEquals(1, sales.size)
            val sale = sales.single()
            return mapOf(
                "totalAmount" to sale.getValue("totalAmount").jsonPrimitive.int,
                "deposit" to sale.getValue("deposit").jsonPrimitive.int,
                "change" to sale.getValue("change").jsonPrimitive.int
            )
        }
    }

    private fun awaitUntil(description: String, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 15_000
        while (System.currentTimeMillis() < deadline) {
            if (condition()) return
            Thread.sleep(50)
        }
        fail("Timed out waiting until $description")
    }
}
