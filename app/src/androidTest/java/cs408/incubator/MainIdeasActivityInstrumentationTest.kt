package cs408.incubator

import android.support.test.espresso.Espresso
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import com.woxthebox.draglistview.DragListView
import firestore_library.USERNAME
import firestore_library.getDB
import firestore_library.updateUserName
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class MainIdeasActivityInstrumentationTest {

    @get:Rule
    public val rule = ActivityTestRule<MainIdeasActivity>(MainIdeasActivity::class.java)

    lateinit var mainIdeasActivity: MainIdeasActivity

    @Before
    fun setUp() {
        mainIdeasActivity = rule.activity
    }

    @Test
    fun check_profile_UI() {
        Espresso.onView(withContentDescription("Navigate up")).perform(ViewActions.click())
        sleep(2000)
        Espresso.onView(withText("Profile")).perform(click())
        var name = ""
        getDB().collection("Users").document(USERNAME).get()
                .addOnSuccessListener {
                    name = it["name"].toString()
                    Espresso.onView(withId(R.id.display_name)).check(matches(withText(name)))
                }
    }

    @Test
    fun check_addIdea_UI(){
        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.addTitle)).check(matches(withHint(R.string.title)))
        Espresso.onView(withId(R.id.addDesc)).check(matches(withHint(R.string.desc)))
        Espresso.onView(withId(R.id.addCollab)).check(matches(withHint(R.string.collab)))
        Espresso.onView(withId(R.id.addTag)).check(matches(withHint(R.string.tags)))
    }

    @Test
    fun add_new_idea(){
        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())
        val title = "Successful_add_new_idea"
        val tags = "Testing"

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView((withId(R.id.addTag)))
                .perform(ViewActions.typeText(tags))

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())

        assert(mainIdeasActivity.ideaInfoList.containsValue(title))

    }

    @Test
    fun check_idea_details() {
        val title = "Detail Test"
        val desc = "Checking for details"
        val tag = "Test"
        sleep(2000)
        Espresso.onView(withText("Detail Test")).perform(ViewActions.click())
        sleep(1000)
        Espresso.onView(withId(R.id.ideaName)).check(matches(withText(title)))
        Espresso.onView(withId(R.id.detailDesc)).check(matches(withText(desc)))
        Espresso.onView(withId(R.id.tagText)).check(matches(withText(tag)))
        Espresso.onView(withId(R.id.collaboratorText)).check(matches(withText(USERNAME)))
    }


}