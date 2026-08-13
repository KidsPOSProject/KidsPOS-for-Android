package info.nukoneko.cuc.android.kidspos.di.hilt

import info.nukoneko.cuc.android.kidspos.api.generated.model.SaleResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class NetworkModuleTest {

    private val json: Json = NetworkModule.provideJson()

    @Test
    fun `decodes offset date time property of generated model`() {
        val decoded = json.decodeFromString<SaleResponse>(
            """
            {
              "id": 1,
              "storeId": 2,
              "storeName": "テスト店",
              "totalAmount": 300,
              "deposit": 500,
              "change": 200,
              "saleTime": "2026-08-13T15:04:05+09:00"
            }
            """.trimIndent()
        )

        assertEquals(
            OffsetDateTime.of(2026, 8, 13, 15, 4, 5, 0, ZoneOffset.ofHours(9)),
            decoded.saleTime
        )
    }

    @Test
    fun `encodes offset date time property of generated model`() {
        val original = SaleResponse(
            id = 1,
            storeId = 2,
            storeName = "テスト店",
            totalAmount = 300,
            deposit = 500,
            change = 200,
            saleTime = OffsetDateTime.of(2026, 8, 13, 15, 4, 5, 0, ZoneOffset.ofHours(9))
        )

        val encoded = json.encodeToString(SaleResponse.serializer(), original)
        val restored = json.decodeFromString(SaleResponse.serializer(), encoded)

        assertEquals(original.saleTime, restored.saleTime)
    }

    @Test
    fun `ignores unknown keys sent by the server`() {
        val decoded = json.decodeFromString<SaleResponse>(
            """
            {
              "id": 1,
              "storeId": 2,
              "storeName": "テスト店",
              "totalAmount": 300,
              "deposit": 500,
              "change": 200,
              "saleTime": "2026-08-13T15:04:05+09:00",
              "unknownField": "ignored"
            }
            """.trimIndent()
        )

        assertEquals(1, decoded.id)
    }
}
