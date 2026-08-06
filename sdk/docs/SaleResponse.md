
# SaleResponse

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int** | 売上ID |  |
| **storeId** | **kotlin.Int** | 店舗ID |  |
| **storeName** | **kotlin.String** | 店舗名 |  |
| **totalAmount** | **kotlin.Int** | 合計金額（リバー） |  |
| **deposit** | **kotlin.Int** | 預り金（リバー） |  |
| **change** | **kotlin.Int** | お釣り（リバー） |  |
| **saleTime** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | 売上時刻 |  |
| **items** | [**kotlin.collections.List&lt;SaleItemResponse&gt;**](SaleItemResponse.md) | 売上商品リスト |  [optional] |



