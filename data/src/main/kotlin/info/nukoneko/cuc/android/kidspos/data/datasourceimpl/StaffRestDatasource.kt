package info.nukoneko.cuc.android.kidspos.data.datasourceimpl

import info.nukoneko.cuc.android.kidspos.data.api.APIService
import info.nukoneko.cuc.android.kidspos.data.datasource.StaffDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Staff

class StaffRestDatasource(private val api: APIService) : StaffDatasource {
    override suspend fun fetchStaff(barcode: Barcode): Staff {
        return api.getStaff(staffBarcode = barcode.value)
    }

    override suspend fun fetchStaffList(barcodeList: List<Barcode>): List<Staff> {
        TODO("Not yet implemented")
    }
}
