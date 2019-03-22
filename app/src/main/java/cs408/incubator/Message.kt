package cs408.incubator

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Message(
        val messageText: String = "",
        val fromUser: String = "",
        @ServerTimestamp
        val sentAt: Date? = null
)