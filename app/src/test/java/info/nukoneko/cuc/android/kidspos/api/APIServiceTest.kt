package info.nukoneko.cuc.android.kidspos.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class APIServiceTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: APIService
    private val json = Json { ignoreUnknownKeys = true }
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        
        apiService = retrofit.create(APIService::class.java)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `getItemByBarcode should return item on success`() = runTest {
        // Given
        val barcode = "1234567890"
        val expectedItem = Item(barcode, "テスト商品", 500)
        val jsonResponse = json.encodeToString(expectedItem)
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))
        
        // When
        val result = apiService.getItemByBarcode(barcode)
        
        // Then
        assertEquals(expectedItem.barcode, result.barcode)
        assertEquals(expectedItem.name, result.name)
        assertEquals(expectedItem.price, result.price)
        
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/items/$barcode", request.path)
    }
    
    @Test
    fun `getItemByBarcode should throw HttpException on 404`() = runTest {
        // Given
        val barcode = "9999999999"
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        
        // When/Then
        val exception = assertFailsWith<HttpException> {
            apiService.getItemByBarcode(barcode)
        }
        assertEquals(404, exception.code())
    }
    
    @Test
    fun `getStaffByBarcode should return staff on success`() = runTest {
        // Given
        val barcode = "100"
        val expectedStaff = Staff(1, barcode, "テストスタッフ")
        val jsonResponse = json.encodeToString(expectedStaff)
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))
        
        // When
        val result = apiService.getStaffByBarcode(barcode)
        
        // Then
        assertEquals(expectedStaff.id, result.id)
        assertEquals(expectedStaff.barcode, result.barcode)
        assertEquals(expectedStaff.name, result.name)
        
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/staffs/$barcode", request.path)
    }
    
    @Test
    fun `getStores should return list of stores`() = runTest {
        // Given
        val stores = listOf(
            Store(1, "店舗1"),
            Store(2, "店舗2"),
            Store(3, "店舗3")
        )
        val jsonResponse = json.encodeToString(stores)
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))
        
        // When
        val result = apiService.getStores()
        
        // Then
        assertEquals(3, result.size)
        assertEquals("店舗1", result[0].name)
        assertEquals("店舗2", result[1].name)
        assertEquals("店舗3", result[2].name)
        
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/stores", request.path)
    }
    
    @Test
    fun `postSale should send sale data correctly`() = runTest {
        // Given
        val items = listOf(
            Item("123", "商品1", 100),
            Item("456", "商品2", 200)
        )
        val sale = Sale("20240101120000", 1, 1, items, 300)
        val expectedResponse = mapOf("status" to "success")
        val jsonResponse = json.encodeToString(expectedResponse)
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(201))
        
        // When
        val result = apiService.postSale(sale)
        
        // Then
        assertNotNull(result)
        
        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/sales", request.path)
        
        val requestBody = request.body.readUtf8()
        assertTrue(requestBody.contains("20240101120000"))
        assertTrue(requestBody.contains("商品1"))
        assertTrue(requestBody.contains("商品2"))
    }
    
    @Test
    fun `network timeout should throw exception`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBodyDelay(2, TimeUnit.SECONDS)
        )
        
        // When/Then
        assertFailsWith<Exception> {
            apiService.getStores()
        }
    }
}