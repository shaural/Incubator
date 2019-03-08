package cs408.incubator

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_task.*
import java.util.*


//import cs408.incubator.DTO.DBHandler
import cs408.incubator.DTO.INTENT_TODO_ID
import cs408.incubator.DTO.INTENT_TODO_NAME
import cs408.incubator.DTO.ToDoItem
import firestore_library.USERNAME
import java.text.SimpleDateFormat

import kotlin.collections.ArrayList



var IDEA_ID = ""
class TaskActivity : AppCompatActivity() {


    var todoId: Long = -1
    var list: MutableList<ToDoItem>? = null
    var adapter : ItemAdapter? = null
    var touchHelper : ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        IDEA_ID = intent.getStringExtra("ideaID")

        setSupportActionBar(item_toolbar)
        val actionbarEvent = supportActionBar
        actionbarEvent?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }


        rv_item.layoutManager = LinearLayoutManager(this)

        readTasks()

        fab_item.setOnClickListener {
            var cal = Calendar.getInstance()
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add ToDo Item")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.ev_todo)
            val deadline = view.findViewById<EditText>(R.id.deadline)
            deadline.setOnClickListener {
                val current = cal.timeInMillis
                val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    cal.set(Calendar.YEAR,y)
                    cal.set(Calendar.MONTH,m)
                    cal.set(Calendar.DAY_OF_MONTH,d)

                    if(cal.timeInMillis < current){
                        Toast.makeText(this,"Deadline cannot be set!",Toast.LENGTH_SHORT).show()
                        cal = Calendar.getInstance()
                    }
                    else {
                        val format = "MMM d, yyyy"
                        val sdf = SimpleDateFormat(format, Locale.US)
                        deadline.setText(sdf.format(cal.time))
                    }
                }
                DatePickerDialog(this,listener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
            }

            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (toDoName.text.isNotEmpty()) {

                    if(deadline.text.isNullOrEmpty()) {
                        val item = ToDoItem()
                        item.itemName = toDoName.text.toString()
                        //add to firebase
                        // Access a Cloud Firestore instance from your Activity
                        val db = FirebaseFirestore.getInstance()
                        val docRef = db.collection("Ideas").document(IDEA_ID)
                        docRef.update("Tasks", FieldValue.arrayUnion(toDoName.text.toString()))
                        docRef.update("Log", FieldValue.arrayUnion(genLogStr(USERNAME, "add", "task", toDoName.text.toString())))
                        item.toDoId = todoId
                        item.isCompleted = false
                        // dbHandler.addToDoItem(item)
                        readTasks()
                    }
                    else {
                        addTask(toDoName.text.toString(),deadline.text.toString(),cal.timeInMillis)
                    }
                }
                else {
                    Toast.makeText(this,"Task cannot be empty!",Toast.LENGTH_SHORT).show()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }

        touchHelper =
                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                    override fun onMove(
                            p0: RecyclerView,
                            p1: RecyclerView.ViewHolder,
                            p2: RecyclerView.ViewHolder
                    ): Boolean {
                        val sourcePosition = p1.adapterPosition
                        val targetPosition = p2.adapterPosition
                        Collections.swap(list,sourcePosition,targetPosition)
                        adapter?.notifyItemMoved(sourcePosition,targetPosition)
                        return true
                    }

                    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })

        touchHelper?.attachToRecyclerView(rv_item)

    }

    fun updateItem(item: ToDoItem) {
        var cal = Calendar.getInstance()
        val current = cal.timeInMillis
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update ToDo Item")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.ev_todo)
        val deadline = view.findViewById<EditText>(R.id.deadline)

        toDoName.setText(item.itemName)
        deadline.setText(item.deadline)

        deadline.setOnClickListener {

            val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                cal.set(Calendar.YEAR,y)
                cal.set(Calendar.MONTH,m)
                cal.set(Calendar.DAY_OF_MONTH,d)

                if(cal.timeInMillis < current){
                    Toast.makeText(this,"Deadline cannot be set!",Toast.LENGTH_SHORT).show()
                    cal = Calendar.getInstance()
                }
                else {
                    val format = "MMM d, yyyy"
                    val sdf = SimpleDateFormat(format, Locale.US)
                    deadline.setText(sdf.format(cal.time))
                }
            }
            DatePickerDialog(this,listener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Ideas").document(IDEA_ID)
        if(item.deadline.isNotEmpty()){
            val taskName =  "${item.itemName}~${item.notifid}~${item.deadline}"
            docRef.update("Tasks", FieldValue.arrayRemove(taskName))
        }
        else
            docRef.update("Tasks", FieldValue.arrayRemove(item.itemName))

        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {
                if(deadline.text.isNullOrEmpty()) {
                    item.itemName = toDoName.text.toString()
                    //add new item
                    docRef.update("Tasks", FieldValue.arrayUnion(toDoName.text.toString()))
                    var str_log = item.itemName + "_to_" + toDoName.text.toString()
                    docRef.update("Log", FieldValue.arrayUnion(genLogStr(USERNAME, "update", "task", str_log)))

                    item.toDoId = todoId
                    item.isCompleted = false
                    //dbHandler.updateToDoItem(item)
                    readTasks()
                }
                else{
                    reschedNotification(toDoName.text.toString(),deadline.text.toString(),cal.timeInMillis,item.notifid)
                }
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    fun reschedNotification(name:String,deadline:String,delay: Long,id:Long){
        val notification = getNotification(name)
        val notif = Intent(this,NotificationPublisher::class.java).apply {
            putExtra("notification",notification)
            putExtra("notification-id",id)
        }

        val pendingIntent = PendingIntent.getBroadcast(this,0,notif,PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC,delay,pendingIntent)

        val item = ToDoItem()
        item.itemName = name
        val notifData = "$name~$id~$deadline"
        //add to firebase
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Ideas").document(IDEA_ID)
        docRef.update("Tasks", FieldValue.arrayUnion(notifData))
        docRef.update("Log", FieldValue.arrayUnion(genLogStr(USERNAME, "updated", "task", notifData)))
        item.toDoId = todoId
        item.isCompleted = false

        // dbHandler.addToDoItem(item)
        readTasks()
    }

    fun addTask(name : String, deadline: String, delay: Long) {

        val notification = getNotification(name)
        val cur = SystemClock.elapsedRealtime()
        val notif = Intent(this,NotificationPublisher::class.java).apply {
            putExtra("notification",notification)
            putExtra("notification-id",cur)
        }

        val pendingIntent = PendingIntent.getBroadcast(this,0,notif,PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC,delay,pendingIntent)

        val item = ToDoItem()
        item.itemName = name
        val notifData = "$name~$cur~$deadline"
        //add to firebase
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Ideas").document(IDEA_ID)
        docRef.update("Tasks", FieldValue.arrayUnion(notifData))
        docRef.update("Log", FieldValue.arrayUnion(genLogStr(USERNAME, "add", "task", notifData)))
        item.toDoId = todoId
        item.isCompleted = false
        // dbHandler.addToDoItem(item)
        readTasks()



    }

    fun getNotification(content : String):Notification {
        val i = Intent(this,MainIdeasActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,i,0)
        val builder = NotificationCompat.Builder(this,"INCUBATOR")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Deadline Coming up")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setAutoCancel(true)

        return builder.build()


    }

    override fun onResume() {
        readTasks()
        super.onResume()
    }


    class ItemAdapter(val activity: TaskActivity, val list: MutableList<ToDoItem>) :
            RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                    LayoutInflater.from(activity).inflate(
                            R.layout.rv_child_item,
                            p0,
                            false
                    )
            )
        }



        override fun getItemCount(): Int {
            return list.size
        }


        //@SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {

                //manage firebase add to new list first then delete


                //activity.refreshList()

            holder.itemName.text = list[p1].itemName
            holder.itemName.isChecked = list[p1].isCompleted
            holder.itemName.setOnClickListener {
                //if it's checked
                Toast.makeText(activity,"Checked",Toast.LENGTH_SHORT).show()
                //add to completed task firebase
                list[p1].isCompleted = !list[p1].isCompleted
                val dbc = FirebaseFirestore.getInstance()
                val docRefc = dbc.collection("Ideas").document(IDEA_ID)
                docRefc.update("ctasks", FieldValue.arrayUnion(list[p1].itemName))
                docRefc.update("Log", FieldValue.arrayUnion(genLogStr(USERNAME, "check", "task", list[p1].itemName)))

                var taskname = list[p1].itemName
                if(list[p1].deadline.isNotEmpty()){
                    taskname+="~${list[p1].notifid}~${list[p1].deadline}"
                    val nm = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.cancel(list[p1].notifid.toInt())
                }
                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("Ideas").document(IDEA_ID)
                docRef.update("Tasks", FieldValue.arrayRemove(taskname))
                activity.readTasks()
            }

            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Are you sure")
                dialog.setMessage("Do you want to delete this item ?")
                dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                    //remove
                    var taskname = list[p1].itemName
                    if(list[p1].deadline.isNotEmpty()){
                        taskname+="~${list[p1].notifid}~${list[p1].deadline}"
                        val nm = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        nm.cancel(list[p1].notifid.toInt())
                    }
                    val db = FirebaseFirestore.getInstance()
                    val docRef = db.collection("Ideas").document(IDEA_ID)
                    docRef.update("Tasks", FieldValue.arrayRemove(taskname))
                            .addOnSuccessListener {
                                docRef.update("Log", FieldValue.arrayUnion(genLogStr(USERNAME, "delete", "task", list[p1].itemName)))

                                Toast.makeText(activity,"Removed",Toast.LENGTH_SHORT).show()
                            }
                    activity.readTasks()

                }
                dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                }
                dialog.show()
            }
            holder.edit.setOnClickListener {
                activity.updateItem(list[p1])
            }

        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val itemName: CheckBox = v.findViewById(R.id.cb_item)
            val edit: ImageView = v.findViewById(R.id.iv_edit)
            val delete: ImageView = v.findViewById(R.id.iv_delete)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            val intent = Intent(applicationContext,IdeaDetailsActivity::class.java).apply {
                putExtra("ideaTag", IDEA_ID)
            }
            startActivity(intent)
            finish()

            true
        } else
            super.onOptionsItemSelected(item)
    }

    fun readTasks() {
        var tasks = ArrayList<String>()
        var taskList = ArrayList<ToDoItem>()
        var compTasks = ArrayList<String>()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Ideas").document(IDEA_ID)
        docRef.get().addOnSuccessListener {
            if(it["Tasks"] != null) {
                findViewById<TextView>(R.id.pending).visibility = View.VISIBLE
                tasks = it["Tasks"] as ArrayList<String>
                println("Got the tasks " + tasks.toString())
                for (task in tasks) {
                    val item = ToDoItem()
                    if(task.contains("~")){
                        val parts = task.split("~")
                        item.itemName = parts[0]
                        item.notifid = parts[1].toLong()
                        item.deadline = parts[2]
                    }
                    else {
                        item.itemName = task
                    }

                    item.isCompleted = false
                    item.toDoId = todoId
                    taskList.add(item)
                }
            }

            if(it["ctasks"]!=null){
                findViewById<LinearLayout>(R.id.rv2_item).visibility = View.VISIBLE
                var string = ""
                compTasks = it["ctasks"] as ArrayList<String>
                for(task in compTasks){
                    string += task+"\n"
                }

                findViewById<TextView>(R.id.compTasks).text = string
            }

            list = taskList
            adapter = ItemAdapter(this, list!!)
            rv_item.adapter = adapter
        }
    }

}
