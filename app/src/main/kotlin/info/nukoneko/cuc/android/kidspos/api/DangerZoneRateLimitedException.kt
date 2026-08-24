package info.nukoneko.cuc.android.kidspos.api

class DangerZoneRateLimitedException(
    val retryAfterSeconds: Long?
) : Exception("Danger zone verification is rate limited")
