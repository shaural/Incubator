package firestore_library

import com.google.firebase.firestore.*
import cs408.incubator.Idea
import kotlin.reflect.KFunction1

val settings = FirebaseFirestoreSettings.Builder()
        .build()
val USERNAME = "ydassani"

fun addIdea(idea: Idea, callback: (String) -> Unit) {
    val doc: CollectionReference = getDB().collection("Ideas")
    var map = HashMap<String, Any>()
    map.put("Name", idea.getTitle())
    map.put("Description", idea.getDesc())
    map.put("Collaborators", idea.getCollaborators())
    map.put("Tags", idea.getTags())

    val ideaCollabs = idea.getCollaborators()
    ideaCollabs.add(USERNAME)

    var ideaRef = ""
    getDB().collection("Ideas").add(map)
            .addOnSuccessListener {
                ideaRef = it.id
                println("Success" + it.id)

                for (user in ideaCollabs) {
                    getDB().collection("Users").document(user)
                            .update("Ideas_Owned", FieldValue.arrayUnion(ideaRef))
                            .addOnSuccessListener {
                                println("Added ID to $user")
                            }
                            .addOnFailureListener {
                                println("Fail! ID not added to User")
                            }
                }

                callback(ideaRef)
            }
            .addOnFailureListener {
                println("FAILLL")
            }

}

fun getIdeas(callback: (ArrayList<String>) -> Unit) {

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

fun getSearch(query: String, callback: (ArrayList<String>) -> Unit) {
    var searchIdeas = ArrayList<String>()
    var userIdeas = ArrayList<String>()
    println("TEST")

    val docRef = getDB().collection("Users").document(USERNAME)
    docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
        if (e != null) {
//            Log.w(TAG, "Listen failed.", e)
            return@EventListener
        }

        if (snapshot != null && snapshot.exists()) {
            if(snapshot.get("Ideas_Owned") != null) {
                userIdeas = snapshot.get("Ideas_Owned") as ArrayList<String>
//            Log.d(TAG, "Current data: " + snapshot.data)
            } else {
                println("User idea list = null")
                callback(ArrayList())
            }
        } else {
//            Log.d(TAG, "Current data: null")
        }
    })
    getDB().collection("Ideas")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document != null && userIdeas.contains(document.id)) {
                        var temp = document.data["Name"].toString()
                        if (temp.contains(query)) {
                            // match
                            searchIdeas.add(document.id)
                        } else {
                            if(document.data["Tags"] != null) {
                                val list = document.data["Tags"] as ArrayList<String>
                                if (list.contains(query)) {
                                    // tag matched
                                    searchIdeas.add(document.id)
                                }
                            }
                        }
                    }
                }

                callback(searchIdeas)
            }
            .addOnFailureListener { exception ->
                println("Error in search method...")
//                Log.d(TAG, "Error getting documents: ", exception)
            }
}

fun getIdeasByID(key: String, callback: (String) -> Unit) {

    getDB().collection("Ideas").document(key).get()
            .addOnSuccessListener { idea ->
                val ideaInfo = "" + idea["Name"] + "-" + key
                println(ideaInfo)
                callback(ideaInfo)
            }
            .addOnFailureListener {
                println("Failed to find Idea with $key")
            }

}

fun verifyUsers(emails: String, callback: (Boolean) -> Unit) {
    val users = ArrayList<String>()
    if (emails.contains(",")) {
        val m = emails.split(",")
        for (mail in m)
            users.add(mail.trim())
    } else
        users.add(emails)

    for (user in users) {
        getDB().collection("Users").document(user).get()
                .addOnSuccessListener {
                    if (!it.exists()) {
                        println("No exist")
                        callback(false)
                    }

                }
                .addOnFailureListener {
                    println("Fail!")
                }
    }
    callback(true)
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