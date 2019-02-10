package cs408.incubator

import android.app.PendingIntent.getActivity
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.ShadowDrawableWrapper
import android.support.design.widget.Snackbar
import android.support.v4.util.Pair
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.woxthebox.draglistview.DragListView
import kotlinx.android.synthetic.main.activity_main_ideas.*
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.woxthebox.draglistview.DragItem
import com.woxthebox.draglistview.DragItemAdapter
import firestore_library.*
import java.io.File


class MainIdeasActivity : AppCompatActivity() {
    val REQ_CODE = 1
    var count = (0).toLong()
    private lateinit var ideaInfoList: LinkedHashMap<String,String>
    private lateinit var ideaByPrio: ArrayList<String>
    private lateinit var mDragList: DragListView
    private lateinit var ideaArray: ArrayList<Pair<Long,String>>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ideas)
        setSupportActionBar(toolbar)
        val actionbarEvent: ActionBar? = supportActionBar
        actionbarEvent.apply {
            this!!.setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }


        mDragList = findViewById<DragListView>(R.id.ideaList)

        mDragList.setDragListListener(object : DragListView.DragListListener {
            override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemDragStarted(position: Int) {
                Toast.makeText(applicationContext, "Start - position: $position", Toast.LENGTH_SHORT).show()
            }

            override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                if (fromPosition != toPosition) {
                    /**
                     * This will get you a list of Pair(Long, String).
                     * The order will be the "Priority" order
                     * All strings will be of the form "Idea Name"-"Idea ID"
                     */
                    //println(mDragList.adapter.itemList.toString())
                    Toast.makeText(applicationContext, "End - position: $toPosition", Toast.LENGTH_SHORT).show()

                    val list:ArrayList<Pair<Long,String>> = mDragList.adapter.itemList as ArrayList<Pair<Long,String>>
                    val newList:ArrayList<String> = arrayListOf()
                    for (item in list) {
//                        setPriority(item.second.toString()
//                                .substring(item.second.toString().indexOf('-')+1,item.second.toString().length))
                        newList.add(item.second.toString().substring(item.second.toString().indexOf('-')+1,item.second.toString().length))
                    }
                    setPriority(newList)
                }
            }
        })

        ideaArray = ArrayList()
        getIdeasByPriority(::getIdeaPriority)


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            val intent = Intent(this,AddIdeaActivity::class.java)
            startActivityForResult(intent,REQ_CODE)
        }


    }

    fun getIdeaPriority(keys: ArrayList<String>){
        if(!keys.isEmpty()) {
            ideaByPrio = keys
            println(ideaByPrio.toString())
        }
        getIdeas(::userIdeaKeys)
    }

    fun userIdeaKeys(keys: ArrayList<String>){
        ideaInfoList = LinkedHashMap()
        if(keys.isEmpty() || keys.size<=0)
            return

        //count = keys.size.toLong()
        for(i in ideaByPrio){
            if(keys.contains(i))
                keys.remove(i)
        }

        println("KEYS" + keys.toString())

        if(keys.size > 0) {
            for(key in keys)
                ideaInfoList.put(key,"")
            for(key in ideaByPrio)
                ideaInfoList.put(key,"")

            count = (keys.size + ideaByPrio.size).toLong()
            for (id in keys) {

                getIdeasByID(id, ::addToIdeaList)
            }
            for(id in ideaByPrio){
                getIdeasByID(id,::addToIdeaList)
            }
        }
        else{
            for(key in ideaByPrio)
                ideaInfoList.put(key,"")
            count = ideaByPrio.size.toLong()
            for(id in ideaByPrio){
                println(id)
                getIdeasByID(id,::addToIdeaList)
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addToIdeaList(ideaInfo : String){
        val ideaVals = ideaInfo.split("-")
        ideaInfoList.replace(ideaVals[1],ideaVals[0])
        count--
        if(count == 0.toLong()){
            println(ideaInfoList.toString())
            var counter = 0
            for(i in ideaInfoList.entries){
                val title = i.value+"-"+i.key
                ideaArray.add(Pair(counter.toLong(),title))
                counter++
            }

            mDragList.setLayoutManager(LinearLayoutManager(applicationContext))
            val listAdapter = IdeaItemAdapter(ideaArray,R.layout.idea_item,true)
            mDragList.setAdapter(listAdapter,false)
            mDragList.setCanDragHorizontally(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQ_CODE){
            if(resultCode == 1){
                val layout = findViewById<LinearLayout>(R.id.linList)
                layout.removeAllViews()
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                layout.addView(mDragList,params)

                val ideaTitle = data?.getStringExtra("IDEA_TITLE")
                val ideaID = data?.getStringExtra("IDEA_ID")
                val v: Long = (ideaArray.size + 1).toLong()

                val newArray = ArrayList<Pair<Long,String>>()
                newArray.add(Pair(v,"$ideaTitle-$ideaID"))
                newArray.addAll(ideaArray)


                mDragList.setLayoutManager(LinearLayoutManager(applicationContext))
                val listAdapter = IdeaItemAdapter(newArray,R.layout.idea_item,true)
                mDragList.setAdapter(listAdapter,true)
                mDragList.setCanDragHorizontally(false)
                ideaArray.add(Pair(v,"$ideaTitle-$ideaID"))
            }
            else if(resultCode == -1){
                Toast.makeText(applicationContext,"No idea added",Toast.LENGTH_SHORT).show()
            }
        }

    }

}
