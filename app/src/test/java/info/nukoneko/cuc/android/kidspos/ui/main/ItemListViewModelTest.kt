package info.nukoneko.cuc.android.kidspos.ui.main

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.testutil.CoroutineTestRule
import info.nukoneko.cuc.android.kidspos.testutil.FakeEventBus
import info.nukoneko.cuc.android.kidspos.testutil.FakeGlobalConfig
import info.nukoneko.cuc.android.kidspos.testutil.getOrAwaitValue
import info.nukoneko.cuc.android.kidspos.ui.main.itemlist.ItemListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ItemListViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    
    private lateinit var viewModel: ItemListViewModel
    private lateinit var fakeEventBus: FakeEventBus
    private lateinit var fakeGlobalConfig: FakeGlobalConfig
    private lateinit var mockListener: ItemListViewModel.Listener
    
    @Before
    fun setup() {
        fakeEventBus = FakeEventBus()
        fakeGlobalConfig = FakeGlobalConfig()
        mockListener = mock()
        
        viewModel = ItemListViewModel(fakeGlobalConfig, fakeEventBus)
        viewModel.listener = mockListener
    }
    
    @Test
    fun `initial state should show zero price and disabled account button`() {
        // Then
        assertEquals("0 リバー", viewModel.getCurrentPriceText().getOrAwaitValue())
        assertFalse(viewModel.getAccountButtonEnabled().getOrAwaitValue())
    }
    
    @Test
    fun `onClickClear should clear items and notify listener`() {
        // Given
        val item = Item("123", "テスト商品", 100)
        viewModel.onReadItemSuccessEvent(BarcodeEvent.ReadItemSuccess(item))
        
        // When
        viewModel.onClickClear(null)
        
        // Then
        verify(mockListener).onDataClear()
        assertEquals("0 リバー", viewModel.getCurrentPriceText().getOrAwaitValue())
        assertFalse(viewModel.getAccountButtonEnabled().getOrAwaitValue())
    }
    
    @Test
    fun `onClickAccount should start account with current items`() {
        // Given
        val item1 = Item("123", "商品1", 100)
        val item2 = Item("456", "商品2", 200)
        viewModel.onReadItemSuccessEvent(BarcodeEvent.ReadItemSuccess(item1))
        viewModel.onReadItemSuccessEvent(BarcodeEvent.ReadItemSuccess(item2))
        
        // When
        viewModel.onClickAccount(null)
        
        // Then
        argumentCaptor<List<Item>>().apply {
            verify(mockListener).onStartAccount(capture())
            assertEquals(2, firstValue.size)
            assertEquals(item1, firstValue[0])
            assertEquals(item2, firstValue[1])
        }
    }
    
    @Test
    fun `staff visibility should be VISIBLE when staff is set`() {
        // Given
        val staff = Staff(1, "100", "テストスタッフ")
        fakeGlobalConfig.setTestStaff(staff)
        
        // When
        viewModel.onResume()
        
        // Then
        assertEquals(View.VISIBLE, viewModel.getCurrentStaffVisibility().getOrAwaitValue())
        assertEquals("たんとう: テストスタッフ", viewModel.getCurrentStaffText().getOrAwaitValue())
    }
    
    @Test
    fun `staff visibility should be INVISIBLE when staff is not set`() {
        // Given
        fakeGlobalConfig.setTestStaff(null)
        
        // When
        viewModel.onResume()
        
        // Then
        assertEquals(View.INVISIBLE, viewModel.getCurrentStaffVisibility().getOrAwaitValue())
    }
    
    @Test
    fun `onReadItemSuccessEvent should add item and update views`() {
        // Given
        val item = Item("789", "テスト商品", 500)
        val event = BarcodeEvent.ReadItemSuccess(item)
        
        // When
        viewModel.onReadItemSuccessEvent(event)
        
        // Then
        verify(mockListener).onDataAdded(item)
        assertEquals("500 リバー", viewModel.getCurrentPriceText().getOrAwaitValue())
        assertTrue(viewModel.getAccountButtonEnabled().getOrAwaitValue())
    }
    
    @Test
    fun `multiple items should calculate total correctly`() {
        // Given
        val item1 = Item("123", "商品1", 100)
        val item2 = Item("456", "商品2", 200)
        val item3 = Item("789", "商品3", 300)
        
        // When
        viewModel.onReadItemSuccessEvent(BarcodeEvent.ReadItemSuccess(item1))
        viewModel.onReadItemSuccessEvent(BarcodeEvent.ReadItemSuccess(item2))
        viewModel.onReadItemSuccessEvent(BarcodeEvent.ReadItemSuccess(item3))
        
        // Then
        assertEquals("600 リバー", viewModel.getCurrentPriceText().getOrAwaitValue())
        assertTrue(viewModel.getAccountButtonEnabled().getOrAwaitValue())
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
        assertFalse(fakeEventBus.isRegistered(viewModel))
    }
}