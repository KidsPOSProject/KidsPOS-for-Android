package info.nukoneko.cuc.android.kidspos.ui.main.calculate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Sale
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.testutil.CoroutineTestRule
import info.nukoneko.cuc.android.kidspos.testutil.FakeEventBus
import info.nukoneko.cuc.android.kidspos.testutil.FakeGlobalConfig
import info.nukoneko.cuc.android.kidspos.testutil.getOrAwaitValue
import info.nukoneko.cuc.android.kidspos.util.Mode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CalculatorDialogViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    
    @Mock
    private lateinit var mockApi: APIService
    
    private lateinit var viewModel: CalculatorDialogViewModel
    private lateinit var fakeEventBus: FakeEventBus
    private lateinit var fakeGlobalConfig: FakeGlobalConfig
    private lateinit var mockListener: CalculatorDialogViewModel.Listener
    
    @Before
    fun setup() {
        fakeEventBus = FakeEventBus()
        fakeGlobalConfig = FakeGlobalConfig()
        mockListener = mock()
        
        viewModel = CalculatorDialogViewModel(mockApi, fakeGlobalConfig, fakeEventBus)
        viewModel.listener = mockListener
    }
    
    @Test
    fun `initial state should have zero total price and disabled account button`() {
        // Then
        assertEquals("0", viewModel.getTotalPriceText().getOrAwaitValue())
        assertEquals("0", viewModel.getDepositText().getOrAwaitValue())
        assertFalse(viewModel.getAccountButtonEnabled().getOrAwaitValue())
    }
    
    @Test
    fun `setup should update total price and items`() {
        // Given
        val items = listOf(
            Item("123", "商品1", 100),
            Item("456", "商品2", 200)
        )
        val totalPrice = 300
        
        // When
        viewModel.setup(items, totalPrice)
        
        // Then
        assertEquals("300", viewModel.getTotalPriceText().getOrAwaitValue())
    }
    
    @Test
    fun `onNumberClick should update deposit and enable account button when sufficient`() {
        // Given
        viewModel.setup(listOf(Item("123", "商品", 500)), 500)
        
        // When
        viewModel.onNumberClick(5)
        
        // Then
        assertEquals("5", viewModel.getDepositText().getOrAwaitValue())
        assertFalse(viewModel.getAccountButtonEnabled().getOrAwaitValue())
        
        // When
        viewModel.onNumberClick(0)
        viewModel.onNumberClick(0)
        
        // Then
        assertEquals("500", viewModel.getDepositText().getOrAwaitValue())
        assertTrue(viewModel.getAccountButtonEnabled().getOrAwaitValue())
    }
    
    @Test
    fun `onNumberClick should build multi-digit number correctly`() {
        // When
        viewModel.onNumberClick(1)
        viewModel.onNumberClick(2)
        viewModel.onNumberClick(3)
        
        // Then
        assertEquals("123", viewModel.getDepositText().getOrAwaitValue())
    }
    
    @Test
    fun `onNumberClick should not exceed 100000`() {
        // Given
        viewModel.onNumberClick(9)
        viewModel.onNumberClick(9)
        viewModel.onNumberClick(9)
        viewModel.onNumberClick(9)
        viewModel.onNumberClick(9)
        viewModel.onNumberClick(9) // 999999
        
        // When
        viewModel.onNumberClick(9) // Should not add this
        
        // Then
        assertEquals("999999", viewModel.getDepositText().getOrAwaitValue())
    }
    
    @Test
    fun `onClearClick should remove last digit`() {
        // Given
        viewModel.onNumberClick(1)
        viewModel.onNumberClick(2)
        viewModel.onNumberClick(3)
        
        // When
        viewModel.onClearClick()
        
        // Then
        assertEquals("12", viewModel.getDepositText().getOrAwaitValue())
        
        // When
        viewModel.onClearClick()
        
        // Then
        assertEquals("1", viewModel.getDepositText().getOrAwaitValue())
        
        // When
        viewModel.onClearClick()
        
        // Then
        assertEquals("0", viewModel.getDepositText().getOrAwaitValue())
    }
    
    @Test
    fun `onDoneClick should show result dialog`() {
        // Given
        viewModel.setup(listOf(Item("123", "商品", 500)), 500)
        viewModel.onNumberClick(5)
        viewModel.onNumberClick(0)
        viewModel.onNumberClick(0)
        
        // When
        viewModel.onDoneClick(null)
        
        // Then
        verify(mockListener).onShouldShowResultDialog(500, 500)
    }
    
    @Test
    fun `onCancelClick should dismiss dialog`() {
        // When
        viewModel.onCancelClick(null)
        
        // Then
        verify(mockListener).onDismiss()
    }
    
    @Test
    fun `onOk in practice mode should show message and dismiss without API call`() = runTest {
        // Given
        fakeGlobalConfig.setTestRunningMode(Mode.PRACTICE)
        viewModel.setup(listOf(Item("123", "商品", 500)), 500)
        
        // When
        viewModel.onOk()
        
        // Then
        verify(mockListener).onShouldShowErrorMessage("練習モードのためレシートは出ません")
        verify(mockListener).onDismiss()
        verifyNoInteractions(mockApi)
        
        val event = fakeEventBus.getLastPostedEvent() as? SystemEvent.SentSaleSuccess
        assertEquals(null, event?.sale)
    }
    
    @Test
    fun `onOk in normal mode should create sale via API`() = runTest {
        // Given
        fakeGlobalConfig.setTestRunningMode(Mode.REGISTER)
        fakeGlobalConfig.setTestStore(Store(1, "テスト店舗"))
        fakeGlobalConfig.setTestStaff(Staff(1, "100", "テストスタッフ"))
        
        val items = listOf(
            Item("123", "商品1", 100),
            Item("456", "商品2", 200)
        )
        viewModel.setup(items, 300)
        viewModel.onNumberClick(5)
        viewModel.onNumberClick(0)
        viewModel.onNumberClick(0)
        
        val expectedSale = Sale("20240101120000", 1, 1, items, 300)
        whenever(mockApi.createSale(1, "100", 500, "123,456")).thenReturn(expectedSale)
        
        // When
        viewModel.onOk()
        
        // Then
        verify(mockApi).createSale(1, "100", 500, "123,456")
        verify(mockListener).onDismiss()
        
        val event = fakeEventBus.getLastPostedEvent() as? SystemEvent.SentSaleSuccess
        assertEquals(expectedSale, event?.sale)
    }
    
    @Test
    fun `onOk should show error message when API fails`() = runTest {
        // Given
        fakeGlobalConfig.setTestRunningMode(Mode.REGISTER)
        fakeGlobalConfig.setTestStore(Store(1, "テスト店舗"))
        fakeGlobalConfig.setTestStaff(Staff(1, "100", "テストスタッフ"))
        
        viewModel.setup(listOf(Item("123", "商品", 500)), 500)
        viewModel.onNumberClick(5)
        viewModel.onNumberClick(0)
        viewModel.onNumberClick(0)
        
        val exception = RuntimeException("Network error")
        whenever(mockApi.createSale(any(), any(), any(), any())).thenThrow(exception)
        
        // When
        viewModel.onOk()
        
        // Then
        verify(mockListener).onShouldShowErrorMessage("Network error")
        verify(mockListener, never()).onDismiss()
    }
    
    @Test
    fun `onOk with missing store or staff should use defaults`() = runTest {
        // Given
        fakeGlobalConfig.setTestRunningMode(Mode.REGISTER)
        fakeGlobalConfig.setTestStore(null)
        fakeGlobalConfig.setTestStaff(null)
        
        viewModel.setup(listOf(Item("123", "商品", 500)), 500)
        viewModel.onNumberClick(5)
        viewModel.onNumberClick(0)
        viewModel.onNumberClick(0)
        
        val expectedSale = Sale("20240101120000", 0, 0, listOf(Item("123", "商品", 500)), 500)
        whenever(mockApi.createSale(0, "", 500, "123")).thenReturn(expectedSale)
        
        // When
        viewModel.onOk()
        
        // Then
        verify(mockApi).createSale(0, "", 500, "123")
    }
    
    @Test
    fun `account button should be disabled when deposit is less than total`() {
        // Given
        viewModel.setup(listOf(Item("123", "商品", 1000)), 1000)
        
        // When
        viewModel.onNumberClick(9)
        viewModel.onNumberClick(9)
        viewModel.onNumberClick(9)
        
        // Then
        assertEquals("999", viewModel.getDepositText().getOrAwaitValue())
        assertFalse(viewModel.getAccountButtonEnabled().getOrAwaitValue())
    }
    
    @Test
    fun `account button should be enabled when deposit equals total`() {
        // Given
        viewModel.setup(listOf(Item("123", "商品", 1000)), 1000)
        
        // When
        viewModel.onNumberClick(1)
        viewModel.onNumberClick(0)
        viewModel.onNumberClick(0)
        viewModel.onNumberClick(0)
        
        // Then
        assertEquals("1000", viewModel.getDepositText().getOrAwaitValue())
        assertTrue(viewModel.getAccountButtonEnabled().getOrAwaitValue())
    }
}