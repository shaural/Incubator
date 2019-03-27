package cs408.incubator

import android.app.Activity
import android.support.design.widget.FloatingActionButton
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.isDialog
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import firestore_library.updateUserName
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.*
import java.lang.Thread.sleep


class AddIdeaTests {

    @get:Rule
    public val rule = ActivityTestRule<AddIdeaActivity>(AddIdeaActivity::class.java)

    @get:Rule
    val rule2 = ActivityTestRule<MainIdeasActivity>(MainIdeasActivity::class.java)



    lateinit var addIdeaActivity:AddIdeaActivity
    lateinit var mainIdeasActivity: MainIdeasActivity
    lateinit var mdevice:UiDevice


    @Before
    fun setUp() {
        addIdeaActivity = rule.activity
        mainIdeasActivity = rule2.activity
        mdevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }


    @Test
    fun check_single_invalid_email() {
        val title = "Check_Invalid_Email_1"
        val email = "invalid email"
        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title)).perform(ViewActions.closeSoftKeyboard())

        Espresso.onView(withId(R.id.addCollab))
                .perform(ViewActions.click())
        sleep(1000)

        mdevice.findObject(By.res("cs408.incubator","collabEmail")).text = email
        //sleep(1000)
        Espresso.onView(withText("Add"))
                .perform(ViewActions.click())


        val verify = addIdeaActivity.collabcheck

        assertEquals(verify,false)
    }

    @Test
    fun check_multiple_invalid_email() {
        val title = "Check_Invalid_Email_2"
        val email = "abc@abc.com"
        val email2 = "invalid-email"

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView(withId(R.id.addCollab))
                .perform(ViewActions.click())
        sleep(500)
        mdevice.findObject(By.res("cs408.incubator","collabEmail")).text = email

        Espresso.onView(withText("Add"))
                .perform(ViewActions.click())

        Espresso.onView(withId(R.id.addCollab))
                .perform(ViewActions.click())
        sleep(500)
        mdevice.findObject(By.res("cs408.incubator","collabEmail")).text = email2

        Espresso.onView(withText("Add"))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.collabcheck
        assertEquals(verify,false)
        Espresso.onView(withId(R.id.addCollab)).check(matches(withText(email)))
    }

    @Test
    fun check_nonexisting_user() {
        val title = "Check_Invalid_collaborator"
        val email = "notexisting@gmail.com"

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView(withId(R.id.addCollab))
                .perform(ViewActions.click())
        sleep(500)
        mdevice.findObject(By.res("cs408.incubator","collabEmail")).text = email

        Espresso.onView(withText("Add"))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.collabcheck
        assertEquals(verify,false)
    }

    @Test
    fun sucessful_add_idea() {
        val title = "Successful_add_idea"
        val tags = "Testing"

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView((withId(R.id.addTag)))
                .perform(ViewActions.typeText(tags))

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())


        val verify = addIdeaActivity.verification
        assertEquals(verify,true)
        assert(addIdeaActivity.isFinishing)
        assert(mainIdeasActivity.ideaInfoList.containsValue(title))

    }

    @Test
    fun successful_collaborator() {
        val title = "Check_valid_collaborator"
        val email = "newuser@gmail.com"

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView(withId(R.id.addCollab))
                .perform(ViewActions.click())
        sleep(500)
        mdevice.findObject(By.res("cs408.incubator","collabEmail")).text = email

        Espresso.onView(withText("Add"))
                .perform(ViewActions.click())

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.collabcheck
        assertEquals(verify,true)
        sleep(1000)
        assert(mainIdeasActivity.ideaInfoList.containsValue(title))


    }

    @Test
    fun check_blank_title(){
        val title = ""

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.verification
        println(verify)
        assertEquals(verify,false)
    }

    @Test
    fun check_long_title(){
        var title = ""
        for(i in 0..40){
            title += "abcd,"
        }

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.verification
        println(verify)
        assertEquals(verify,false)
    }



}