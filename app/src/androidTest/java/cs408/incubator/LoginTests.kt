package cs408.incubator

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.text.InputType
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import io.grpc.LoadBalancer.PickResult.withError
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class LoginTests {

    @get:Rule
    val rule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    lateinit var loginActivity: LoginActivity
    lateinit var mdevice: UiDevice


    @Before
    fun setup() {
        loginActivity = rule.activity
        mdevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    }

    @Test
    fun blank_email() {
        val email = ""
        val password = "emptyMail"

        Espresso.onView(withId(R.id.password)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(withId(R.id.btn_login)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.email)).check(matches(hasErrorText("Enter email address!")))
    }

    @Test
    fun blank_password() {
        val email = "empty@password.com"
        val password = ""
        //sleep(1000)

        mdevice.findObject(By.res("cs408.incubator","email")).text = email

        Espresso.onView(withId(R.id.btn_login)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.password)).check(matches(hasErrorText("Enter password!")))
    }

    @Test
    fun invalid_credentials() {
        val email = "test@test.com"
        val password = "Incorrect"

        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","email")).text = email
        Espresso.onView(withId(R.id.password)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(withId(R.id.btn_login)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.auth_failed)).inRoot(withDecorView(not(rule.activity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun valid_credentials() {
        var email = "abca@abca.com"
        val password = "1234567"

        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","email")).text = email
        Espresso.onView(withId(R.id.password)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(withId(R.id.btn_login)).perform(ViewActions.click())
        sleep(1000)
        Espresso.onView(withContentDescription("Navigate up")).perform(ViewActions.click())
        sleep(1000)
        Espresso.onView(withText("Profile")).perform(click())
        sleep(2000)
        Espresso.onView(withId(R.id.sign_out)).perform(click())


    }

    @Test
    fun check_signup() {
        sleep(1000)
        mdevice.findObject(By.res("cs408.incubator","btn_signup")).click()
        sleep(2000)
        mdevice.hasObject(By.res("cs408.incubator","sign_up_button"))
        Espresso.onView(withId(R.id.sign_in_button)).perform(click())
        sleep(500)
        Espresso.onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }


}