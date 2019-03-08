package cs408.incubator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_mainchat.*
import android.view.*
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter

class ChatActivity: AppCompatActivity(){
    private var rootRef: FirebaseFirestore? = null
    val user = intent.extras.get("userID").toString()
    private var adapter: MessageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainchat)

        rootRef = FirebaseFirestore.getInstance()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val docP = intent.extras.get("ideaID").toString()
        val name = intent.extras.get("nametitle").toString()

        button.setOnClickListener {
            val messageText = edit_text.text.toString()

            val message = Message(messageText, user)

            rootRef!!.collection("Ideas").document(docP).collection("Messages").add(message)
            edit_text.text.clear()
        }

        val query = rootRef!!.collection("Ideas").document(docP).collection("Messages").orderBy("sentAt", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()
        adapter = MessageAdapter(options)
        recycle_view.adapter = adapter
        title = name

    }

    inner class MessageViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        internal fun setMessage(message: Message){
            val textView = view.findViewById<TextView>(R.id.chat_message)
            textView.text = message.messageText
        }
    }

    inner class MessageAdapter internal constructor(options: FirestoreRecyclerOptions<Message>) : FirestoreRecyclerAdapter<Message, MessageViewHolder>(options){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            return if (viewType == R.layout.item_chat_sent){
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_sent, parent, false)
                MessageViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_received, parent, false)
                MessageViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
            holder.setMessage(model)
        }

        override fun getItemViewType(position: Int) : Int{
            return if (user != getItem(position).fromUser){
                R.layout.item_chat_received
            } else {
                R.layout.item_chat_sent
            }
        }

        override fun onDataChanged(){
            recycle_view.layoutManager?.scrollToPosition(itemCount-1)
        }
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null){
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null){
            adapter!!.stopListening()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }
}