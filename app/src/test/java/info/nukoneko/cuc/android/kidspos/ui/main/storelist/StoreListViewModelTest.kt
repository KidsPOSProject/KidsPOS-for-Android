package info.nukoneko.cuc.android.kidspos.ui.main.storelist

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.testutil.CoroutineTestRule
import info.nukoneko.cuc.android.kidspos.testutil.FakeGlobalConfig
import info.nukoneko.cuc.android.kidspos.testutil.getOrAwaitValue
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
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoreListViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    
    @Mock
    private lateinit var mockApi: APIService
    
    private lateinit var viewModel: StoreListViewModel
    private lateinit var fakeGlobalConfig: FakeGlobalConfig
    private lateinit var mockListener: StoreListViewModel.Listener
    
    @Before
    fun setup() {
        fakeGlobalConfig = FakeGlobalConfig()
        mockListener = mock()
        
        viewModel = StoreListViewModel(mockApi, fakeGlobalConfig)
        viewModel.listener = mockListener
    }
    
    @Test
    fun `initial state should hide all views`() {
        // Then
        assertEquals(View.GONE, viewModel.getProgressVisibility().getOrAwaitValue())
        assertEquals(View.GONE, viewModel.getRecyclerViewVisibility().getOrAwaitValue())
        assertEquals(View.GONE, viewModel.getErrorButtonVisibility().getOrAwaitValue())
    }
    
    @Test
    fun `onResume should fetch stores and show progress`() = runTest {
        // Given
        val stores = listOf(
            Store(1, "店舗1"),
            Store(2, "店舗2")
        )
        whenever(mockApi.fetchStores()).thenReturn(stores)
        
        // When
        viewModel.onResume()
        
        // Then
        verify(mockApi).fetchStores()
        assertEquals(stores, viewModel.getData().getOrAwaitValue())
        assertEquals(View.GONE, viewModel.getProgressVisibility().getOrAwaitValue())
        assertEquals(View.VISIBLE, viewModel.getRecyclerViewVisibility().getOrAwaitValue())
        assertEquals(View.GONE, viewModel.getErrorButtonVisibility().getOrAwaitValue())
    }
    
    @Test
    fun `fetchStores with empty result should show error button`() = runTest {
        // Given
        whenever(mockApi.fetchStores()).thenReturn(emptyList())
        
        // When
        viewModel.onResume()
        
        // Then
        assertEquals(emptyList<Store>(), viewModel.getData().getOrAwaitValue())
        assertEquals(View.GONE, viewModel.getProgressVisibility().getOrAwaitValue())
        assertEquals(View.GONE, viewModel.getRecyclerViewVisibility().getOrAwaitValue())
        assertEquals(View.VISIBLE, viewModel.getErrorButtonVisibility().getOrAwaitValue())
    }
    
    @Test
    fun `fetchStores failure should show error dialog and button`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        whenever(mockApi.fetchStores()).thenThrow(exception)
        
        // When
        viewModel.onResume()
        
        // Then
        verify(mockListener).onShouldShowErrorDialog("Network error")
        assertEquals(View.GONE, viewModel.getProgressVisibility().getOrAwaitValue())
        assertEquals(View.GONE, viewModel.getRecyclerViewVisibility().getOrAwaitValue())
        assertEquals(View.VISIBLE, viewModel.getErrorButtonVisibility().getOrAwaitValue())
    }
    
    @Test
    fun `onSelect should update global config and dismiss`() {
        // Given
        val store = Store(3, "選択店舗")
        assertNull(fakeGlobalConfig.currentStore)
        
        // When
        viewModel.onSelect(store)
        
        // Then
        assertEquals(store, fakeGlobalConfig.currentStore)
        verify(mockListener).onDismiss()
    }
    
    @Test
    fun `onReload should refetch stores`() = runTest {
        // Given
        val stores = listOf(Store(1, "店舗1"))
        whenever(mockApi.fetchStores()).thenReturn(stores)
        
        // When
        viewModel.onReload(null)
        
        // Then
        verify(mockApi).fetchStores()
        assertEquals(stores, viewModel.getData().getOrAwaitValue())
    }
    
    @Test
    fun `onClose should dismiss dialog`() {
        // When
        viewModel.onClose(null)
        
        // Then
        verify(mockListener).onDismiss()
    }
    
    @Test
    fun `multiple concurrent fetch requests should only execute one`() = runTest {
        // Given
        val stores = listOf(Store(1, "店舗1"))
        whenever(mockApi.fetchStores()).thenReturn(stores)
        
        // When - Start multiple fetches rapidly
        viewModel.onResume()
        viewModel.onReload(null)
        viewModel.onReload(null)
        
        // Then - Only one API call should be made
        verify(mockApi, times(1)).fetchStores()
    }
    
    @Test
    fun `requesting state should show only progress`() = runTest {
        // Given
        val stores = listOf(Store(1, "店舗1"))
        whenever(mockApi.fetchStores()).thenAnswer {
            // Check visibility states during request
            assertEquals(View.VISIBLE, viewModel.getProgressVisibility().value)
            assertEquals(View.GONE, viewModel.getRecyclerViewVisibility().value)
            assertEquals(View.GONE, viewModel.getErrorButtonVisibility().value)
            stores
        }
        
        // When
        viewModel.onResume()
        
        // Then
        verify(mockApi).fetchStores()
    }
}