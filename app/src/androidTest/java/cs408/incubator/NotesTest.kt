package cs408.incubator

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.longClick
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import firestore_library.updateUserName
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.random.Random.Default.nextInt

class NotesTest {

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
        if(!titleSet) {
            title = "Test-" + nextInt()
            addTestIdea()
            titleSet = true
        }

    }

    fun addTestIdea() {
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        sleep(500)
        Espresso.onView((ViewMatchers.withId(R.id.addTitle))).perform(ViewActions.typeText(title))
        Espresso.onView(ViewMatchers.withId(R.id.confirm))
                .perform(ViewActions.click())
    }

    @Test
    fun addNewNote() {
        val noteTitle = "new note 1"
        val noteDesc = "This is a note"

        sleep(500)
        Espresso.onView(ViewMatchers.withText(title)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.personalNotesButton)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.fabAddNote)).perform(click())
        sleep(500)

        Espresso.onView(withId(R.id.et_add_note_title)).check(matches(withHint("Title")))
                .perform(ViewActions.typeText(noteTitle))

        Espresso.onView(withId(R.id.et_add_note_desc)).check(matches(withHint("Description...")))
                .perform(ViewActions.typeText(noteDesc))

        Espresso.onView(withId(R.id.confirm_add_note)).perform(click())
        sleep(1000)
        Espresso.onView(withText(noteTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun shareNote() {
        val noteTitle = "share note"
        val noteDesc = "Sharing this"

        sleep(500)
        Espresso.onView(ViewMatchers.withText(title)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.personalNotesButton)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.fabAddNote)).perform(click())
        sleep(500)

        Espresso.onView(withId(R.id.et_add_note_title)).check(matches(withHint("Title")))
                .perform(ViewActions.typeText(noteTitle))

        Espresso.onView(withId(R.id.et_add_note_desc)).check(matches(withHint("Description...")))
                .perform(ViewActions.typeText(noteDesc))

        Espresso.onView(withId(R.id.confirm_add_note)).perform(click())
        sleep(1000)
        Espresso.onView(withText(noteTitle)).check(matches(isDisplayed())).perform(longClick())
        Espresso.onView(withText("Share")).perform(click())

        Espresso.onView(withText("$noteTitle has been shared with the collaborators of this idea."))
                .inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView)))
                .check(matches(isDisplayed()))

        Espresso.onView(withContentDescription("Navigate up")).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.sharedNotesButton)).perform(click())
        Espresso.onView(withId(android.R.id.text1)).check(matches(withText(noteTitle)))
        Espresso.onView(withId(android.R.id.text2)).check(matches(withText("abca@abca.com")))


    }

    @Test
    fun delete_note() {
        val noteTitle = "Delete note"
        val noteDesc = "Deleting this"

        sleep(500)
        Espresso.onView(ViewMatchers.withText(title)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.personalNotesButton)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.fabAddNote)).perform(click())
        sleep(500)

        Espresso.onView(withId(R.id.et_add_note_title)).check(matches(withHint("Title")))
                .perform(ViewActions.typeText(noteTitle))

        Espresso.onView(withId(R.id.et_add_note_desc)).check(matches(withHint("Description...")))
                .perform(ViewActions.typeText(noteDesc))

        Espresso.onView(withId(R.id.confirm_add_note)).perform(click())
        sleep(1000)
        Espresso.onView(withText(noteTitle)).check(matches(isDisplayed())).perform(longClick())
        Espresso.onView(withText("Delete")).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.tv_notes_empty)).check(matches(isDisplayed()))
    }
}