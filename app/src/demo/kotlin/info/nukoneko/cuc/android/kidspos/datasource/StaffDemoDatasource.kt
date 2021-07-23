package info.nukoneko.cuc.android.kidspos.datasource

import info.nukoneko.cuc.android.kidspos.data.datasource.StaffDatasource
import info.nukoneko.cuc.android.kidspos.domain.entity.Barcode
import info.nukoneko.cuc.android.kidspos.domain.entity.Staff
import info.nukoneko.cuc.android.kidspos.domain.entity.StaffId

class StaffDemoDatasource : StaffDatasource {
    private fun createDummyStaff(id: Int, barcode: Barcode) = Staff(
        id = StaffId(id),
        barcode = barcode,
        name = "Staff$id"
    )

    override suspend fun fetchStaff(barcode: Barcode) = createDummyStaff(1, barcode)

    override suspend fun fetchStaffList(barcodeList: List<Barcode>) =
        barcodeList.mapIndexed { index, barcode ->
            createDummyStaff(index, barcode)
        }
}
