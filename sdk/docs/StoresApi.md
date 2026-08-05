# StoresApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createStore**](StoresApi.md#createStore) | **POST** /api/stores | 店舗登録 |
| [**deleteStore**](StoresApi.md#deleteStore) | **DELETE** /api/stores/{id} | 店舗削除 |
| [**getAllStores**](StoresApi.md#getAllStores) | **GET** /api/stores | 店舗一覧取得 |
| [**getStoreById**](StoresApi.md#getStoreById) | **GET** /api/stores/{id} | 店舗取得 |
| [**updateStore**](StoresApi.md#updateStore) | **PUT** /api/stores/{id} | 店舗更新 |


<a id="createStore"></a>
# **createStore**
> StoreEntity createStore(storeEntity)

店舗登録

新しい店舗を登録します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = StoresApi()
val storeEntity : StoreEntity =  // StoreEntity | 
try {
    val result : StoreEntity = apiInstance.createStore(storeEntity)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StoresApi#createStore")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StoresApi#createStore")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **storeEntity** | [**StoreEntity**](StoreEntity.md)|  | |

### Return type

[**StoreEntity**](StoreEntity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="deleteStore"></a>
# **deleteStore**
> deleteStore(id)

店舗削除

店舗を削除します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = StoresApi()
val id : kotlin.Int = 56 // kotlin.Int | 
try {
    apiInstance.deleteStore(id)
} catch (e: ClientException) {
    println("4xx response calling StoresApi#deleteStore")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StoresApi#deleteStore")
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

<a id="getAllStores"></a>
# **getAllStores**
> kotlin.collections.List&lt;StoreEntity&gt; getAllStores()

店舗一覧取得

全店舗を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = StoresApi()
try {
    val result : kotlin.collections.List<StoreEntity> = apiInstance.getAllStores()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StoresApi#getAllStores")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StoresApi#getAllStores")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;StoreEntity&gt;**](StoreEntity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getStoreById"></a>
# **getStoreById**
> StoreEntity getStoreById(id)

店舗取得

指定したIDの店舗を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = StoresApi()
val id : kotlin.Int = 56 // kotlin.Int | 
try {
    val result : StoreEntity = apiInstance.getStoreById(id)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StoresApi#getStoreById")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StoresApi#getStoreById")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**|  | |

### Return type

[**StoreEntity**](StoreEntity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="updateStore"></a>
# **updateStore**
> StoreEntity updateStore(id, storeEntity)

店舗更新

店舗情報を更新します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = StoresApi()
val id : kotlin.Int = 56 // kotlin.Int | 
val storeEntity : StoreEntity =  // StoreEntity | 
try {
    val result : StoreEntity = apiInstance.updateStore(id, storeEntity)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StoresApi#updateStore")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StoresApi#updateStore")
    e.printStackTrace()
}
```

### Parameters
| **id** | **kotlin.Int**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **storeEntity** | [**StoreEntity**](StoreEntity.md)|  | |

### Return type

[**StoreEntity**](StoreEntity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

