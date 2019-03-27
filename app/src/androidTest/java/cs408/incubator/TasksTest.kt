package cs408.incubator

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import firestore_library.updateUserName
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.random.Random

class TasksTest {
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
            title = "TaskTest-" + Random.nextInt()
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
    fun addTasks() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.taskButton)).perform(click())
        sleep(500)

        Espresso.onView(withId(R.id.fab_item)).perform(click())
        sleep(100)
        Espresso.onView(withId(R.id.ev_todo)).perform(typeText("New Task"))
        Espresso.onView(withText("Add")).perform(click())
        sleep(100)
        Espresso.onView(withText("New Task")).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.fab_item)).perform(click())
        sleep(100)
        Espresso.onView(withId(R.id.ev_todo)).perform(typeText("Another Task"))
        Espresso.onView(withText("Add")).perform(click())
        sleep(100)
        Espresso.onView(withText("Another Task")).check(matches(isDisplayed()))

    }

    @Test
    fun editTask() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.taskButton)).perform(click())
        sleep(500)

        Espresso.onView(withId(R.id.fab_item)).perform(click())
        sleep(100)
        Espresso.onView(withId(R.id.ev_todo)).perform(typeText("Edit Task"))
        Espresso.onView(withText("Add")).perform(click())
        sleep(100)
        Espresso.onView(withText("Edit Task")).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.iv_edit)).perform(click())
        sleep(100)
        Espresso.onView(withId(R.id.ev_todo)).perform(clearText(),typeText("Task Edited"))
        Espresso.onView(withText("Update")).perform(click())
        Espresso.onView(withText("Edit Task")).check(doesNotExist())
        Espresso.onView(withText("Task Edited")).check(matches(isDisplayed()))
    }

    @Test
    fun deleteTask() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.taskButton)).perform(click())
        sleep(500)

        Espresso.onView(withId(R.id.fab_item)).perform(click())
        sleep(100)
        Espresso.onView(withId(R.id.ev_todo)).perform(typeText("Delete Task"))
        Espresso.onView(withText("Add")).perform(click())
        sleep(100)
        Espresso.onView(withText("Delete Task")).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.iv_delete)).perform(click())
        sleep(100)
        Espresso.onView(withText("Continue")).perform(click())
        Espresso.onView(withText("Delete Task")).check(doesNotExist())
    }

    @Test
    fun markTasks() {
        sleep(1000)
        Espresso.onView(withText(title)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.taskButton)).perform(click())
        sleep(500)

        Espresso.onView(withId(R.id.fab_item)).perform(click())
        sleep(100)
        Espresso.onView(withId(R.id.ev_todo)).perform(typeText("Task1"))
        Espresso.onView(withText("Add")).perform(click())
        sleep(100)
        Espresso.onView(withText("Task1")).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.fab_item)).perform(click())
        sleep(100)
        Espresso.onView(withId(R.id.ev_todo)).perform(typeText("Task2"))
        Espresso.onView(withText("Add")).perform(click())
        sleep(100)

        Espresso.onView(withText("Task2")).check(matches(isDisplayed())).perform(click())
        Espresso.onView(withId(R.id.compTasks)).check(matches(withText("Task2\n")))

        Espresso.onView(withText("Task1")).check(matches(isDisplayed())).perform(click())
        Espresso.onView(withId(R.id.compTasks)).check(matches(withText("Task2\nTask1\n")))




    }
}