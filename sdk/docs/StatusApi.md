# StatusApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getServerStatus**](StatusApi.md#getServerStatus) | **GET** /api/status | サーバーステータス取得 |


<a id="getServerStatus"></a>
# **getServerStatus**
> StatusResponse getServerStatus()

サーバーステータス取得

サーバーの稼働状態、アプリケーションバージョン、API互換バージョンを取得します。クライアントは apiVersion を自身の対応バージョンと比較し、不一致の場合はアップデートを促してください

### Example
```kotlin
// Import classes:
//import info.nukoneko.kidspos.sdk.infrastructure.*
//import info.nukoneko.kidspos.sdk.models.*

val apiInstance = StatusApi()
try {
    val result : StatusResponse = apiInstance.getServerStatus()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StatusApi#getServerStatus")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StatusApi#getServerStatus")
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

