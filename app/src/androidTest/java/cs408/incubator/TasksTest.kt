package cs408.incubator

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import firestore_library.updateUserName
import org.junit.Before
import org.junit.Rule
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
}