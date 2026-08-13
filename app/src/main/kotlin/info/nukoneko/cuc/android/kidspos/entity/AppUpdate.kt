package info.nukoneko.cuc.android.kidspos.entity

data class AppUpdate(
    val versionName: String,
    val versionCode: Int,
    val fileSize: Long,
    val releaseNotes: String?,
    val downloadPath: String
)
