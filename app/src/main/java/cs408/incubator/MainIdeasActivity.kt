package cs408.incubator

import android.app.PendingIntent.getActivity
import android.content.ClipData
import android.content.ClipDescription
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
import com.woxthebox.draglistview.DragListView
import kotlinx.android.synthetic.main.activity_main_ideas.*
import android.widget.Toast
import com.woxthebox.draglistview.DragItem
import com.woxthebox.draglistview.DragItemAdapter


class MainIdeasActivity : AppCompatActivity() {

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

        val mDragList = findViewById<DragListView>(R.id.ideaList)

        mDragList.setDragListListener(object : DragListView.DragListListener {
            override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemDragStarted(position: Int) {
                Toast.makeText(applicationContext, "Start - position: $position", Toast.LENGTH_SHORT).show()
            }

            override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                if (fromPosition != toPosition) {
                    Toast.makeText(applicationContext, "End - position: $toPosition", Toast.LENGTH_SHORT).show()
                }
            }
        })

        var ideaArray = ArrayList<Pair<Long,String>>()
        for(i in 0..25){
            ideaArray.add(Pair(i.toLong(), "Idea-$i"))
        }

        mDragList.setLayoutManager(LinearLayoutManager(applicationContext))
        val listAdapter = IdeaItemAdapter(ideaArray,R.layout.idea_item,true)
        mDragList.setAdapter(listAdapter,true)
        mDragList.setCanDragHorizontally(false)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->

        }
    }

}
