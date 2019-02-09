package incubator.app.tasks

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*ic_logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_in))
        Handler().postDelayed({
            ic_logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_out))
            Handler().postDelayed({
                ic_logo.visibility = View.GONE
                startActivity(Intent(this,DashboardActivity::class.java))
                finish()
            },500)
        },1500)*/
        startActivity(Intent(this, DashboardActivity::class.java))
        //finish()

    }
}
