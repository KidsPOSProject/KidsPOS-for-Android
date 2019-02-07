package info.nukoneko.cuc.android.kidspos.util

import android.view.KeyEvent
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], manifest = Config.NONE)
class BarcodeReadDelegateTest {

    private lateinit var delegate: BarcodeReadDelegate
    private lateinit var listener: BarcodeReadDelegate.OnBarcodeReadListener

    @Before
    fun onSetup() {
        listener = mock()
        delegate = BarcodeReadDelegate(listener)
    }

    @Test
    fun readBarcodeSuccessWithItem() {
        val inputKeyEvents = intArrayOf(
                KeyEvent.KEYCODE_1,
                KeyEvent.KEYCODE_0,
                KeyEvent.KEYCODE_0,
                KeyEvent.KEYCODE_1,
                KeyEvent.KEYCODE_2,
                KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4,
                KeyEvent.KEYCODE_5,
                KeyEvent.KEYCODE_6,
                KeyEvent.KEYCODE_7,
                KeyEvent.KEYCODE_ENTER)

        val expectValue = "1001234567"
        val expectKind: BarcodeKind = BarcodeKind.ITEM
        inputKeyEvents
                .map { KeyEvent(KeyEvent.ACTION_DOWN, it) }
                .forEach { delegate.onKeyEvent(it) }

        verify(listener, times(1)).onReadSuccess(expectValue, expectKind)
    }
}