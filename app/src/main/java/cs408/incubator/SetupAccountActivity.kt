package cs408.incubator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import firestore_library.setUserDispName

class SetupAccountActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_setup_account)
    }

    fun setDisplayName(v: View){

        val name = findViewById<EditText>(R.id.dispName).text.toString()
        val reg = "[ A-z]+".toRegex()
        if(name.isEmpty())
            Toast.makeText(applicationContext,"Name cannot be empty!",Toast.LENGTH_SHORT).show()

        if(name.matches(reg)) {
            setUserDispName(name,::startMain)
        }
        else {
            Toast.makeText(applicationContext,"Invalid Characters in Name",Toast.LENGTH_SHORT).show()
        }


    }

    fun startMain(b : Boolean){
        if(b){
            val intent = Intent(this,MainIdeasActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}