package info.nukoneko.cuc.android.kidspos.domain.repository

import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Staff

interface StaffRepository {
    suspend fun fetchStaff(barcode: Barcode): Staff

    suspend fun fetchStaffList(barcodeList: List<Barcode>): List<Staff>
}
