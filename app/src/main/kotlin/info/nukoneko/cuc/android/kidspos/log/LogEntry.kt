package info.nukoneko.cuc.android.kidspos.log

import kotlinx.serialization.Serializable

@Serializable
data class LogEntry(
    val timestamp: Long,
    val priority: Int,
    val tag: String?,
    val message: String
)
