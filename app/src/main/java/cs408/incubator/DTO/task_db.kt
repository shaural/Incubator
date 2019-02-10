package cs408.incubator.DTO

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlin.reflect.KFunction1

val settings = FirebaseFirestoreSettings.Builder()
        .build()
val USERNAME = "csangphe"

fun getDB(): FirebaseFirestore {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    db.setFirestoreSettings(settings)
    return db
}