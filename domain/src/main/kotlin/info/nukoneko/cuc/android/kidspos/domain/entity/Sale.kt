package info.nukoneko.cuc.android.kidspos.domain.entity

import java.util.*

data class Sale(
    val id: SaleId, val barcode: Barcode,
    val createdAt: Date,
    val points: Int, val price: Price,
    val items: String, val storeId: StoreId, val staffId: StaffId
)
