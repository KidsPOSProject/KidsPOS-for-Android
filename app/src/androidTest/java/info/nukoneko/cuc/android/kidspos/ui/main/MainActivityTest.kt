package info.nukoneko.cuc.android.kidspos.ui.main

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import info.nukoneko.cuc.android.kidspos.R
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    
    @Test
    fun testMainActivityLaunches() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java)
        
        // Verify toolbar is displayed
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
        
        // Verify navigation drawer button is displayed
        onView(withContentDescription(R.string.navigation_drawer_open))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun testNavigationDrawerOpensAndCloses() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java)
        
        // Open navigation drawer
        onView(withContentDescription(R.string.navigation_drawer_open))
            .perform(click())
        
        // Verify drawer header is displayed
        onView(withId(R.id.drawer_header))
            .check(matches(isDisplayed()))
        
        // Close drawer by clicking outside
        onView(withId(R.id.drawer_layout))
            .perform(click())
    }
    
    @Test
    fun testToolbarMenuItemsAreAccessible() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java)
        
        // Verify settings action is available
        onView(withId(R.id.action_setting))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
        
        // Verify store selection action is available
        onView(withId(R.id.action_store))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }
    
    @Test
    fun testFragmentContainerIsDisplayed() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java)
        
        // Verify fragment container is present
        onView(withId(R.id.content))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun testBottomButtonsAreDisplayed() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java)
        
        // Verify clear button is displayed
        onView(allOf(withId(R.id.clear_button), withText("とりけし")))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        
        // Verify account button is displayed but initially disabled
        onView(allOf(withId(R.id.account_button), withText("おかいけい")))
            .check(matches(isDisplayed()))
            .check(matches(isNotEnabled()))
    }
}