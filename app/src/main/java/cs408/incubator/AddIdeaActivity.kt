package cs408.incubator

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import firestore_library.addIdea
import firestore_library.getDB
import firestore_library.verifyUsers
import kotlinx.android.synthetic.main.activity_add_idea.*
//import javax.swing.UIManager.put



class AddIdeaActivity : AppCompatActivity() {

    private var verification:Boolean = false

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

        // create entry in notes table when new idea created
        val data = HashMap<String, String>()
        data.put("Idea_Title", findViewById<EditText>(R.id.addTitle).text.toString())
        getDB().collection("Notes").document(id).set(data)


        val intent = Intent(this,MainIdeasActivity::class.java).apply {
            putExtra("IDEA_TITLE",findViewById<EditText>(R.id.addTitle).text.toString())
            putExtra("IDEA_ID",id)
        }
        setResult(1,intent)
        finish()
    }

    fun verifyFields() {
        val title = findViewById<EditText>(R.id.addTitle).text.toString()
        val collabs = findViewById<EditText>(R.id.addCollab).text.toString()

        if(title.isBlank()){
            Toast.makeText(applicationContext,"Idea Title Cannot be Blank",Toast.LENGTH_SHORT).show()
            verification = false
            return
        }
        else if(title.length > 150){
            Toast.makeText(applicationContext,"Idea Title too Long. [150 character Limit]",Toast.LENGTH_SHORT).show()
            verification = false
            return
        }
        else if(collabs.isNotEmpty()){
            if(!verifyEmail(collabs)) {
                Toast.makeText(applicationContext,"Invalid collaborator Email", Toast.LENGTH_SHORT).show()
                verification = false
            }
            else
                verifyUsers(collabs,::verifyUserExists)
        }
        else
            verification = true


    }

    fun verifyEmail(emails : String):Boolean{
        if(emails.contains(",")){
            val emailList = emails.split(",")
            for(mail in emailList){
                if(!Patterns.EMAIL_ADDRESS.matcher(mail.trim()).matches())
                    return false
            }
            return true
        }
        else
            return Patterns.EMAIL_ADDRESS.matcher(emails).matches()
    }

    fun verifyUserExists(bool : Boolean) {
        if(!bool){
            Toast.makeText(applicationContext,"User Not Found",Toast.LENGTH_SHORT).show()
            return
        }
        verification = true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            return when(item.itemId){
                R.id.confirm -> {

                    verifyFields()
                    if(verification) {

                        val idea = Idea()
                        idea.setTitle(findViewById<EditText>(R.id.addTitle).text.toString())
                        idea.setDesc(findViewById<EditText>(R.id.addDesc).text.toString())
                        idea.setCollaborators(findViewById<EditText>(R.id.addCollab).text.toString())
                        idea.setTags(findViewById<EditText>(R.id.addTag).text.toString())

                        Toast.makeText(applicationContext, idea.getTitle(), Toast.LENGTH_SHORT).show()
                        addIdea(idea, ::returnIdea)

                        true
                    }
                    else
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