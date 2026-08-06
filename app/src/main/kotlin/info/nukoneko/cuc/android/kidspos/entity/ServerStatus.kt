package info.nukoneko.cuc.android.kidspos.entity

import kotlinx.serialization.Serializable

@Serializable
data class ServerStatus(
    val status: String,
    val version: String,
    val apiVersion: Int
)
