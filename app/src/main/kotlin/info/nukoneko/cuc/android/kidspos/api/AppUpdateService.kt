package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.AppUpdate

interface AppUpdateService {
    suspend fun checkForUpdate(currentVersionCode: Int): AppUpdate?
}
