package cs408.incubator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_add_idea.*

class AddIdeaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_idea)

        setSupportActionBar(addIdeaToolbar)
        val actionbarEvent = supportActionBar
        actionbarEvent?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_idea,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            return when(item.itemId){
                R.id.confirm -> {
                    val intent = Intent(this,MainIdeasActivity::class.java)
                    setResult(1,intent)
                    finish()
                    true
                }
                android.R.id.home -> {
                    val intent = Intent(this,MainIdeasActivity::class.java)
                    setResult(-1,intent)
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return true
    }
}