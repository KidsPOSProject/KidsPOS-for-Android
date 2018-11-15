package info.nukoneko.cuc.android.kidspos.entity

import java.util.Date

data class Sale(val id: Int, val barcode: String,
           val createdAt: Date,
           val points: Int, val price: Int,
           val items: String, val storeId: Int, val staffId: Int)
