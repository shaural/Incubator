package cs408.incubator

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.text.InputType
import io.grpc.LoadBalancer.PickResult.withError
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class LoginActivityInstrumentationTest {

    @get:Rule
    val rule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    lateinit var loginActivity: LoginActivity

    @Before
    fun setup() {
        loginActivity = rule.activity
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

    /**@Test
    fun blank_password() {
        val email = "empty@password.com"
        val password = ""
        //sleep(1000)

        Espresso.onView(withId(R.id.email)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.email)).perform(ViewActions.typeText(email))

        Espresso.onView(withId(R.id.btn_login)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.password)).check(matches(hasErrorText("Enter password!")))
    }

    @Test
    fun invalid_credentials() {
        val email = "test@test.com"
        val password = "Incorrect"

        sleep(1000)
        Espresso.onView(withId(R.id.email)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.email)).perform(ViewActions.typeText(email))
        Espresso.onView(withId(R.id.password)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText(password))

        Espresso.onView(withId(R.id.btn_login)).perform(ViewActions.click())
    }*/


}