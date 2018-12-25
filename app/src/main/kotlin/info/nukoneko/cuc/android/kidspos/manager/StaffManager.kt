package info.nukoneko.cuc.android.kidspos.manager

import info.nukoneko.cuc.android.kidspos.api.APIAdapter
import info.nukoneko.cuc.android.kidspos.entity.Staff
import io.reactivex.Single

class StaffManager(apiAdapter: APIAdapter) : BaseManager(apiAdapter) {
    fun getStaff(barcode: String): Single<Staff> {
        return apiService.getStaff(barcode)
    }
}
