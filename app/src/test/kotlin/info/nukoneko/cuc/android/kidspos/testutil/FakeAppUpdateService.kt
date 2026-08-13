package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.api.AppUpdateService
import info.nukoneko.cuc.android.kidspos.entity.AppUpdate

class FakeAppUpdateService : AppUpdateService {
    val checkedVersionCodes = mutableListOf<Int>()

    var checkForUpdateHandler: suspend (Int) -> AppUpdate? = { null }

    override suspend fun checkForUpdate(currentVersionCode: Int): AppUpdate? {
        checkedVersionCodes += currentVersionCode
        return checkForUpdateHandler(currentVersionCode)
    }
}
