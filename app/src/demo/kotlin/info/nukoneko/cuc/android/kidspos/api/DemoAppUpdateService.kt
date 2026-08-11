package info.nukoneko.cuc.android.kidspos.api

import info.nukoneko.cuc.android.kidspos.entity.AppUpdate

class DemoAppUpdateService : AppUpdateService {
    override suspend fun checkForUpdate(currentVersionCode: Int): AppUpdate? = null
}
