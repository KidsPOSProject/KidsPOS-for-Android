package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Staff
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class StaffRepository(
    private val apiService: APIService,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getStaffByBarcode(barcode: String): Staff = withContext(dispatcher) {
        apiService.getStaff(barcode)
    }
}
