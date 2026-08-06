# SalesApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createSale**](SalesApi.md#createSale) | **POST** /api/sales | 売上登録 |
| [**getAllSales**](SalesApi.md#getAllSales) | **GET** /api/sales | 売上一覧取得 |
| [**getSaleById**](SalesApi.md#getSaleById) | **GET** /api/sales/{id} | 売上詳細取得 |
| [**validatePrinter**](SalesApi.md#validatePrinter) | **GET** /api/sales/validate-printer/{storeId} | プリンター設定確認 |


<a id="createSale"></a>
# **createSale**
> CreateSale201Response createSale(createSaleRequest)

売上登録

新しい売上を登録します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SalesApi()
val createSaleRequest : CreateSaleRequest =  // CreateSaleRequest | 
try {
    val result : CreateSale201Response = apiInstance.createSale(createSaleRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SalesApi#createSale")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SalesApi#createSale")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **createSaleRequest** | [**CreateSaleRequest**](CreateSaleRequest.md)|  | |

### Return type

[**CreateSale201Response**](CreateSale201Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="getAllSales"></a>
# **getAllSales**
> kotlin.collections.List&lt;SaleResponse&gt; getAllSales()

売上一覧取得

全売上データを取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SalesApi()
try {
    val result : kotlin.collections.List<SaleResponse> = apiInstance.getAllSales()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SalesApi#getAllSales")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SalesApi#getAllSales")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;SaleResponse&gt;**](SaleResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getSaleById"></a>
# **getSaleById**
> SaleResponse getSaleById(id)

売上詳細取得

指定したIDの売上詳細を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SalesApi()
val id : kotlin.Int = 56 // kotlin.Int | 
try {
    val result : SaleResponse = apiInstance.getSaleById(id)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SalesApi#getSaleById")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SalesApi#getSaleById")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**|  | |

### Return type

[**SaleResponse**](SaleResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="validatePrinter"></a>
# **validatePrinter**
> ValidatePrinter200Response validatePrinter(storeId)

プリンター設定確認

指定した店舗のプリンター設定を確認します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SalesApi()
val storeId : kotlin.Int = 56 // kotlin.Int | 
try {
    val result : ValidatePrinter200Response = apiInstance.validatePrinter(storeId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SalesApi#validatePrinter")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SalesApi#validatePrinter")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **storeId** | **kotlin.Int**|  | |

### Return type

[**ValidatePrinter200Response**](ValidatePrinter200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

