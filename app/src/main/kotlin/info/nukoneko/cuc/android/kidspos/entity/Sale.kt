package info.nukoneko.cuc.android.kidspos.entity

import kotlinx.serialization.Serializable

@Serializable
data class Sale(
    val id: Int, val barcode: String,
    val createdAt: String,
    val points: Int, val price: Int,
    val items: String, val storeId: Int, val staffId: Int
)
