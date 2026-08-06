# ItemsApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createItem**](ItemsApi.md#createItem) | **POST** /api/item | 商品登録 |
| [**deleteItem**](ItemsApi.md#deleteItem) | **DELETE** /api/item/{id} | 商品削除 |
| [**generateBarcodePdf**](ItemsApi.md#generateBarcodePdf) | **GET** /api/item/barcode-pdf | バーコードPDF生成 |
| [**getAllItems**](ItemsApi.md#getAllItems) | **GET** /api/item | 商品一覧取得 |
| [**getItemByBarcode**](ItemsApi.md#getItemByBarcode) | **GET** /api/item/barcode/{barcode} | バーコードで商品取得 |
| [**getItemById**](ItemsApi.md#getItemById) | **GET** /api/item/{id} | 商品取得 |
| [**partialUpdateItem**](ItemsApi.md#partialUpdateItem) | **PATCH** /api/item/{id} | 商品部分更新 |
| [**updateItem**](ItemsApi.md#updateItem) | **PUT** /api/item/{id} | 商品更新 |


<a id="createItem"></a>
# **createItem**
> ItemResponse createItem(createItemRequest)

商品登録

新しい商品を登録します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = ItemsApi()
val createItemRequest : CreateItemRequest =  // CreateItemRequest | 
try {
    val result : ItemResponse = apiInstance.createItem(createItemRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ItemsApi#createItem")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ItemsApi#createItem")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **createItemRequest** | [**CreateItemRequest**](CreateItemRequest.md)|  | |

### Return type

[**ItemResponse**](ItemResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="deleteItem"></a>
# **deleteItem**
> deleteItem(id)

商品削除

指定したIDの商品を削除します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = ItemsApi()
val id : kotlin.Int = 56 // kotlin.Int | 
try {
    apiInstance.deleteItem(id)
} catch (e: ClientException) {
    println("4xx response calling ItemsApi#deleteItem")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ItemsApi#deleteItem")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a id="generateBarcodePdf"></a>
# **generateBarcodePdf**
> java.io.File generateBarcodePdf()

バーコードPDF生成

全商品のバーコードをPDF形式で生成します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = ItemsApi()
try {
    val result : java.io.File = apiInstance.generateBarcodePdf()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ItemsApi#generateBarcodePdf")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ItemsApi#generateBarcodePdf")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**java.io.File**](java.io.File.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a id="getAllItems"></a>
# **getAllItems**
> kotlin.collections.List&lt;ItemResponse&gt; getAllItems()

商品一覧取得

登録されている全商品を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = ItemsApi()
try {
    val result : kotlin.collections.List<ItemResponse> = apiInstance.getAllItems()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ItemsApi#getAllItems")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ItemsApi#getAllItems")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;ItemResponse&gt;**](ItemResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getItemByBarcode"></a>
# **getItemByBarcode**
> ItemResponse getItemByBarcode(barcode)

バーコードで商品取得

バーコードから商品を検索します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = ItemsApi()
val barcode : kotlin.String = barcode_example // kotlin.String | 
try {
    val result : ItemResponse = apiInstance.getItemByBarcode(barcode)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ItemsApi#getItemByBarcode")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ItemsApi#getItemByBarcode")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **barcode** | **kotlin.String**|  | |

### Return type

[**ItemResponse**](ItemResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getItemById"></a>
# **getItemById**
> ItemResponse getItemById(id)

商品取得

指定したIDの商品を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = ItemsApi()
val id : kotlin.Int = 56 // kotlin.Int | 
try {
    val result : ItemResponse = apiInstance.getItemById(id)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ItemsApi#getItemById")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ItemsApi#getItemById")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**|  | |

### Return type

[**ItemResponse**](ItemResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="partialUpdateItem"></a>
# **partialUpdateItem**
> ItemResponse partialUpdateItem(id, partialUpdateItemRequest)

商品部分更新

指定したIDの商品を部分的に更新します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = ItemsApi()
val id : kotlin.Int = 56 // kotlin.Int | 
val partialUpdateItemRequest : PartialUpdateItemRequest =  // PartialUpdateItemRequest | 
try {
    val result : ItemResponse = apiInstance.partialUpdateItem(id, partialUpdateItemRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ItemsApi#partialUpdateItem")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ItemsApi#partialUpdateItem")
    e.printStackTrace()
}
```

### Parameters
| **id** | **kotlin.Int**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **partialUpdateItemRequest** | [**PartialUpdateItemRequest**](PartialUpdateItemRequest.md)|  | |

### Return type

[**ItemResponse**](ItemResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="updateItem"></a>
# **updateItem**
> ItemResponse updateItem(id, createItemRequest)

商品更新

指定したIDの商品を更新します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = ItemsApi()
val id : kotlin.Int = 56 // kotlin.Int | 
val createItemRequest : CreateItemRequest =  // CreateItemRequest | 
try {
    val result : ItemResponse = apiInstance.updateItem(id, createItemRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ItemsApi#updateItem")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ItemsApi#updateItem")
    e.printStackTrace()
}
```

### Parameters
| **id** | **kotlin.Int**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **createItemRequest** | [**CreateItemRequest**](CreateItemRequest.md)|  | |

### Return type

[**ItemResponse**](ItemResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

