package info.nukoneko.cuc.android.kidspos.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilTest {
    @Test
    fun validIpV4Address() {
        assertTrue(ValidationUtil.isValidIPv4Address("0.0.0.0"))
        assertTrue(ValidationUtil.isValidIPv4Address("127.0.0.1"))
        assertTrue(ValidationUtil.isValidIPv4Address("192.168.1.1"))
    }

    @Test
    fun invalidIpV4Address() {
        assertFalse(ValidationUtil.isValidIPv4Address(""))
        assertFalse(ValidationUtil.isValidIPv4Address("abc"))
        assertFalse(ValidationUtil.isValidIPv4Address("0.0.0"))
        assertFalse(ValidationUtil.isValidIPv4Address("0.0.0."))
        assertFalse(ValidationUtil.isValidIPv4Address("256.0.0.0"))
        assertFalse(ValidationUtil.isValidIPv4Address("-1.-1.-1.-1"))
        assertFalse(ValidationUtil.isValidIPv4Address("1.2.3.4."))
        assertFalse(ValidationUtil.isValidIPv4Address("1.2.3.4.5"))
    }

    @Test
    fun usablePort() {
        assertTrue(ValidationUtil.isUsablePort(1025))
        assertTrue(ValidationUtil.isUsablePort(10000))
        assertTrue(ValidationUtil.isUsablePort(17999))
    }

    @Test
    fun nonUsablePort() {
        assertFalse(ValidationUtil.isUsablePort(-1))
        assertFalse(ValidationUtil.isUsablePort(0))
        assertFalse(ValidationUtil.isUsablePort(1024))
        assertFalse(ValidationUtil.isUsablePort(18000))
    }

    @Test
    fun validKidsPOSBarcode() {
        assertTrue(ValidationUtil.isValidKidsPOSBarcode("1001000000"))
        assertTrue(ValidationUtil.isValidKidsPOSBarcode("1023456789"))
        assertTrue(ValidationUtil.isValidKidsPOSBarcode("1076543210"))
    }

    @Test
    fun invalidKidsPOSBarcode() {
        assertFalse(ValidationUtil.isValidKidsPOSBarcode(""))
        assertFalse(ValidationUtil.isValidKidsPOSBarcode("0"))
        assertFalse(ValidationUtil.isValidKidsPOSBarcode("103456789"))
        assertFalse(ValidationUtil.isValidKidsPOSBarcode("10765432101"))
        assertFalse(ValidationUtil.isValidKidsPOSBarcode("10345678901"))
    }
}