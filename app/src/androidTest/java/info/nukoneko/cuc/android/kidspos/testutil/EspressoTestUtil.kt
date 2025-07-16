package info.nukoneko.cuc.android.kidspos.testutil

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Matcher

/**
 * Utility functions for Espresso UI tests
 */
object EspressoTestUtil {
    
    /**
     * Wait for a specified amount of time
     */
    fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            
            override fun getDescription(): String = "Wait for $millis milliseconds"
            
            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }
    
    /**
     * Click on a view without checking if it's displayed
     */
    fun forceClick(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            
            override fun getDescription(): String = "Force click"
            
            override fun perform(uiController: UiController, view: View) {
                view.performClick()
            }
        }
    }
    
    /**
     * Check if a view exists in the hierarchy (may not be visible)
     */
    fun exists(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            
            override fun getDescription(): String = "Check if view exists"
            
            override fun perform(uiController: UiController, view: View) {
                // No-op, just checking existence
            }
        }
    }
}