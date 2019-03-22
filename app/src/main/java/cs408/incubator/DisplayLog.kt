package cs408.incubator

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import firestore_library.getDB
import kotlinx.android.synthetic.main.activity_display_log.*
import kotlinx.android.synthetic.main.activity_mainchat.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DisplayLog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_log)

        setSupportActionBar(logToolbar)
        val actionbarEvent = supportActionBar
        actionbarEvent?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        generateLog()

    }

    fun generateLog() {
        val docId  =intent.getStringExtra("ideaID")
        getDB().collection("Ideas").document(docId)
                .get()
                .addOnSuccessListener {
                    if(it["Log"]!=null){
                        val log = it["Log"] as List<String>
                        addLogtoDisplay(log)
                    }
                }
    }

    fun addLogtoDisplay(list: List<String>) {
        val logList = findViewById<LinearLayout>(R.id.LogText)
        for(l in list){
            val pos = l.indexOf("-",8)
            val ts = l.substring(0,pos)
            var log = l.replace(ts,"")
            println("LOG "+ts+" "+log)

            val text = TextView(applicationContext)
            text.textSize = 10.0F
            text.setPadding(0,3,0,3)

            val frmat = SimpleDateFormat("MMM d,YYYY HH:mm", Locale.US)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS",Locale.ENGLISH)
                val date = LocalDate.parse(ts,formatter)
                //val time = frmat.format(date)
                text.text = "$date $log"

            } else {
                text.text = "$ts $log"
            }

            logList.addView(text)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            return when (item.itemId) {
                android.R.id.home -> {
                    val intent = Intent(this, IdeaDetailsActivity::class.java).apply {
                        putExtra("ideaTag",intent.getStringExtra("ideaID"))
                    }
                    finish()
                    startActivity(intent)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

        }
        return true
    }
}