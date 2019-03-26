package cs408.incubator

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.random.Random.Default.nextInt

class SignUpTests {

    @get:Rule
    val rule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)
    //val rule2 = ActivityTestRule<SignUpActivity>(SignUpActivity::class.java)

    lateinit var loginActivity: LoginActivity
    //lateinit var signUpActivity: SignUpActivity
    lateinit var mdevice: UiDevice


    @Before
    fun setup() {
        loginActivity = rule.activity
        //signUpActivity = rule2.activity
        mdevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    }

    @Test
    fun check_register_flow() {
        Thread.sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","btn_signup")).click()
        sleep(1000)
        //signUpActivity.onResume()
        mdevice.hasObject(By.res("cs408.incubator","sign_up_button"))
        mdevice.findObject(By.res("cs408.incubator","sign_in_button")).click()
        sleep(1000)
        mdevice.hasObject(By.res("cs408.incubator","btn_signup"))


    }

    @Test
    fun blank_email() {
        val email = ""
        val password = "emptyMail"
        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","btn_signup")).click()
        sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Enter email address!")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun blank_password() {
        val email = "newemail@gmail.com"
        val password = ""

        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","btn_signup")).click()
        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","email")).text = email
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Enter password!")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun check_existing_user() {
        val email = "abca@abca.com"
        val password = "123456"

        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","btn_signup")).click()
        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","email")).text = email
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click())
        sleep(200)
        Espresso.onView(ViewMatchers.withText("Authentication failed. User Already Exists")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun invalid_email() {
        val email = "ab"
        val password = "123456"

        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","btn_signup")).click()
        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","email")).text = email
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Authentication failed. Invalid Email Format")).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun invalid_password() {
        val email = "abca@abca.com"
        val password = "12345"
        val errMsg = "Password too short, enter minimum 6 characters!"

        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","btn_signup")).click()
        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","email")).text = email
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(errMsg)).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun profile_creation() {
        val name = "@az"
        val email = "abcza"+nextInt()+"@gmail.com"
        val password = "123456"
        val errmsg = "Invalid Characters in Name"
        val errmsg2 = "Name cannot be empty!"

        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","btn_signup")).click()
        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","email")).text = email
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click())
        sleep(1000)

        mdevice.findObject(By.res("cs408.incubator","dispName")).text = name
        mdevice.findObject(By.res("cs408.incubator","submitName")).click()

        Espresso.onView(ViewMatchers.withText(errmsg)).inRoot(RootMatchers.withDecorView(IsNot.not(rule.activity.window.decorView))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","dispName")).text = "abcd"
        mdevice.findObject(By.res("cs408.incubator","submitName")).click()
        sleep(1000)

        Espresso.onView(withId(R.id.fab)).check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click())
        sleep(1000)
        Espresso.onView(ViewMatchers.withText("Profile")).perform(ViewActions.click())
        sleep(2000)
        Espresso.onView(withId(R.id.sign_out)).perform(ViewActions.click())
    }



}