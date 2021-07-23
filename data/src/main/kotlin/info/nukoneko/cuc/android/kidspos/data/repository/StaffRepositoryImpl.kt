package info.nukoneko.cuc.android.kidspos.data.repository

import info.nukoneko.cuc.android.kidspos.data.datasource.StaffDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Staff
import info.nukoneko.cuc.android.kidspos.domain.repository.StaffRepository

class StaffRepositoryImpl(private val staffDatasource: StaffDatasource) : StaffRepository {
    override suspend fun fetchStaff(barcode: Barcode): Staff {
        return staffDatasource.fetchStaff(barcode)
    }

    override suspend fun fetchStaffList(barcodeList: List<Barcode>): List<Staff> {
        return staffDatasource.fetchStaffList(barcodeList)
    }

}
