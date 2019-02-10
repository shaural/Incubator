package firestore_library

import android.util.Log
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
    var map = HashMap<String,Any>()
    map.put("Name",idea.getTitle())
    map.put("Description",idea.getDesc())
    map.put("Collaborators",idea.getCollaborators())
    map.put("Tags",idea.getTags())
    var ideaRef = ""
    getDB().collection("Ideas").add(map)
            .addOnSuccessListener {
                ideaRef = it.id
                println("Success" + it.id )

                getDB().collection("Users").document(USERNAME)
                        .update("Ideas_Owned",FieldValue.arrayUnion(ideaRef))
                        .addOnSuccessListener {
                            println("Added ID to User")
                        }
                        .addOnFailureListener {
                            println("Fail! ID not added to User")
                        }

                callback(ideaRef)
            }
            .addOnFailureListener {
                println("FAILLL")
            }

}

fun getIdeas(callback: (ArrayList<String>) -> Unit){

    val ideakeys = ArrayList<String>()
    getDB().collection("Users").document(USERNAME).get()
            .addOnSuccessListener {
                ideakeys.addAll(it["Ideas_Owned"] as Collection<String>)
                println(ideakeys)
                callback(ideakeys)
            }
            .addOnFailureListener {
                println("Failure")
            }

}

fun getIdeasByID(key: String, callback: (String) -> Unit) {

    getDB().collection("Ideas").document(key).get()
            .addOnSuccessListener { idea ->
                val ideaInfo = "" + idea["Name"] + "-"+ key
                println(ideaInfo)
                callback(ideaInfo)
            }
            .addOnFailureListener {
                println("Failed to find Idea with $key")
            }

}

fun addUser() {
    val blankList = ArrayList<String>()
    var map = HashMap<String, Any>()
    map.put("Name", "Test User")
    map.put("Ideas_Owned", blankList)
    getDB().collection("Users").document(USERNAME).set(map)
            .addOnSuccessListener {
                println("User Added")
            }
            .addOnFailureListener {
                println("Failed adding user")
            }
}

fun getDB(): FirebaseFirestore {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    db.setFirestoreSettings(settings)
    return db
}