package info.nukoneko.cuc.android.kidspos.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.SystemEvent
import info.nukoneko.cuc.android.kidspos.testutil.CoroutineTestRule
import info.nukoneko.cuc.android.kidspos.testutil.FakeEventBus
import info.nukoneko.cuc.android.kidspos.testutil.FakeGlobalConfig
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    
    private lateinit var viewModel: MainViewModel
    private lateinit var fakeEventBus: FakeEventBus
    private lateinit var fakeGlobalConfig: FakeGlobalConfig
    private lateinit var mockListener: MainViewModel.Listener
    
    @Before
    fun setup() {
        fakeEventBus = FakeEventBus()
        fakeGlobalConfig = FakeGlobalConfig()
        mockListener = mock()
        
        viewModel = MainViewModel(fakeGlobalConfig, fakeEventBus)
        viewModel.listener = mockListener
    }
    
    @Test
    fun `onStart should register to event bus`() {
        // When
        viewModel.onStart()
        
        // Then
        assertTrue(fakeEventBus.isRegistered(viewModel))
    }
    
    @Test
    fun `onStop should unregister from event bus`() {
        // Given
        viewModel.onStart()
        
        // When
        viewModel.onStop()
        
        // Then
        assertTrue(!fakeEventBus.isRegistered(viewModel))
    }
    
    @Test
    fun `onResume should update title with store name when store is set`() {
        // Given
        val store = Store(1, "テスト店舗")
        fakeGlobalConfig.setTestStore(store)
        
        // When
        viewModel.onResume()
        
        // Then
        verify(mockListener).onShouldChangeTitleSuffix("(テスト店舗)")
    }
    
    @Test
    fun `onBarcodeInput with ITEM barcode should post ReadItemRequest`() = runTest {
        // Given
        val barcode = "1234567890"
        
        // When
        viewModel.onBarcodeInput(barcode, BarcodeKind.ITEM)
        
        // Then
        val postedEvent = fakeEventBus.getLastPostedEvent()
        assertNotNull(postedEvent)
        assertTrue(postedEvent is BarcodeEvent.ReadItemRequest)
        assertEquals(barcode, (postedEvent as BarcodeEvent.ReadItemRequest).barcode)
    }
    
    @Test
    fun `onBarcodeInput with STAFF barcode should post ReadStaffRequest`() = runTest {
        // Given
        val barcode = "9876543210"
        
        // When
        viewModel.onBarcodeInput(barcode, BarcodeKind.STAFF)
        
        // Then
        val postedEvent = fakeEventBus.getLastPostedEvent()
        assertNotNull(postedEvent)
        assertTrue(postedEvent is BarcodeEvent.ReadStaffRequest)
        assertEquals(barcode, (postedEvent as BarcodeEvent.ReadStaffRequest).barcode)
    }
    
    @Test
    fun `onReadItemFailureEvent should show message when no server configured`() = runTest {
        // Given
        fakeGlobalConfig.setTestServerAddress("")
        val event = BarcodeEvent.ReadItemFailure("1234567890", BarcodeEvent.ReadFailureType.NO_CONFIGURATION)
        
        // When
        viewModel.onReadItemFailureEvent(event)
        
        // Then
        verify(mockListener).onNotReachableServer()
    }
    
    @Test
    fun `onReadItemFailureEvent should show error message for network error`() = runTest {
        // Given
        val event = BarcodeEvent.ReadItemFailure("1234567890", BarcodeEvent.ReadFailureType.NETWORK_ERROR)
        
        // When
        viewModel.onReadItemFailureEvent(event)
        
        // Then
        verify(mockListener).onShouldShowMessage("通信に失敗しました。")
    }
    
    @Test
    fun `onReadStaffSuccessEvent should update global config and show message`() = runTest {
        // Given
        val staff = Staff(1, "100", "テストスタッフ")
        val event = BarcodeEvent.ReadStaffSuccess(staff)
        
        // When
        viewModel.onReadStaffSuccessEvent(event)
        
        // Then
        assertEquals(staff, fakeGlobalConfig.currentStaff)
        verify(mockListener).onShouldShowMessage("スタッフ ${staff.name} でログインしました")
    }
    
    @Test
    fun `onSystemStoreChangeEvent should update title`() = runTest {
        // Given
        val store = Store(2, "新店舗")
        fakeGlobalConfig.setTestStore(store)
        val event = SystemEvent.StoreChange(store)
        
        // When
        viewModel.onSystemStoreChangeEvent(event)
        
        // Then
        verify(mockListener).onShouldChangeTitleSuffix("(新店舗)")
    }
}