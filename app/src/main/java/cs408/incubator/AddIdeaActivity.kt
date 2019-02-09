package cs408.incubator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import firestore_library.addIdea
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

    fun returnIdea(id : String){
        Toast.makeText(applicationContext,"Success",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MainIdeasActivity::class.java).apply {
            putExtra("IDEA_TITLE",findViewById<EditText>(R.id.addTitle).text.toString())
            putExtra("IDEA_ID",id)
        }
        setResult(1,intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            return when(item.itemId){
                R.id.confirm -> {
                    //TODO --> Testing for all current components of Idea

                    val idea = Idea()
                    idea.setTitle(findViewById<EditText>(R.id.addTitle).text.toString())
                    idea.setDesc(findViewById<EditText>(R.id.addDesc).text.toString())
                    idea.setCollaborators(findViewById<EditText>(R.id.addCollab).text.toString())
                    idea.setTags(findViewById<EditText>(R.id.addTag).text.toString())

                    Toast.makeText(applicationContext,idea.getTitle(),Toast.LENGTH_SHORT).show()

                    addIdea(idea,::returnIdea)

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