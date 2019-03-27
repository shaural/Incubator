package cs408.incubator

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import firestore_library.updateUserName
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.random.Random

class CollaboratorTests {
    @get:Rule
    public val rule = ActivityTestRule<MainIdeasActivity>(MainIdeasActivity::class.java)

    private var titleSet = false
    lateinit var mainIdeasActivity: MainIdeasActivity
    lateinit var mdevice: UiDevice
    lateinit var title: String

    @Before
    fun setUp() {
        updateUserName("abca@abca.com")
        mainIdeasActivity = rule.activity
        mdevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        if (!titleSet) {
            title = "CollabTest-" + Random.nextInt()
            addTestIdea()
            titleSet = true
        }

    }

    fun addTestIdea() {
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView((ViewMatchers.withId(R.id.addTitle))).perform(ViewActions.typeText(title))
        mdevice.findObject(By.res("cs408.incubator","confirm")).click()

    }

    @Test
    fun addCollaborator() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(200)
        Espresso.onView(withText("abca@abca.com")).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.collabButton)).perform(click())
        mdevice.findObject(By.res("cs408.incubator","collabEmail")).text = "test@test.com"
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        sleep(200)
        Espresso.onView(withText("abca@abca.com")).check(matches(isDisplayed()))
        Espresso.onView(withText("test@test.com")).check(matches(isDisplayed()))

    }

    @Test
    fun addBlankEmail() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(200)
        Espresso.onView(withText("abca@abca.com")).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.collabButton)).perform(click())
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        Espresso.onView(withText("Collaborator email cannot be Empty!")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun addInvalidEmail() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(200)
        Espresso.onView(withText("abca@abca.com")).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.collabButton)).perform(click())
        mdevice.findObject(By.res("cs408.incubator","collabEmail")).text = "test"
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        Espresso.onView(withText("Invalid Collaborator Email!")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun addNonExistingUser() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(200)
        Espresso.onView(withText("abca@abca.com")).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.collabButton)).perform(click())
        mdevice.findObject(By.res("cs408.incubator","collabEmail")).text = "xzyvzayv@abcd.com"
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        Espresso.onView(withText("User Not Found")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun removeCollaborator() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(200)
        Espresso.onView(withId(R.id.collabButton)).perform(click())
        mdevice.findObject(By.res("cs408.incubator", "collabEmail")).text = "test@test.com"
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        sleep(1000)
        Espresso.onView(withText("abca@abca.com")).check(matches(isDisplayed()))
        Espresso.onView(withText("test@test.com")).check(matches(isDisplayed())).perform(click())
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        sleep(1000)
        Espresso.onView(withText("test@test.com")).check(doesNotExist())
    }

    @Test
    fun removeSelf() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(200)
        Espresso.onView(withId(R.id.collabButton)).perform(click())
        mdevice.findObject(By.res("cs408.incubator", "collabEmail")).text = "test@test.com"
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        sleep(1000)
        Espresso.onView(withText("abca@abca.com")).check(matches(isDisplayed())).perform(click())
        Espresso.onView(withText("Delete Idea to remove yourself as collaborator")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(matches(isDisplayed()))

    }

}