package firestore_library

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

val settings = FirebaseFirestoreSettings.Builder()
        .build()

fun addIdea() {
    val doc: CollectionReference
    doc = getDB().collection("Ideas")
    var map = HashMap<String,Any>()
    map.put("1","Test 1")

    getDB().collection("Ideas").add(map)
            .addOnSuccessListener {
                println("Success" + it.id )
            }
}

fun getDB(): FirebaseFirestore {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    db.setFirestoreSettings(settings)
    return db
}