package cs408.incubator

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import firestore_library.USERNAME
import firestore_library.updateUserName
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class IdeaDetailsTests {
    @get:Rule
    public val rule = ActivityTestRule<MainIdeasActivity>(MainIdeasActivity::class.java)


    lateinit var mainIdeasActivity: MainIdeasActivity

    @Before
    fun setUp() {
        mainIdeasActivity = rule.activity

    }

    @Test
    fun check_edit_details() {
        val title = "Detail Test"
        val desc = "Checking for detailsNew description"
        val tag = "Test"

        val newDesc = "New description"

        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withText("Detail Test")).perform(ViewActions.click())
        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withId(R.id.ideaName)).check(ViewAssertions.matches(ViewMatchers.withText(title)))
        Espresso.onView(ViewMatchers.withId(R.id.detailDesc)).check(ViewAssertions.matches(ViewMatchers.withText(desc)))
        Espresso.onView(ViewMatchers.withId(R.id.tagText)).check(ViewAssertions.matches(ViewMatchers.withText(tag)))
        Espresso.onView(ViewMatchers.withId(R.id.collaboratorText)).check(ViewAssertions.matches(ViewMatchers.withText(USERNAME)))

        Espresso.onView(withId(R.id.button)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.descriptionText1)).perform(ViewActions.clearText())
        Espresso.onView(withId(R.id.descriptionText1)).perform(ViewActions.typeText(newDesc))
        sleep(1000)
        Espresso.onView(withId(R.id.button)).perform(ViewActions.click())

        sleep(1000)
        Espresso.onView(withId(R.id.detailDesc)).check(matches(withText(newDesc)))
    }

    @Test
    fun check_add_tag() {
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withText("Detail Test")).perform(ViewActions.click())
        Thread.sleep(1000)

        Espresso.onView(withId(R.id.tagButton)).perform(ViewActions.click())
        sleep(1000)

        Espresso.onView(withId(R.id.et_tag)).perform(ViewActions.typeText("Tag2"))
        Espresso.onView(withId(R.id.btn_add_tag)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click())
        sleep(1000)

        Espresso.onView(withId(R.id.tagText)).check(matches(withText("Test\nTag2")))

    }

    @Test
    fun check_view_tasks() {
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withText("Detail Test")).perform(ViewActions.click())
        Thread.sleep(1000)

        Espresso.onView(withId(R.id.taskButton)).perform(ViewActions.click())
        sleep(1000)

        Espresso.onView(withId(R.id.fab_item)).perform(ViewActions.click())
        sleep(1000)
        Espresso.onView(withId(R.id.ev_todo)).perform(ViewActions.typeText("New Task2"))
        Espresso.onView(withText("Add")).perform(ViewActions.click())
        sleep(1000)

        Espresso.onView(withText("New Task2")).check(matches(withText("New Task2")))

    }

    @Test
    fun check_complete_tasks() {
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withText("Detail Test")).perform(ViewActions.click())
        Thread.sleep(1000)

        Espresso.onView(withId(R.id.taskButton)).perform(ViewActions.click())
        sleep(1000)

        Espresso.onView(withText("New Task1")).perform(ViewActions.click())
        sleep(1000)

        Espresso.onView(withId(R.id.compTasks)).check(matches(withText("New Task1\n")))
    }
}