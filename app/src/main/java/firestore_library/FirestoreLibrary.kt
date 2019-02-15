package firestore_library

import com.google.firebase.firestore.*
import cs408.incubator.Idea
import cs408.incubator.genLogStr
import java.text.SimpleDateFormat
import kotlin.reflect.KFunction1
import java.util.Calendar

val settings = FirebaseFirestoreSettings.Builder()
        .build()
var USERNAME = "test@test.com"

fun updateUserName(name: String){
    USERNAME = name
}

fun addIdea(idea: Idea, callback: (String) -> Unit) {
    val doc: CollectionReference = getDB().collection("Ideas")
    var map = HashMap<String, Any>()
    map.put("Name", idea.getTitle())
    map.put("Description", idea.getDesc())
    map.put("Collaborators", idea.getCollaborators())
    map.put("Tags", idea.getTags())
    map.put("Owner", USERNAME)

    // log
    var log_al = ArrayList<String>()
    log_al.add(genLogStr(USERNAME, "create", "idea", idea.getTitle()))
    map.put("Log", log_al)

    val ideaCollabs = idea.getCollaborators()
    ideaCollabs.add(USERNAME)

    var ideaRef = ""
    getDB().collection("Ideas").add(map)
            .addOnSuccessListener {
                ideaRef = it.id
                println("Success" + it.id)

                for (user in ideaCollabs) {
                    val priList = ArrayList<String>()
                    priList.add(ideaRef)
                    getDB().collection("Users").document(user)
                            .update("Ideas_Owned", FieldValue.arrayUnion(ideaRef))
                            .addOnSuccessListener {
                                println("Added ID to $user")
                            }
                            .addOnFailureListener {
                                println("Fail! ID not added to User")
                            }
                    getDB().collection("Users").document(user).get()
                            .addOnSuccessListener {
                                if(it["Priority"]!= null){
                                    priList.addAll(it["Priority"] as Collection<String>)
                                    getDB().collection("Users").document(user).update("Priority",priList)
                                            .addOnSuccessListener {
                                                println("Updated Priority")
                                            }
                                            .addOnFailureListener {
                                                println("Fail!")
                                            }
                                }
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
                        if (temp.toLowerCase().contains(query.toLowerCase())) {
                            // match
                            searchIdeas.add("" +document.data["Name"] + "-"+document.id)
                        } else {
                            if(document.data["Tags"] != null) {
                                val list = document.data["Tags"] as ArrayList<String>

                                for (l in list) {
                                    if(l.toLowerCase().contains(query.toLowerCase())) {
                                        //tag matched
                                        searchIdeas.add("" +document.data["Name"] + "-"+document.id)
                                        break
                                    }

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

fun setUserDispName(disp : String, callback: (Boolean) -> Unit) {
    val map = HashMap<String,Any>()
    map.put("email", USERNAME)
    map.put("name",disp)
    map.put("Priority",ArrayList<String>())
    map.put("Ideas_Owned",ArrayList<String>())

    getDB().collection("Users").document(USERNAME).set(map)
            .addOnSuccessListener {
                println("Successfully set display name")
                callback(true)
            }
            .addOnFailureListener {
                println(USERNAME + "Fail!")
            }
}

fun getIdeasByID(key: String, callback: (String) -> Unit) {

    getDB().collection("Ideas").document(key).get()
            .addOnSuccessListener { idea ->

                val ideaInfo = "" + idea["Name"] + "-"+ key

                callback(ideaInfo)
            }
            .addOnFailureListener {
                println("Failed to find Idea with $key")
            }

}

fun getIdeasByPriority(callback: (ArrayList<String>) -> Unit){
    val ideakeys = ArrayList<String>()
    getDB().collection("Users").document(USERNAME).get()
            .addOnSuccessListener {
                ideakeys.addAll(it["Priority"] as Collection<String>)
                println(ideakeys)
                callback(ideakeys)
            }
            .addOnFailureListener {
                println("Failure")
            }
}

fun setPriority(porder: ArrayList<String>) {
    for (i in porder) {
        getDB().collection("Users").document(USERNAME).update("Priority", FieldValue.arrayRemove(i))
    }
    for (i in porder) {
        println(i)
        getDB().collection("Users").document(USERNAME)
                .update("Priority", FieldValue.arrayUnion(i))
                .addOnSuccessListener {
                    println("added")
                }
                .addOnFailureListener {
                    println("fail")
                }
    }

}
//    getDB().collection("Users").document("Priority")
//            .update("Priority", FieldValue.arrayUnion(str))
//    println(str)

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