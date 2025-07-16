package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import info.nukoneko.cuc.android.kidspos.R
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ItemListFragmentTest {
    
    @Test
    fun testFragmentDisplaysInitialState() {
        // Launch fragment
        launchFragmentInContainer<ItemListFragment>()
        
        // Verify price view shows initial value
        onView(withId(R.id.price_view))
            .check(matches(isDisplayed()))
            .check(matches(withText("0 リバー")))
        
        // Verify recycler view is displayed
        onView(withId(R.id.recycler_view))
            .check(matches(isDisplayed()))
        
        // Verify no items message is displayed when list is empty
        onView(withId(R.id.no_item_message))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun testStaffVisibilityWhenNotLoggedIn() {
        // Launch fragment
        launchFragmentInContainer<ItemListFragment>()
        
        // Verify staff label is not visible
        onView(withId(R.id.current_staff_label))
            .check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        
        // Verify staff text is not visible
        onView(withId(R.id.current_staff_text))
            .check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }
    
    @Test
    fun testBarcodeInputIsEnabled() {
        // Launch fragment
        launchFragmentInContainer<ItemListFragment>()
        
        // Verify barcode input field is displayed and enabled
        onView(withId(R.id.barcode_input))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .check(matches(withHint("バーコード")))
    }
    
    @Test
    fun testClearButtonFunctionality() {
        // Launch fragment
        launchFragmentInContainer<ItemListFragment>()
        
        // Verify clear button is displayed and enabled
        onView(allOf(withId(R.id.clear_button), withText("とりけし")))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }
    
    @Test
    fun testAccountButtonInitiallyDisabled() {
        // Launch fragment
        launchFragmentInContainer<ItemListFragment>()
        
        // Verify account button is displayed but disabled
        onView(allOf(withId(R.id.account_button), withText("おかいけい")))
            .check(matches(isDisplayed()))
            .check(matches(isNotEnabled()))
    }
}