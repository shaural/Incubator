package cs408.incubator

import android.app.Activity
import android.support.design.widget.FloatingActionButton
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import firestore_library.updateUserName
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.*


class AddIdeaActivityInstrumentationTest {

    @get:Rule
    public val rule = ActivityTestRule<AddIdeaActivity>(AddIdeaActivity::class.java)

    @get:Rule
    val rule2 = ActivityTestRule<MainIdeasActivity>(MainIdeasActivity::class.java)



    lateinit var addIdeaActivity:AddIdeaActivity
    lateinit var mainIdeasActivity: MainIdeasActivity

    @Before
    fun setUp() {
        updateUserName("newus@gmail.com")
        addIdeaActivity = rule.activity
        mainIdeasActivity = rule2.activity
    }


    @Test
    fun check_single_invalid_email() {
        val title = "Check_Invalid_Email_1"
        val email = "invalid email"
        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView(withId(R.id.addCollab))
                .perform(ViewActions.typeText(email))

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.verification
        assertEquals(verify,false)
    }

    @Test
    fun check_multiple_invalid_email() {
        val title = "Check_Invalid_Email_2"
        val email = "abc@gmail.com, invalid-email"

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView(withId(R.id.addCollab))
                .perform(ViewActions.typeText(email))

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.verification
        assertEquals(verify,false)
    }

    /**@Test
    fun check_nonexisting_user() {
        val title = "Check_Invalid_collaborator"
        val email = "notexisting@gmail.com"

        Espresso.onView((withId(R.id.addTitle)))
                .perform(ViewActions.typeText(title))

        Espresso.onView(withId(R.id.addCollab))
                .perform(ViewActions.typeText(email))

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.verification
        assertEquals(verify,false)
    }*/

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
                .perform(ViewActions.typeText(email))

        Espresso.onView(withId(R.id.confirm))
                .perform(ViewActions.click())

        val verify = addIdeaActivity.verification
        println("no"+verify)
        assertEquals(verify,true)

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