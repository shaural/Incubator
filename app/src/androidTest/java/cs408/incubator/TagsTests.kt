package cs408.incubator

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.*
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

class TagsTests {
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
            title = "TagTest-" + Random.nextInt()
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
    fun addTag() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.tagButton)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.et_tag)).check(matches(withHint("Enter new Tag here...")))
                .perform(typeText("New Tag"))
        Espresso.onView(withId(R.id.btn_add_tag)).perform(click())
        Espresso.onView(withText("New Tag")).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.et_tag)).check(matches(withHint("Enter new Tag here...")))
                .perform(typeText("Another"))
        Espresso.onView(withId(R.id.btn_add_tag)).perform(click())
        Espresso.onView(withText("Another")).check(matches(isDisplayed()))

        Espresso.onView(withContentDescription("Navigate up")).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.tagText)).check(matches(withText("New Tag\nAnother")))
    }

    @Test
    fun editTag() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.tagButton)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.et_tag)).check(matches(withHint("Enter new Tag here...")))
                .perform(typeText("EditTag"))
        Espresso.onView(withId(R.id.btn_add_tag)).perform(click())

        /** Cancel Edit */
        Espresso.onView(withText("EditTag")).check(matches(isDisplayed())).perform(click())
        Espresso.onView(withId(android.R.id.button2)).perform(click())
        Espresso.onView(withText("EditTag")).check(matches(isDisplayed()))

        /** Do Edit */
        Espresso.onView(withText("EditTag")).check(matches(isDisplayed())).perform(click())
        mdevice.findObject(By.text("EditTag")).text = "Tag is Edited"
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        Espresso.onView(withText("Tag is Edited")).check(matches(isDisplayed()))

    }

    @Test
    fun addEmptyTag() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.tagButton)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.et_tag)).check(matches(withHint("Enter new Tag here...")))
                .perform(typeText(""))
        Espresso.onView(withId(R.id.btn_add_tag)).perform(click())
        Espresso.onView(withText("Tag cannot be empty.")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(matches(isDisplayed()))

    }

    @Test
    fun editToEmptyTag() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.tagButton)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.et_tag)).check(matches(withHint("Enter new Tag here...")))
                .perform(typeText("EditToBlank"))
        Espresso.onView(withId(R.id.btn_add_tag)).perform(click())
        Espresso.onView(withText("EditToBlank")).check(matches(isDisplayed())).perform(click())

        mdevice.findObject(By.text("EditToBlank")).text = ""
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        Espresso.onView(withText("Tag cannot be empty.")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(matches(isDisplayed()))

    }

    @Test
    fun deleteTag() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.tagButton)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.et_tag)).check(matches(withHint("Enter new Tag here...")))
                .perform(typeText("EditTag"))
        Espresso.onView(withId(R.id.btn_add_tag)).perform(click())
        Espresso.onView(withText("EditTag")).check(matches(isDisplayed())).perform(click())

        mdevice.findObject(By.text("EditTag")).text = "DeleteThis"
        Espresso.onView(withId(android.R.id.button1)).perform(click())

        /** Cancel delete */
        Espresso.onView(withText("DeleteThis")).check(matches(isDisplayed())).perform(longClick())
        Espresso.onView(withId(android.R.id.button2)).perform(click())
        Espresso.onView(withText("DeleteThis")).check(matches(isDisplayed()))

        /** Delete */
        Espresso.onView(withText("DeleteThis")).check(matches(isDisplayed())).perform(longClick())
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        Espresso.onView(withText("DeleteThis")).check(doesNotExist())
    }

}