# SettingsApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createSetting**](SettingsApi.md#createSetting) | **POST** /api/setting | 設定作成 |
| [**deleteSetting**](SettingsApi.md#deleteSetting) | **DELETE** /api/setting/{key} | 設定削除 |
| [**getAllSettings**](SettingsApi.md#getAllSettings) | **GET** /api/setting | 設定一覧取得 |
| [**getApplicationSettings**](SettingsApi.md#getApplicationSettings) | **GET** /api/setting/application | アプリケーション設定取得 |
| [**getPrinterSettings**](SettingsApi.md#getPrinterSettings) | **GET** /api/setting/printer/{storeId} | プリンター設定取得 |
| [**getSettingByKey**](SettingsApi.md#getSettingByKey) | **GET** /api/setting/{key} | 設定取得 |
| [**getStatus**](SettingsApi.md#getStatus) | **GET** /api/setting/status | ステータス取得 |
| [**saveApplicationSettings**](SettingsApi.md#saveApplicationSettings) | **POST** /api/setting/application | アプリケーション設定保存 |
| [**savePrinterSettings**](SettingsApi.md#savePrinterSettings) | **POST** /api/setting/printer/{storeId} | プリンター設定保存 |
| [**updateSetting**](SettingsApi.md#updateSetting) | **PUT** /api/setting/{key} | 設定更新 |


<a id="createSetting"></a>
# **createSetting**
> SettingEntity createSetting(settingEntity)

設定作成

新しい設定を作成します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
val settingEntity : SettingEntity =  // SettingEntity | 
try {
    val result : SettingEntity = apiInstance.createSetting(settingEntity)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#createSetting")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#createSetting")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **settingEntity** | [**SettingEntity**](SettingEntity.md)|  | |

### Return type

[**SettingEntity**](SettingEntity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="deleteSetting"></a>
# **deleteSetting**
> deleteSetting(key)

設定削除

設定を削除します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
val key : kotlin.String = key_example // kotlin.String | 
try {
    apiInstance.deleteSetting(key)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#deleteSetting")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#deleteSetting")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **key** | **kotlin.String**|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a id="getAllSettings"></a>
# **getAllSettings**
> kotlin.collections.List&lt;SettingEntity&gt; getAllSettings()

設定一覧取得

全設定を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
try {
    val result : kotlin.collections.List<SettingEntity> = apiInstance.getAllSettings()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#getAllSettings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#getAllSettings")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;SettingEntity&gt;**](SettingEntity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getApplicationSettings"></a>
# **getApplicationSettings**
> ApplicationSetting getApplicationSettings()

アプリケーション設定取得

アプリケーション全体の設定を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
try {
    val result : ApplicationSetting = apiInstance.getApplicationSettings()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#getApplicationSettings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#getApplicationSettings")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ApplicationSetting**](ApplicationSetting.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getPrinterSettings"></a>
# **getPrinterSettings**
> GetPrinterSettings200Response getPrinterSettings(storeId)

プリンター設定取得

店舗のプリンター設定を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
val storeId : kotlin.Int = 56 // kotlin.Int | 
try {
    val result : GetPrinterSettings200Response = apiInstance.getPrinterSettings(storeId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#getPrinterSettings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#getPrinterSettings")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **storeId** | **kotlin.Int**|  | |

### Return type

[**GetPrinterSettings200Response**](GetPrinterSettings200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getSettingByKey"></a>
# **getSettingByKey**
> SettingEntity getSettingByKey(key)

設定取得

指定したキーの設定を取得します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
val key : kotlin.String = key_example // kotlin.String | 
try {
    val result : SettingEntity = apiInstance.getSettingByKey(key)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#getSettingByKey")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#getSettingByKey")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **key** | **kotlin.String**|  | |

### Return type

[**SettingEntity**](SettingEntity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="getStatus"></a>
# **getStatus**
> StatusResponse getStatus()

ステータス取得

APIのステータスを取得します。後方互換のため維持しています。/api/status を使用してください

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
try {
    val result : StatusResponse = apiInstance.getStatus()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#getStatus")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#getStatus")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**StatusResponse**](StatusResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="saveApplicationSettings"></a>
# **saveApplicationSettings**
> SaveApplicationSettings200Response saveApplicationSettings(applicationSetting)

アプリケーション設定保存

アプリケーション全体の設定を保存します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
val applicationSetting : ApplicationSetting =  // ApplicationSetting | 
try {
    val result : SaveApplicationSettings200Response = apiInstance.saveApplicationSettings(applicationSetting)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#saveApplicationSettings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#saveApplicationSettings")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **applicationSetting** | [**ApplicationSetting**](ApplicationSetting.md)|  | |

### Return type

[**SaveApplicationSettings200Response**](SaveApplicationSettings200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="savePrinterSettings"></a>
# **savePrinterSettings**
> SavePrinterSettings200Response savePrinterSettings(storeId, savePrinterSettingsRequest)

プリンター設定保存

店舗のプリンター設定を保存します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
val storeId : kotlin.Int = 56 // kotlin.Int | 
val savePrinterSettingsRequest : SavePrinterSettingsRequest =  // SavePrinterSettingsRequest | 
try {
    val result : SavePrinterSettings200Response = apiInstance.savePrinterSettings(storeId, savePrinterSettingsRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#savePrinterSettings")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#savePrinterSettings")
    e.printStackTrace()
}
```

### Parameters
| **storeId** | **kotlin.Int**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **savePrinterSettingsRequest** | [**SavePrinterSettingsRequest**](SavePrinterSettingsRequest.md)|  | |

### Return type

[**SavePrinterSettings200Response**](SavePrinterSettings200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="updateSetting"></a>
# **updateSetting**
> SettingEntity updateSetting(key, `value`)

設定更新

設定を更新します

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = SettingsApi()
val key : kotlin.String = key_example // kotlin.String | 
val `value` : kotlin.String = `value`_example // kotlin.String | 
try {
    val result : SettingEntity = apiInstance.updateSetting(key, `value`)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SettingsApi#updateSetting")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SettingsApi#updateSetting")
    e.printStackTrace()
}
```

### Parameters
| **key** | **kotlin.String**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **&#x60;value&#x60;** | **kotlin.String**|  | |

### Return type

[**SettingEntity**](SettingEntity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/x-www-form-urlencoded
 - **Accept**: application/json

