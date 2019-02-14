package cs408.incubator

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.content.Intent
import android.provider.Settings.Global.getString
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_item.*
import java.util.*


//import cs408.incubator.DTO.DBHandler
import cs408.incubator.DTO.INTENT_TODO_ID
import cs408.incubator.DTO.INTENT_TODO_NAME
import cs408.incubator.DTO.ToDoItem

import kotlinx.android.synthetic.main.activity_item.*
import kotlin.collections.ArrayList


val string = "f89JEFF2SHcnjnv81pDG"
val stringc = "rzWkG55B8gES2odXt94H"
class ItemActivity : AppCompatActivity() {

    var todoId: Long = -1
    var list: MutableList<ToDoItem>? = null
    var adapter : ItemAdapter? = null
    var touchHelper : ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        setSupportActionBar(item_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = intent.getStringExtra(INTENT_TODO_NAME)
        todoId = intent.getLongExtra(INTENT_TODO_ID, -1)

        rv_item.layoutManager = LinearLayoutManager(this)

        readTasks()

        fab_item.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add ToDo Item")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.ev_todo)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (toDoName.text.isNotEmpty()) {
                    val item = ToDoItem()
                    item.itemName = toDoName.text.toString()
                    //add to firebase
                    // Access a Cloud Firestore instance from your Activity
                    val db = FirebaseFirestore.getInstance()
                    val docRef = db.collection("Ideas").document(string)
                    docRef.update("Tasks", FieldValue.arrayUnion(toDoName.text.toString()))
                    item.toDoId = todoId
                    item.isCompleted = false
                   // dbHandler.addToDoItem(item)
                    readTasks()
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
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update ToDo Item")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.ev_todo)
        toDoName.setText(item.itemName)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Ideas").document(string)
        docRef.update("Tasks", FieldValue.arrayRemove(item.itemName))
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {

                item.itemName = toDoName.text.toString()
                //add new item
                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("Ideas").document(string)
                docRef.update("Tasks", FieldValue.arrayUnion(toDoName.text.toString()))
                item.toDoId = todoId
                item.isCompleted = false
                //dbHandler.updateToDoItem(item)
                readTasks()
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    override fun onResume() {
        readTasks()
        super.onResume()
    }


    class ItemAdapter(val activity: ItemActivity, val list: MutableList<ToDoItem>) :
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
                val docRefc = dbc.collection("Ideas").document(string)
                docRefc.update("ctasks", FieldValue.arrayUnion(list[p1].itemName))
                //add to clist for ui


                //clist.add(list[p1].itemName)

                //remove from the firebase task list
                //activity.dbHandler.updateToDoItem(list[p1])
                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("Ideas").document(string)
                docRef.update("Tasks", FieldValue.arrayRemove(list[p1].itemName))
                activity.readTasks()
            }
            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Are you sure")
                dialog.setMessage("Do you want to delete this item ?")
                dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                    //remove
                    val db = FirebaseFirestore.getInstance()
                    val docRef = db.collection("Ideas").document(string)
                    Toast.makeText(activity,list[p1].itemName,Toast.LENGTH_SHORT).show()
                    docRef.update("Tasks", FieldValue.arrayRemove(list[p1].itemName))
                            .addOnSuccessListener {
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
        val docRef = db.collection("Ideas").document(string)
        docRef.get().addOnSuccessListener {
            if(it["Tasks"] != null) {
                findViewById<TextView>(R.id.pending).visibility = View.VISIBLE
                tasks = it["Tasks"] as ArrayList<String>
                println("Got the tasks " + tasks.toString())
                for (task in tasks) {
                    val item = ToDoItem()
                    item.itemName = task
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
