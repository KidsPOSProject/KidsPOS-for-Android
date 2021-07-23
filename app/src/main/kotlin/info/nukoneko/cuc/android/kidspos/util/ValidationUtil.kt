package info.nukoneko.cuc.android.kidspos.util

object ValidationUtil {
    fun isValidIPv4Address(ipAddress: String): Boolean {
        return Regex("((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])([.](?!$)|$)){4}")
            .matches(ipAddress)
    }

    fun isUsablePort(port: Int): Boolean {
        return port in 1025..17999
    }

    fun isValidKidsPOSBarcode(barcode: String): Boolean {
        return Regex("10[0-9]{8}").matches(barcode)
    }
}
