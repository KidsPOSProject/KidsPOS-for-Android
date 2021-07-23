package info.nukoneko.cuc.android.kidspos.data.datasource

import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Staff

interface StaffDatasource {
    suspend fun fetchStaff(barcode: Barcode): Staff

    suspend fun fetchStaffList(barcodeList: List<Barcode>): List<Staff>
}
