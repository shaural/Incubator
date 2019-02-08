package firestoreLibrary

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

val settings = FirebaseFirestoreSettings.Builder()
        .build()

fun getDB(): FirebaseFirestore {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    db.setFirestoreSettings(settings)
    return db
}