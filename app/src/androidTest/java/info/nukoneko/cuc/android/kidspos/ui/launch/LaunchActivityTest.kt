package info.nukoneko.cuc.android.kidspos.ui.launch

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.ui.main.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LaunchActivityTest {
    
    @Before
    fun setUp() {
        Intents.init()
    }
    
    @After
    fun tearDown() {
        Intents.release()
    }
    
    @Test
    fun testLaunchActivityDisplaysCorrectly() {
        // Launch the activity
        ActivityScenario.launch(LaunchActivity::class.java)
        
        // Verify the launcher image is displayed
        onView(withId(R.id.launcher_image))
            .check(matches(isDisplayed()))
        
        // Verify practice mode button is displayed
        onView(withId(R.id.practice_button))
            .check(matches(isDisplayed()))
            .check(matches(withText("れんしゅう")))
            .check(matches(isEnabled()))
        
        // Verify register mode button is displayed
        onView(withId(R.id.register_button))
            .check(matches(isDisplayed()))
            .check(matches(withText("ほんばん")))
            .check(matches(isEnabled()))
    }
    
    @Test
    fun testPracticeModeButtonLaunchesMainActivity() {
        // Launch the activity
        ActivityScenario.launch(LaunchActivity::class.java)
        
        // Click practice mode button
        onView(withId(R.id.practice_button))
            .perform(click())
        
        // Verify MainActivity intent is launched
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
    
    @Test
    fun testRegisterModeButtonLaunchesMainActivity() {
        // Launch the activity
        ActivityScenario.launch(LaunchActivity::class.java)
        
        // Click register mode button
        onView(withId(R.id.register_button))
            .perform(click())
        
        // Verify MainActivity intent is launched
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
}