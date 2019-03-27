package cs408.incubator

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.widget.AutoCompleteTextView
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import firestore_library.updateUserName
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class SearchTests {
    @get:Rule
    public val rule = ActivityTestRule<MainIdeasActivity>(MainIdeasActivity::class.java)


    lateinit var mainIdeasActivity: MainIdeasActivity
    lateinit var mdevice: UiDevice

    @Before
    fun setUp() {
        updateUserName("abca@abca.com")
        mainIdeasActivity = rule.activity
        mdevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    }

    fun addIdeas() {

        val tags = "This is a test"

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView((ViewMatchers.withId(R.id.addTitle))).perform(ViewActions.typeText("Tag_1"))
        Espresso.onView(withId(R.id.addTag)).perform(typeText(tags))
        mdevice.findObject(By.res("cs408.incubator","confirm")).click()
        sleep(1000)

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView((ViewMatchers.withId(R.id.addTitle))).perform(ViewActions.typeText("Tag_2"))
        mdevice.findObject(By.res("cs408.incubator","confirm")).click()
        sleep(1000)

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView((ViewMatchers.withId(R.id.addTitle))).perform(ViewActions.typeText("TagAnotherIdea"))
        Espresso.onView(withId(R.id.addTag)).perform(typeText(tags))
        mdevice.findObject(By.res("cs408.incubator","confirm")).click()
        sleep(1000)

    }

    fun addIdeasforName() {
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView((ViewMatchers.withId(R.id.addTitle))).perform(ViewActions.typeText("Search_Name_1"))
        mdevice.findObject(By.res("cs408.incubator","confirm")).click()
        sleep(1000)

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView((ViewMatchers.withId(R.id.addTitle))).perform(ViewActions.typeText("Search_Name_2"))
        mdevice.findObject(By.res("cs408.incubator","confirm")).click()
        sleep(1000)

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView((ViewMatchers.withId(R.id.addTitle))).perform(ViewActions.typeText("AnotherIdea"))
        mdevice.findObject(By.res("cs408.incubator","confirm")).click()
        sleep(1000)
    }


    @Test
    fun searchByName(){
        addIdeasforName()
        Thread.sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","search_idea")).click()
        Espresso.onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText("Search\n"))
        sleep(500)
        Espresso.onView(withText("Search_Name_1")).check(matches(isDisplayed()))
        Espresso.onView(withText("Search_Name_2")).check(matches(isDisplayed()))
        Espresso.onView(withText("AnotherIdea")).check(doesNotExist())
    }

    @Test
    fun searchByTag() {
        addIdeas()
        Thread.sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","search_idea")).click()
        Espresso.onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText("This is a test\n"))
        sleep(1000)
        Espresso.onView(withText("Tag_1")).check(matches(isDisplayed()))
        Espresso.onView(withText("Tag_2")).check(doesNotExist())
        Espresso.onView(withText("TagAnotherIdea")).check(matches(isDisplayed()))
    }

    @Test
    fun nothingFound() {
        mdevice.findObject(By.res("cs408.incubator","search_idea")).click()
        Espresso.onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText("zxxz\n"))
        Espresso.onView(withText("No ideas found!")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(matches(isDisplayed()))

    }
}