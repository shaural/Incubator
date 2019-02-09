package firestore_library

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import cs408.incubator.Idea

val settings = FirebaseFirestoreSettings.Builder()
        .build()
val USERNAME = "ydassani"

fun addIdea(idea : Idea, callback: (String)->Unit) {
    val doc: CollectionReference = getDB().collection("Ideas")
    var map = HashMap<String,Idea>()
    map.put("1",idea)
    var ideaRef = ""
    getDB().collection("Ideas").add(map)
            .addOnSuccessListener {
                ideaRef = it.id
                println("Success" + it.id )
                /**getDB().collection("Users").document(USERNAME)
                        .update("Ideas_Owned",FieldValue.arrayUnion(ideaRef))*/

                callback(ideaRef)
            }
            .addOnFailureListener {
                println("FAILLL")
            }

}

fun getDB(): FirebaseFirestore {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    db.setFirestoreSettings(settings)
    return db
}