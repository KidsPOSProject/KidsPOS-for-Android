package info.nukoneko.cuc.android.kidspos.entity

import kotlinx.serialization.Serializable

@Serializable
data class Store(val id: Int, val name: String, val printerUri: String? = null)
